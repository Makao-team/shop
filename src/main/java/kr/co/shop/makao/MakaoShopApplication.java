package kr.co.shop.makao;

import kr.co.shop.makao.config.EnableCommonJpa;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableCommonJpa
@SpringBootApplication
public class MakaoShopApplication {
    public static void main(String[] args) {
        SpringApplication.run(MakaoShopApplication.class, args);
    }
}
