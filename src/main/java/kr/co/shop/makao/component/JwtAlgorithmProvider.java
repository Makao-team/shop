package kr.co.shop.makao.component;

import io.jsonwebtoken.SignatureAlgorithm;

public interface JwtAlgorithmProvider {
    SignatureAlgorithm provide();
}
