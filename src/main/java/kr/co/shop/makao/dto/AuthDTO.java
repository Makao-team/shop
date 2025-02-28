package kr.co.shop.makao.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

public record AuthDTO() {
    @Builder
    public record TokenReissueRequest(
            @NotBlank(message = "INVALID_REFRESH_TOKEN")
            String refreshToken
    ) {
    }

    @Builder
    public record TokenReissueResponse(String accessToken, String refreshToken) {
    }

    @Builder
    public record TokenIssueResponse(String accessToken, String refreshToken) {
    }
}