package kr.co.shop.makao.vo;

import lombok.Builder;

@Builder
public record AuthUser(long id, String email, String role) {
}