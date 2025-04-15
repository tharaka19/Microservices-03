package com.product.dto;

public record ProductDTO(
        int id,
        int productId,
        String productName,
        String description,
        int forSale) {
}
