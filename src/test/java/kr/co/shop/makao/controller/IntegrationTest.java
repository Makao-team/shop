package kr.co.shop.makao.controller;

import io.restassured.RestAssured;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class IntegrationTest {
    @LocalServerPort
    protected int port;

    @Autowired
    protected EntityManager em;

    @Autowired
    protected TransactionTemplate transactionTemplate;

    @BeforeAll
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
    }
}
