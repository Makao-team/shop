package kr.co.shop.makao.component;

import io.jsonwebtoken.SignatureAlgorithm;

public class TestJwtAlgorithmProviderImpl implements JwtAlgorithmProvider {
    @Override
    public SignatureAlgorithm provide() {
        return SignatureAlgorithm.HS256;
    }
}
