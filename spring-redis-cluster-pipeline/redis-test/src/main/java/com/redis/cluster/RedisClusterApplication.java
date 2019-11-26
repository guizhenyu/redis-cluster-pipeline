package com.redis.cluster;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;
import java.util.List;

/**
 * description: RedisClusterApplication
 * date: 2019/11/20 6:12 下午
 * author: guizhenyu
 */
@SpringBootApplication
//@EnableCaching
//@EnableScheduling
@ComponentScan(basePackages = {"com.redis.cluster"})
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
public class RedisClusterApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedisClusterApplication.class, args);
    }




//    @Bean
//    public Docket createRESTAPI(){
//        List<Parameter> parameters = new ArrayList<>();
//        parameters.add(headerParam("Authorization", "令牌", ""));
//        parameters.add(headerParam("X-Accept-Locale", "语言", "zh_CN"));
//        return new Docket(DocumentationType.SWAGGER_2)
////                .host(host)
//                .apiInfo(apiInfo())
//                .enable(true)
//                .select()
//                .apis(RequestHandlerSelectors.basePackage("com.redis.cluster.controller"))
//                .paths(PathSelectors.any())
//                .build();
////                .globalOperationParameters(parameters)
////                .directModelSubstitute(LocalDate.class, String.class)
////                .genericModelSubstitutes(ResponseEntity.class)
////                .useDefaultResponseMessages(false);
//    }
//
//
//    private ApiInfo apiInfo(){
//        return new ApiInfoBuilder()
//                .title("Redis cluster Api")
//                .description("Redis cluster Api")
//                .contact(new Contact("guizhenyu", "", "country__man@163.com"))
//                .version("1.0")
//                .build();
//    }
//
//
//    private Parameter headerParam(String name, String description, String defaultVal){
//        return new ParameterBuilder()
//                .name(name)
//                .description(description)
//                .defaultValue(defaultVal)
//                .parameterType("header")
//                .required(false)
//                .build();
//    }

}
