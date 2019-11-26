package com.redis.cluster.common.swagger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * description: SwaggerConfig
 * date: 2019/11/20 4:42 下午
 * author: guizhenyu
 */

@Configuration
@EnableSwagger2
public class SwaggerConf {
    // 访问地址： http://localhost:9666/swagger-ui.html
    @Bean
    public Docket createRESTAPI(){
//        List<Parameter> parameters = new ArrayList<>();
//        parameters.add(headerParam("Authorization", "令牌", ""));
//        parameters.add(headerParam("X-Accept-Locale", "语言", "zh_CN"));
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .enable(true)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.redis.cluster.controller"))
                .paths(PathSelectors.any())
                .build();
//                .globalOperationParameters(parameters)
//                .directModelSubstitute(LocalDate.class, String.class)
//                .genericModelSubstitutes(ResponseEntity.class)
//                .useDefaultResponseMessages(false);
    }


    private ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                .title("Redis cluster Api")
                .description("Redis cluster Api")
                .contact(new Contact("guizhenyu", "", "country__man@163.com"))
                .version("1.0")
                .build();
    }


    private Parameter headerParam(String name, String description, String defaultVal){
        return new ParameterBuilder()
                .name(name)
                .description(description)
                .defaultValue(defaultVal)
                .parameterType("header")
                .required(false)
                .build();
    }
}
