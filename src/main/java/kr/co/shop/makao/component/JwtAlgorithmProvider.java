package kr.co.shop.makao.component;

import com.auth0.jwt.algorithms.Algorithm;

public interface JwtAlgorithmProvider {
    /**
     * Generate a key according to the algorithm using the secret and return the algorithm
     */
    Algorithm provideHmacSha(String secret);
}
