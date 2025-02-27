package kr.co.shop.makao.component;

import com.auth0.jwt.algorithms.Algorithm;

public class TestJwtAlgorithmProviderImpl implements JwtAlgorithmProvider {
    @Override
    public Algorithm provideHmacSha(String secret) {
        return Algorithm.none();
    }
}
