package com.order.service;

import com.inventory.dto.InventoryDTO;
import com.order.common.ErrorOrderResponse;
import com.order.common.OrderResponse;
import com.order.common.SuccessOrderResponse;
import com.order.model.Orders;
import com.order.dto.OrderDTO;
import com.order.repo.OrderRepo;
import com.product.dto.ProductDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

@Service
@Transactional
public class OrderService {

    private final WebClient inventoryWebClient;

    private final WebClient productWebClient;

    private final OrderRepo orderRepo;

    private final ModelMapper modelMapper;

    public OrderService(WebClient inventoryWebClient, WebClient productWebClient, OrderRepo orderRepo, ModelMapper modelMapper) {
        this.inventoryWebClient = inventoryWebClient;
        this.productWebClient = productWebClient;
        this.orderRepo = orderRepo;
        this.modelMapper = modelMapper;
    }

    public List<OrderDTO> getAllOrders() {
        List<Orders>orderList = orderRepo.findAll();
        return modelMapper.map(orderList, new TypeToken<List<OrderDTO>>(){}.getType());
    }

    public OrderResponse saveOrder(OrderDTO orderDTO) {
        Integer itemId = orderDTO.itemId();

        try{
            InventoryDTO inventoryResponse = inventoryWebClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/item/{itemId}").build(itemId))
                    .retrieve()
                    .bodyToMono(InventoryDTO.class)
                    .block();

            assert inventoryResponse != null;
            Integer productId = inventoryResponse.productId();

            ProductDTO productResponse = productWebClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/product/{productId}").build(productId))
                    .retrieve()
                    .bodyToMono(ProductDTO.class)
                    .block();

            assert productResponse != null;

            if(inventoryResponse.quantity() > 0){
                if(productResponse.forSale() == 1){
                    orderRepo.save(modelMapper.map(orderDTO, Orders.class));
                } else {
                    return new ErrorOrderResponse("This item is not for sale");
                }
                return new SuccessOrderResponse(orderDTO);
            } else {
                return new ErrorOrderResponse("Item not available, please try later");
            }

        } catch (WebClientResponseException e){
            if(e.getStatusCode().is5xxServerError()){
                return new ErrorOrderResponse("Item not found");
            }
        }
        return null;
    }

    public OrderDTO updateOrder(OrderDTO OrderDTO) {
        orderRepo.save(modelMapper.map(OrderDTO, Orders.class));
        return OrderDTO;
    }

    public String deleteOrder(Integer orderId) {
        orderRepo.deleteById(orderId);
        return "Order deleted";
    }

    public OrderDTO getOrderById(Integer orderId) {
        Orders order = orderRepo.getOrderById(orderId);
        return modelMapper.map(order, OrderDTO.class);
    }
}
