package com.order.common;

import lombok.Getter;

@Getter
public record ErrorOrderResponse(
        String errorMessage) implements OrderResponse {
}
