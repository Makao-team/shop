package kr.co.shop.makao.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan("kr.co.shop.makao.entity")
@EnableJpaRepositories("kr.co.shop.makao.repository")
@EnableJpaAuditing
public @interface EnableCommonJpa {
}
