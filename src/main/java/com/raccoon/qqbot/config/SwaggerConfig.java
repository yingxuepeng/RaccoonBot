package com.raccoon.qqbot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.sql.Timestamp;

/**
 * http://localhost:8080/swagger-ui/index.html#/
 * download ts model : https://editor.swagger.io/ -> generate-client ts-angular
 */
@Configuration
@EnableSwagger2
@Profile({"dev", "test"})
public class SwaggerConfig {

    @Bean
    public Docket createDocket() {
        ApiInfo apiInfo = new ApiInfoBuilder().title("raccoon_bot").description("raccoon_bot")
                .contact(new Contact("pyx", "", "")).version("2.0.0").build();
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo)
                .directModelSubstitute(Timestamp.class, Long.class)
                .select().apis(RequestHandlerSelectors.basePackage("com.raccoon.qqbot.controller"))
                .paths(PathSelectors.any())

                .build();
    }
}
