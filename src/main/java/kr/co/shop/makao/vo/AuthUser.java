package kr.co.shop.makao.vo;

import lombok.Builder;

@Builder
public record AuthUser(String subject, String role) {
}