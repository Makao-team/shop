package kr.co.shop.makao.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kr.co.shop.makao.entity.Product;
import kr.co.shop.makao.validation.NotBlankNullable;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
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
            Optional<@Min(value = 0, message = "INVALID_STOCK") Integer> stock
    ) {
    }

    @Builder
    public record FindAllViewRequest(
            @Min(value = 0, message = "INVALID_PAGE") Integer page,
            @Min(value = 10, message = "INVALID_SIZE") Integer size,
            Filter filter, // [Error] 메시지 관리가 안됨
            String keyword
    ) {
        public FindAllViewRequest {
            if (page == null) page = 0;
            if (size == null) size = 10;
        }

        @Getter
        public enum Filter {
            name, description, merchant
        }
    }

    @Builder
    public record FindAllViewResponse(
            List<Product.View> contents,
            boolean last
    ) {
    }

    @Builder
    public record FindOneRequest(
            @NotNull(message = "INVALID_ID") long id
    ) {
    }

    @Builder
    public record FindOneDetailResponse(
            Product content
    ) {
    }

    @Builder
    public record FindOneViewResponse(
            Product.View content
    ) {
    }

    @Builder
    public record FindAllDetailRequest(
            @NotNull(message = "BLANK_MERCHANT_ID") long merchantId,
            @Min(value = 0, message = "INVALID_PAGE") Integer page,
            @Min(value = 10, message = "INVALID_SIZE") Integer size,
            FindAllDetailRequest.Filter filter, // [Error] 메시지 관리가 안됨
            String keyword
    ) {
        public FindAllDetailRequest {
            if (page == null) page = 0;
            if (size == null) size = 10;
        }

        @Getter
        public enum Filter {
            name, description
        }
    }

    @Builder
    public record FindAllDetailResponse(
            List<Product> contents,
            boolean last
    ) {
    }
}
