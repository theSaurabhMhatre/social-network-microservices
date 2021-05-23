package com.example.gateway.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.ComponentScan;

@EnableHystrix
@EnableEurekaClient
@SpringBootApplication
@EntityScan(basePackages = {"com.example.gateway.*"})
@ComponentScan(basePackages = {"com.example.gateway.*"})
public class GatewayServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayServiceApplication.class, args);
    }

}
