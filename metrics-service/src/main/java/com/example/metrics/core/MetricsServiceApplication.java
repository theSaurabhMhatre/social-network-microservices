package com.example.metrics.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;

@EnableEurekaClient
@SpringBootApplication
@EnableHystrixDashboard
public class MetricsServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MetricsServiceApplication.class, args);
    }

}
