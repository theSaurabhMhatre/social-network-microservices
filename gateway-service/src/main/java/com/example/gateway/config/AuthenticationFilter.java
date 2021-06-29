package com.example.gateway.config;

import com.example.gateway.client.AuthClient;
import com.example.gateway.utility.MessageUtils;
import com.example.generic.model.dto.auth.AccountDto;
import com.example.generic.model.response.Response;
import com.example.generic.model.response.Response.Status;
import com.example.generic.model.response.ResponseError;
import com.example.generic.utility.ConversionUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.example.generic.model.constant.AuthConstants.ACCOUNT_DATA;
import static com.example.generic.model.constant.AuthConstants.AUTHORIZATION_HEADER;
import static com.example.generic.model.constant.AuthConstants.AUTH_REALM;

@Component
@SuppressWarnings("unchecked")
public class AuthenticationFilter
        extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final ObjectProvider<AuthClient> authClient;

    private final RouteValidator routeValidator;

    @Autowired
    public AuthenticationFilter(
            ObjectProvider<AuthClient> authClient,
            RouteValidator routeValidator) {
        super(Config.class);
        this.authClient = authClient;
        this.routeValidator = routeValidator;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            if (routeValidator.isUnsecured.test(request)) return chain.filter(exchange);
            String authorizationHeader = getHeader(request, AUTHORIZATION_HEADER);
            Boolean authDataAbsent =
                    StringUtils.isEmpty(authorizationHeader) ||
                    !(authorizationHeader.startsWith(AUTH_REALM));
            if (authDataAbsent) return handle(
                    exchange.getResponse(), MessageUtils.getMessage("validation.error.auth.missing"));
            String token = authorizationHeader.substring(AUTH_REALM.length());
            AccountDto accountDto = validateRequest(token);
            if (ObjectUtils.isEmpty(accountDto)) return handle(
                    exchange.getResponse(), MessageUtils.getMessage("validation.error.auth.invalid"));
            addAccountDataHeader(exchange, accountDto);
            return chain.filter(exchange);
        };
    }

    private String getHeader(ServerHttpRequest request, String key) {
        List<String> values = request.getHeaders().getOrEmpty(key);
        if (!values.isEmpty()) return values.get(0);
        return null;
    }

    private AccountDto validateRequest(String token) {
        Response<AccountDto> response = authClient
                .getIfAvailable()
                .validate(token);
        if (response.getStatus().equals(Status.OK)) return response.getData();
        return null;
    }

    private Mono<Void> handle(ServerHttpResponse serverResponse, String message) {
        ResponseError error = ResponseError
                .builder()
                .message(message)
                .build();
        Response<ResponseError> response = Response.unauthenticated();
        response.setData(error);
        serverResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
        DataBuffer buffer = serverResponse
                .bufferFactory()
                .wrap(ConversionUtils.convertToBytes(response));
        return serverResponse.writeWith(Mono.just(buffer));
    }

    private void addAccountDataHeader(ServerWebExchange exchange, AccountDto accountDto) {
        String accountContent = ConversionUtils.serialize(accountDto);
        exchange.getRequest()
                .mutate()
                .header(ACCOUNT_DATA, accountContent)
                .build();
    }

    public static class Config {

    }

}
