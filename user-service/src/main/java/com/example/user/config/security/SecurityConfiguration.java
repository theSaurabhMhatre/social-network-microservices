package com.example.user.config.security;

import com.example.user.config.filter.JWTFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.example.commons.constants.AuthConstants.ALLOWED_ROLE;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration
        extends WebSecurityConfigurerAdapter {

    private JWTFilter jwtFilter;

    private SecurityHandlers.AuthenticationEntryPointHandler authenticationHandler;

    private SecurityHandlers.CustomAccessDeniedHandler accessHandler;

    @Autowired
    public SecurityConfiguration(JWTFilter jwtFilter,
                                 SecurityHandlers.AuthenticationEntryPointHandler authenticationHandler,
                                 SecurityHandlers.CustomAccessDeniedHandler accessHandler) {
        this.jwtFilter = jwtFilter;
        this.authenticationHandler = authenticationHandler;
        this.accessHandler = accessHandler;
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
                .authenticationEntryPoint(authenticationHandler)
                .accessDeniedHandler(accessHandler)
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        httpSecurity.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
