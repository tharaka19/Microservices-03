package com.inventory.dto;

public record InventoryDTO(
        int id,
        int itemId,
        int productId,
        int quantity) {
}
