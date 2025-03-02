package kr.co.shop.makao.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import kr.co.shop.makao.response.CommonException;

import java.util.Arrays;

public enum ProductStatus {
    ACTIVE("active"),
    PENDING("pending");

    private final String value;

    ProductStatus(String value) {
        this.value = value;
    }

    @JsonCreator
    public static ProductStatus fromValue(String status) {
        return Arrays.stream(values())
                .filter(productStatus -> productStatus.value.equals(status))
                .findFirst()
                .orElseThrow(() -> CommonException.BAD_REQUEST.toException("INVALID_PRODUCT_STATUS"));
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}