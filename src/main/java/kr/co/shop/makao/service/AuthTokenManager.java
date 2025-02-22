package kr.co.shop.makao.service;

public interface AuthTokenManager {
    String createAccessToken(String subject);

    String createRefreshToken(String subject);

    String getSubjectFromAccessToken(String token);

    String getSubjectFromRefreshToken(String token);
}
