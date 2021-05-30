package com.example.gateway.config;

import com.example.commons.model.response.Response;
import com.example.commons.utility.ConversionUtils;
import com.example.gateway.model.dto.UserDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

import static com.example.commons.constants.AuthConstants.AUTHORIZATION_HEADER;
import static com.example.commons.constants.AuthConstants.AUTH_REALM;
import static com.example.commons.constants.AuthConstants.USER_DATA;

@Component
public class AuthenticationFilter
        extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final RestTemplate restTemplate;

    private final RouteValidator routeValidator;

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
                String authorizationHeader = null;
                if (StringUtils.isEmpty(authorizationHeader = getHeader(request, AUTHORIZATION_HEADER)) ||
                        !(authorizationHeader.startsWith(AUTH_REALM))) {
                    return onError(exchange, HttpStatus.UNAUTHORIZED, AUTHORIZATION_HEADER_MISSING);
                }
                String token = authorizationHeader.substring(AUTH_REALM.length());
                try {
                    UserDto userDto = validateRequest(token, request.getHeaders());
                    if (ObjectUtils.isEmpty(userDto)) {
                        return onError(exchange, HttpStatus.UNAUTHORIZED, AUTHORIZATION_HEADER_INVALID);
                    }
                    addUserDataHeader(exchange, userDto);
                } catch (IllegalArgumentException e) {
                    // TODO: handle this
                } catch (JsonProcessingException e) {
                    // TODO: handle this
                }
            }
            return chain.filter(exchange);
        };
    }

    private String getHeader(ServerHttpRequest request,
                             String key) {
        return request.getHeaders()
                .getOrEmpty(key)
                .get(0);
    }

    private UserDto validateRequest(String token,
                                    HttpHeaders headers) throws IllegalArgumentException {
        String url = "http://auth-service/v1/auth/validate/{token}";
        Map<String, String> variables = Map.of("token", token);
        Response<UserDto> response = restTemplate
                .exchange(url, HttpMethod.GET, new HttpEntity<>(headers), Response.class, variables)
                .getBody();
        if (response.getStatus().equals(HttpStatus.OK)) {
            return ConversionUtils.convert(response.getData(), UserDto.class);
        }
        return null;
    }

    private Mono<Void> onError(ServerWebExchange exchange,
                               HttpStatus httpStatus,
                               String error) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }

    private void addUserDataHeader(ServerWebExchange exchange,
                                   UserDto userDto) throws JsonProcessingException {
        String userContents = ConversionUtils.serialize(userDto);
        exchange.getRequest()
                .mutate()
                .header(USER_DATA, userContents)
                .build();
    }

    public static class Config {

    }

}
