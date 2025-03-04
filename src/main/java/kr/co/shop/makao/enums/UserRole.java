package kr.co.shop.makao.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import kr.co.shop.makao.response.CommonException;

import java.util.Arrays;

public enum UserRole {
    MERCHANT("merchant"),
    CUSTOMER("customer"),
    ADMIN("admin");

    private final String value;

    UserRole(String value) {
        this.value = value;
    }

    @JsonCreator
    public static UserRole fromValue(String role) {
        return Arrays.stream(values())
                .filter(userRole -> userRole.value.equals(role))
                .findFirst()
                .orElseThrow(() -> CommonException.BAD_REQUEST.toException("INVALID_USER_ROLE"));
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
