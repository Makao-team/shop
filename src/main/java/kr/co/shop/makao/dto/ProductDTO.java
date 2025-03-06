package kr.co.shop.makao.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kr.co.shop.makao.enums.ProductStatus;
import kr.co.shop.makao.validation.NotBlankNullable;
import lombok.Builder;

import java.util.Optional;

public record ProductDTO() {
    @Builder
    public record SaveRequest(
            @NotBlank(message = "BLANK_NAME") String name,
            @NotBlank(message = "BLANK_DESCRIPTION") String description,
            @NotNull(message = "INVALID_PRICE") @Min(value = 0, message = "INVALID_PRICE") int price,
            @NotNull(message = "INVALID_STOCK") @Min(value = 0, message = "INVALID_STOCK") int stock,
            @NotNull(message = "BLANK_MERCHANT_ID") long merchantId) {
    }

    @Builder
    public record UpdateRequest(
            Optional<@NotBlankNullable(message = "BLANK_NAME") String> name,
            Optional<@NotBlankNullable(message = "BLANK_DESCRIPTION") String> description,
            Optional<@Min(value = 0, message = "INVALID_PRICE") Integer> price,
            Optional<@Min(value = 0, message = "INVALID_STOCK") Integer> stock,
            Optional<ProductStatus> status) {
    }
}
