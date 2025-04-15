package com.order.common;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.order.dto.OrderDTO;
import lombok.Getter;

@Getter
public record SuccessOrderResponse(
        @JsonUnwrapped OrderDTO order) implements OrderResponse {
    public SuccessOrderResponse(OrderDTO order) {
        this.order = order;
    }
}
