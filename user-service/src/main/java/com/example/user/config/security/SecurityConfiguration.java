package com.example.user.config.security;

import com.example.data.model.response.Response;
import com.example.data.model.response.ResponseError;
import com.example.user.config.filter.JWTFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

import static com.example.data.model.constant.AuthConstants.ALLOWED_ROLE;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration
        extends WebSecurityConfigurerAdapter {

    private JWTFilter jwtFilter;

    @Autowired
    public SecurityConfiguration(JWTFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf()
                .disable()
                .authorizeRequests()
                .antMatchers("/**")
                .hasRole(ALLOWED_ROLE)
                .anyRequest()
                .authenticated()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint((httpServletRequest, httpServletResponse, e) -> {
                    Response<ResponseError> response = Response.unauthorized();
                    handle(httpServletResponse, response, e);
                })
                .accessDeniedHandler((httpServletRequest, httpServletResponse, e) -> {
                    Response<ResponseError> response = Response.forbidden();
                    handle(httpServletResponse, response, e);
                })
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        httpSecurity.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    }

    private void handle(HttpServletResponse httpServletResponse,
                        Response<ResponseError> response,
                        Exception exception) throws IOException {
        ResponseError error = ResponseError.builder()
                .message(response.getStatus().getReasonPhrase())
                .details(exception.getMessage())
                .build();
        response.setData(error);
        OutputStream out = httpServletResponse.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(out, response);
        out.flush();
    }

}
