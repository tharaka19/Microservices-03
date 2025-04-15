package com.order.dto;

public record OrderDTO(
         int id,
        int itemId,
        String orderDate,
        int amount) {
}
