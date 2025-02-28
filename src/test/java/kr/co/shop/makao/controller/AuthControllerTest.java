package kr.co.shop.makao.controller;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.restassured.http.ContentType;
import kr.co.shop.makao.component.JwtAlgorithmProvider;
import kr.co.shop.makao.config.AuthProperties;
import kr.co.shop.makao.config.PostgreInitializer;
import kr.co.shop.makao.dto.AuthDTO;
import kr.co.shop.makao.enums.UserRole;
import kr.co.shop.makao.util.RandomString;
import kr.co.shop.makao.util.StringEncoder;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;
import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.equalTo;

@ContextConfiguration(initializers = PostgreInitializer.class)
class AuthControllerTest extends IntegrationTest {
    @Autowired
    private JwtAlgorithmProvider jwtAlgorithmProvider;
    @Autowired
    private AuthProperties authProperties;

    private void insertUser(String name, String email, String phoneNumber, String password, UserRole role) {
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

    private String createRandomEmail() {
        return RandomString.generateEngDigit(30) + "@example.com";
    }

    private String createRandomPhoneNumber() {
        return "010-" + new Random().nextInt(10000) + "-" + new Random().nextInt(10000);
    }

    private AuthDTO.SignUpRequest createRequest(String email, String phoneNumber) {
        return AuthDTO.SignUpRequest.builder()
                .name("name")
                .email(email)
                .phoneNumber(phoneNumber)
                .password("password123!")
                .role(UserRole.CUSTOMER)
                .build();
    }

    @Nested
    class signUp {
        @Test
        void signUp_성공() {
            given().contentType(ContentType.JSON)
                    .body(createRequest(createRandomEmail(), createRandomPhoneNumber()))
                    .when()
                    .post("/auth/sign-up")
                    .then()
                    .statusCode(200)
                    .body("message", equalTo("OK"))
                    .body("data", equalTo(null));
        }

        @Test
        void signUp_실패_이메일_중복() {
            var email = createRandomEmail();

            given().contentType(ContentType.JSON)
                    .body(createRequest(email, createRandomPhoneNumber()))
                    .when()
                    .post("/auth/sign-up")
                    .then()
                    .statusCode(200)
                    .body("message", equalTo("OK"))
                    .body("data", equalTo(null));


            given().contentType(ContentType.JSON)
                    .body(createRequest(email, createRandomPhoneNumber()))
                    .when()
                    .post("/auth/sign-up")
                    .then()
                    .statusCode(400)
                    .body("message", equalTo("EMAIL_DUPLICATED"))
                    .body("data", equalTo(null));
        }

        @Test
        void signUp_실패_전화번호_중복() {
            var phoneNumber = createRandomPhoneNumber();

            given().contentType(ContentType.JSON)
                    .body(createRequest(createRandomEmail(), phoneNumber))
                    .when()
                    .post("/auth/sign-up")
                    .then()
                    .statusCode(200)
                    .body("message", equalTo("OK"))
                    .body("data", equalTo(null));


            given().contentType(ContentType.JSON)
                    .body(createRequest(createRandomEmail(), phoneNumber))
                    .when()
                    .post("/auth/sign-up")
                    .then()
                    .statusCode(400)
                    .body("message", equalTo("PHONE_NUMBER_DUPLICATED"))
                    .body("data", equalTo(null));
        }

        @Test
        void signUp_실패_비밀번호_형식_오류() {
            var request = AuthDTO.SignUpRequest.builder()
                    .name("name")
                    .email("test@example.com")
                    .phoneNumber("010-1234-5678")
                    .password("password123")
                    .role(UserRole.CUSTOMER)
                    .build();

            given().contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/auth/sign-up")
                    .then()
                    .statusCode(400)
                    .body("message", equalTo("INVALID_PASSWORD"))
                    .body("data", equalTo(null));
        }
    }

    @Nested
    class signIn {
        @Test
        void signIn_성공() {
            var dto = createRequest(createRandomEmail(), createRandomPhoneNumber());

            insertUser(dto.name(), dto.email(), dto.phoneNumber(), dto.password(), dto.role());

            given().contentType(ContentType.JSON)
                    .body(AuthDTO.SignInRequest.builder()
                            .email(dto.email())
                            .password(dto.password())
                            .build())
                    .when()
                    .post("/auth/sign-in")
                    .then()
                    .statusCode(200)
                    .body("message", equalTo("OK"))
                    .body("data.accessToken", any(String.class))
                    .body("data.refreshToken", any(String.class));
        }

        @Test
        void signIn_실패_이메일_존재하지_않음() {
            var dto = createRequest(createRandomEmail(), createRandomPhoneNumber());

            given().contentType(ContentType.JSON)
                    .body(AuthDTO.SignInRequest.builder()
                            .email(dto.email())
                            .password(dto.password())
                            .build())
                    .when()
                    .post("/auth/sign-in")
                    .then()
                    .statusCode(400)
                    .body("message", equalTo("USER_NOT_FOUND"))
                    .body("data", equalTo(null));
        }

        @Test
        void signIn_실패_비밀번호_불일치() {
            var dto = createRequest(createRandomEmail(), createRandomPhoneNumber());

            insertUser(dto.name(), dto.email(), dto.phoneNumber(), dto.password(), dto.role());

            given().contentType(ContentType.JSON)
                    .body(AuthDTO.SignInRequest.builder()
                            .email(dto.email())
                            .password("wrongpassword123!")
                            .build())
                    .when()
                    .post("/auth/sign-in")
                    .then()
                    .statusCode(400)
                    .body("message", equalTo("AUTHENTICATION_FAILED"))
                    .body("data", equalTo(null));
        }
    }

    @Nested
    class reissue {
        private String createToken(String secret, long expiration) {
            return Jwts.builder()
                    .setSubject(createRandomEmail())
                    .signWith(Keys.hmacShaKeyFor(secret.getBytes()), jwtAlgorithmProvider.provide())
                    .setExpiration(new Date(System.currentTimeMillis() + expiration))
                    .compact();
        }

        @Test
        void reissue_성공() {
            given().contentType(ContentType.JSON)
                    .body(AuthDTO.TokenReissueRequest.builder()
                            .refreshToken(createToken(authProperties.getRefreshTokenSecret(), 10000))
                            .build())
                    .when()
                    .post("/auth/token/reissue")
                    .then()
                    .statusCode(200)
                    .body("message", equalTo("OK"))
                    .body("data.accessToken", any(String.class));
        }


        @Test
        void reissue_만료_실패() {
            given().contentType(ContentType.JSON)
                    .body(AuthDTO.TokenReissueRequest.builder()
                            .refreshToken(createToken(authProperties.getRefreshTokenSecret(), -1000))
                            .build())
                    .when()
                    .post("/auth/token/reissue")
                    .then()
                    .statusCode(400)
                    .body("message", equalTo("EXPIRED_REFRESH_TOKEN"))
                    .body("data", equalTo(null));
        }

        @Test
        void reissue_잘못된_토큰_실패() {
            given().contentType(ContentType.JSON)
                    .body(AuthDTO.TokenReissueRequest.builder()
                            .refreshToken(createToken(RandomString.generateEngDigit(40), 10000))
                            .build())
                    .when()
                    .post("/auth/token/reissue")
                    .then()
                    .statusCode(400)
                    .body("message", equalTo("INVALID_REFRESH_TOKEN"))
                    .body("data", equalTo(null));
        }
    }
}