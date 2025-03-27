package kr.co.shop.makao.component;

import com.auth0.jwt.algorithms.Algorithm;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class JwtAlgorithmProviderImpl implements JwtAlgorithmProvider {
    @Override
    public Algorithm provideHmacSha(String secret) {
        SecretKeySpec secretKeySpec = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8),
                "HmacSHA256"
        );
        return Algorithm.HMAC256(secretKeySpec.getEncoded());
    }
}