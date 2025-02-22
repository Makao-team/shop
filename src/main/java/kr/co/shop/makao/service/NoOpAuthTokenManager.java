package kr.co.shop.makao.service;

public class NoOpAuthTokenManager implements AuthTokenManager {
    @Override
    public String createAccessToken(String subject) {
        return "";
    }

    @Override
    public String createRefreshToken(String subject) {
        return "";
    }

    @Override
    public String getSubjectFromAccessToken(String token) {
        return null;
    }

    @Override
    public String getSubjectFromRefreshToken(String token) {
        return null;
    }
}
