package kr.co.shop.makao.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import kr.co.shop.makao.entity.User;
import kr.co.shop.makao.enums.UserRole;
import kr.co.shop.makao.util.RandomString;
import kr.co.shop.makao.util.StringEncoder;

import java.util.Date;
import java.util.Random;

public abstract class BaseIntegrationTest extends IntegrationTest {
    protected void insertUser(String name, String email, String phoneNumber, String password, UserRole role) {
        transactionTemplate.execute(status -> {
            em.createQuery("INSERT INTO user (name, email, phoneNumber, password, role) VALUES (:name, :email, :phoneNumber, :password, :role)")
                    .setParameter("name", name)
                    .setParameter("email", email)
                    .setParameter("phoneNumber", phoneNumber)
                    .setParameter("password", StringEncoder.encode(password))
                    .setParameter("role", role)
                    .executeUpdate();
            return null;
        });
    }

    protected User findUserByEmail(String email) {
        return transactionTemplate.execute(
                status -> em.createQuery("SELECT u FROM user u WHERE u.email = :email", User.class)
                        .setParameter("email", email)
                        .getSingleResult()
        );
    }

    protected String createRandomEmail() {
        return RandomString.generateEngDigit(30) + "@example.com";
    }

    protected String createRandomPhoneNumber() {
        return "010-" + new Random().nextInt(10000) + "-" + new Random().nextInt(10000);
    }

    protected String createToken(Algorithm algorithm, long expiration, UserRole role, String email) {
        return JWT.create()
                .withSubject(email)
                .withClaim("role", role.getValue())
                .withExpiresAt(new Date(System.currentTimeMillis() + expiration))
                .sign(algorithm);
    }
}
