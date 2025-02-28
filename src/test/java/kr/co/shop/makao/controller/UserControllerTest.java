package kr.co.shop.makao.controller;

import io.restassured.http.ContentType;
import kr.co.shop.makao.config.PostgreInitializer;
import kr.co.shop.makao.dto.UserDTO;
import kr.co.shop.makao.enums.UserRole;
import kr.co.shop.makao.util.RandomString;
import kr.co.shop.makao.util.StringEncoder;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;

import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.equalTo;

@ContextConfiguration(initializers = PostgreInitializer.class)
class UserControllerTest extends IntegrationTest {
    private String createRandomEmail() {
        return RandomString.generateEngDigit(30) + "@example.com";
    }

    private String createRandomPhoneNumber() {
        return "010-" + new Random().nextInt(10000) + "-" + new Random().nextInt(10000);
    }

    private UserDTO.SaveRequest createRequest(String email, String phoneNumber) {
        return UserDTO.SaveRequest.builder()
                .name("name")
                .email(email)
                .phoneNumber(phoneNumber)
                .password("password123!")
                .role(UserRole.CUSTOMER)
                .build();
    }

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


    @Nested
    class save {
        @Test
        void save_성공() {
            given().contentType(ContentType.JSON)
                    .body(createRequest(createRandomEmail(), createRandomPhoneNumber()))
                    .when()
                    .post("/users/")
                    .then()
                    .statusCode(200)
                    .body("message", equalTo("OK"))
                    .body("data", equalTo(null));
        }

        @Test
        void save_실패_이메일_중복() {
            var email = createRandomEmail();

            given().contentType(ContentType.JSON)
                    .body(createRequest(email, createRandomPhoneNumber()))
                    .when()
                    .post("/users/")
                    .then()
                    .statusCode(200)
                    .body("message", equalTo("OK"))
                    .body("data", equalTo(null));


            given().contentType(ContentType.JSON)
                    .body(createRequest(email, createRandomPhoneNumber()))
                    .when()
                    .post("/users/")
                    .then()
                    .statusCode(400)
                    .body("message", equalTo("EMAIL_DUPLICATED"))
                    .body("data", equalTo(null));
        }

        @Test
        void save_실패_전화번호_중복() {
            var phoneNumber = createRandomPhoneNumber();

            given().contentType(ContentType.JSON)
                    .body(createRequest(createRandomEmail(), phoneNumber))
                    .when()
                    .post("/users/")
                    .then()
                    .statusCode(200)
                    .body("message", equalTo("OK"))
                    .body("data", equalTo(null));


            given().contentType(ContentType.JSON)
                    .body(createRequest(createRandomEmail(), phoneNumber))
                    .when()
                    .post("/users/")
                    .then()
                    .statusCode(400)
                    .body("message", equalTo("PHONE_NUMBER_DUPLICATED"))
                    .body("data", equalTo(null));
        }

        @Test
        void signUp_실패_비밀번호_형식_오류() {
            var request = UserDTO.SaveRequest.builder()
                    .name("name")
                    .email("test@example.com")
                    .phoneNumber("010-1234-5678")
                    .password("password123")
                    .role(UserRole.CUSTOMER)
                    .build();

            given().contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/users/")
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
                    .body(UserDTO.SignInRequest.builder()
                            .email(dto.email())
                            .password(dto.password())
                            .build())
                    .when()
                    .post("/users/sign-in")
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
                    .body(UserDTO.SignInRequest.builder()
                            .email(dto.email())
                            .password(dto.password())
                            .build())
                    .when()
                    .post("/users/sign-in")
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
                    .body(UserDTO.SignInRequest.builder()
                            .email(dto.email())
                            .password("wrongpassword123!")
                            .build())
                    .when()
                    .post("/users/sign-in")
                    .then()
                    .statusCode(400)
                    .body("message", equalTo("AUTHENTICATION_FAILED"))
                    .body("data", equalTo(null));
        }
    }
}