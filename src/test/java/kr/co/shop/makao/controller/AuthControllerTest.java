package kr.co.shop.makao.controller;

import io.restassured.http.ContentType;
import kr.co.shop.makao.config.PostgreInitializer;
import kr.co.shop.makao.dto.AuthDTO;
import kr.co.shop.makao.enums.UserRole;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;

import java.util.Random;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@ContextConfiguration(initializers = PostgreInitializer.class)
class AuthControllerTest extends IntegrationTest {
    private String createRandomEmail() {
        return (new Random()).ints(10, 48, 122)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .mapToObj(c -> String.valueOf((char) c))
                .collect(Collectors.joining()) + "@example.com";
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
}