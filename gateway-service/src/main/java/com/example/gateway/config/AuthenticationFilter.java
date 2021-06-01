package com.example.gateway.config;

import com.example.data.model.dto.auth.AccountDto;
import com.example.data.model.response.Response;
import com.example.data.utility.ConversionUtils;
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

import static com.example.data.model.constant.AuthConstants.ACCOUNT_DATA;
import static com.example.data.model.constant.AuthConstants.AUTHORIZATION_HEADER;
import static com.example.data.model.constant.AuthConstants.AUTH_REALM;

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
                    AccountDto accountDto = validateRequest(token, request.getHeaders());
                    if (ObjectUtils.isEmpty(accountDto)) {
                        return onError(exchange, HttpStatus.UNAUTHORIZED, AUTHORIZATION_HEADER_INVALID);
                    }
                    addAccountDataHeader(exchange, accountDto);
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

    private AccountDto validateRequest(String token,
                                       HttpHeaders headers) throws IllegalArgumentException {
        String url = "http://auth-service/v1/auth/validate/{token}";
        Map<String, String> variables = Map.of("token", token);
        Response<AccountDto> response = restTemplate
                .exchange(url, HttpMethod.GET, new HttpEntity<>(headers), Response.class, variables)
                .getBody();
        if (response.getStatus().equals(HttpStatus.OK)) {
            return ConversionUtils.convert(response.getData(), AccountDto.class);
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

    private void addAccountDataHeader(ServerWebExchange exchange,
                                      AccountDto accountDto) throws JsonProcessingException {
        String accountContent = ConversionUtils.serialize(accountDto);
        exchange.getRequest()
                .mutate()
                .header(ACCOUNT_DATA, accountContent)
                .build();
    }

    public static class Config {

    }

}
