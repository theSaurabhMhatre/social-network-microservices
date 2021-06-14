package com.example.user.config.filter;

import com.example.data.component.utility.MessageUtils;
import com.example.data.model.dto.auth.AccountDto;
import com.example.data.model.dto.auth.RoleDto;
import com.example.data.model.response.Response;
import com.example.data.model.response.ResponseError;
import com.example.data.utility.ConversionUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.example.data.model.constant.AuthConstants.ACCOUNT_DATA;
import static com.example.data.model.constant.AuthConstants.AUTHORIZATION_HEADER;
import static com.example.data.model.constant.AuthConstants.MASKED_PASSWORD;

@Component
public class JWTFilter
        extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        AccountDto accountDto = getAccountFromHeader(request);
        String authHeader = request.getHeader(AUTHORIZATION_HEADER);
        Boolean authDataAbsent =
                StringUtils.isEmpty(authHeader) ||
                StringUtils.isEmpty(accountDto.getId()) ||
                ObjectUtils.isEmpty(accountDto.getRoles());
        if (authDataAbsent) handle(response, MessageUtils.getMessage("validation.error.auth.absent"));
        UsernamePasswordAuthenticationToken token = getAuthenticationToken(authHeader, accountDto);
        token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(token);
        chain.doFilter(request, response);
    }

    private AccountDto getAccountFromHeader(HttpServletRequest httpServletRequest) {
        String accountContent = httpServletRequest.getHeader(ACCOUNT_DATA);
        return ConversionUtils.deserialize(accountContent, AccountDto.class);
    }

    private UsernamePasswordAuthenticationToken getAuthenticationToken(String credentials, AccountDto accountDto) {
        List<GrantedAuthority> roles = getUserAuthorities(accountDto.getRoles());
        var userDetails = new User(accountDto.getId(), MASKED_PASSWORD, roles);
        return new UsernamePasswordAuthenticationToken(userDetails, credentials, userDetails.getAuthorities());
    }

    private List<GrantedAuthority> getUserAuthorities(Set<RoleDto> roles) {
        Set<GrantedAuthority> authorities = new HashSet<>();
        roles.forEach((role) -> authorities.add(new SimpleGrantedAuthority(role.getRole())));
        return new ArrayList<>(authorities);
    }

    private void handle(HttpServletResponse servletResponse, String message)
            throws IOException {
        Response<ResponseError> response = Response.unauthenticated();
        ResponseError error = ResponseError
                .builder()
                .message(message)
                .build();
        response.setData(error);
        OutputStream out = servletResponse.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(out, response);
        out.flush();
    }

}
