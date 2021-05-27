package com.example.gateway.config;

import com.example.gateway.model.dto.AuthDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class AuthenticationFilter
        extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final RestTemplate restTemplate;

    private final RouteValidator routeValidator;

    private static final String ID = "id";

    private static final String ROLE = "role";

    private static final String PASSWORD = "password";

    private static final String BEARER_REALM = "Bearer ";

    private static final String AUTHORIZATION_HEADER = "Authorization";

    private static final String AUTHORIZATION_HEADER_MISSING = "Authorization header is missing";

    private static final String AUTHORIZATION_HEADER_INVALID = "Authorization header is invalid";

    @Autowired
    public AuthenticationFilter(RestTemplate restTemplate,
                                RouteValidator routeValidator) {
        super(Config.class);
        this.restTemplate = restTemplate;
        this.routeValidator = routeValidator;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            if (routeValidator.isSecured.test(request)) {
                String authorizationHeader;
                if (isAuthMissing(request) ||
                        !(authorizationHeader = getAuthHeader(request)).startsWith(BEARER_REALM)) {
                    return onError(exchange, HttpStatus.UNAUTHORIZED, AUTHORIZATION_HEADER_MISSING);
                }
                String token = authorizationHeader.substring(BEARER_REALM.length());
                String url = String.format("http://auth-service/v1/auth/validate/%s", token);
                AuthDto authDto = restTemplate.getForObject(url, AuthDto.class);
                if (authDto == null) {
                    return onError(exchange, HttpStatus.UNAUTHORIZED, AUTHORIZATION_HEADER_INVALID);
                }
                populateRequestWithHeaders(exchange, authDto);
            }
            return chain.filter(exchange);
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange,
                               HttpStatus httpStatus,
                               String error) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }

    private String getAuthHeader(ServerHttpRequest request) {
        return request.getHeaders()
                .getOrEmpty(AUTHORIZATION_HEADER)
                .get(0);
    }

    private Boolean isAuthMissing(ServerHttpRequest request) {
        return !request.getHeaders()
                .containsKey(AUTHORIZATION_HEADER);
    }

    private void populateRequestWithHeaders(ServerWebExchange exchange,
                                            AuthDto authDto) {
        Map<String, Object> claims = authDto.getClaims();
        exchange.getRequest()
                .mutate()
                .header(ID, String.valueOf(claims.get(ID)))
                .header(ROLE, String.valueOf(claims.get(ROLE)))
                .header(PASSWORD, authDto.getUserDto().getPassword())
                .build();
    }

    public static class Config {

    }

}
