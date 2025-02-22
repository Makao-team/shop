package kr.co.shop.makao.config;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;

import java.io.Closeable;

public class PostgreInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext>, Closeable {
    private static final Logger logger = LoggerFactory.getLogger(PostgreInitializer.class);
    private static final String POSTGRES_IMAGE = "postgres:15";

    private static PostgreSQLContainer<?> container;

    @Override
    public void initialize(@NotNull ConfigurableApplicationContext applicationContext) {
        logger.info("Initializing Postgres test container");

        if (container == null) {
            container = new PostgreSQLContainer<>(POSTGRES_IMAGE)
                    .withDatabaseName("makao-test")
                    .withUsername("test")
                    .withPassword("test")
                    .withUrlParam("stringtype", "unspecified");
            container.start();
        }

        TestPropertyValues.of(
                "spring.datasource.url=" + container.getJdbcUrl(),
                "spring.datasource.username=" + container.getUsername(),
                "spring.datasource.password=" + container.getPassword(),
                "spring.datasource.driver-class-name=org.postgresql.Driver",
                "spring.jpa.hibernate.ddl-auto=none",
                "spring.sql.init.mode=always",
                "spring.sql.init.schema-locations=classpath:schema.sql",
                "spring.jpa.properties.hibernate.globally_quoted_identifiers=true"
        ).applyTo(applicationContext.getEnvironment());
    }

    @Override
    public void close() {
        if (container != null) {
            container.close();
            container = null;
        }
    }
}
