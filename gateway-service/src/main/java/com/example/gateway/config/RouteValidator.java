package com.example.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {

    @Value(value = "${open.routes}")
    private String openRoutes;

    private List<String> openRoutesList;

    public Predicate<ServerHttpRequest> isUnsecured = (request) -> {
        return openRoutesList.stream().anyMatch((uri) -> {
            return request.getURI().getPath().contains(uri.trim());
        });
    };

    @PostConstruct
    public void initialize() {
        openRoutesList = List.of(openRoutes.split(","));
    }

}
