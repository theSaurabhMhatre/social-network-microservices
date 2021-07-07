package com.example.user.config.feign;

import com.example.generic.model.dto.auth.AccountDto;
import com.example.generic.model.dto.auth.RoleDto;
import com.example.generic.utility.ConversionUtils;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;
import java.util.stream.Collectors;

import static com.example.generic.model.constant.AuthConstants.ACCOUNT_DATA;
import static com.example.generic.model.constant.AuthConstants.AUTHORIZATION_HEADER;

@Configuration
public class FeignConfiguration {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header(AUTHORIZATION_HEADER, getAuthHeader());
            requestTemplate.header(ACCOUNT_DATA, getAccountData());
        };
    }

    private String getAuthHeader() {
        return SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getCredentials()
                .toString();
    }

    private String getAccountData() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        Set<RoleDto> roles = userDetails
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .map(role -> RoleDto
                        .builder()
                        .role(role)
                        .build())
                .collect(Collectors.toSet());
        AccountDto accountDto = AccountDto
                .builder()
                .id(userDetails.getUsername())
                .password(userDetails.getPassword())
                .roles(roles)
                .build();
        return ConversionUtils.serialize(accountDto);
    }

}
