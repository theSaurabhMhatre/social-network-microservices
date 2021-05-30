package com.example.user.config.security;

import com.example.commons.model.response.Response;
import com.example.commons.model.response.ResponseError;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

public class SecurityHandlers {

    public static void handle(HttpServletResponse httpServletResponse,
                              Response<ResponseError> response,
                              Exception exception) throws IOException, ServletException {
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

    @Component
    public static class AuthenticationEntryPointHandler implements AuthenticationEntryPoint {

        @Override
        public void commence(HttpServletRequest httpServletRequest,
                             HttpServletResponse httpServletResponse,
                             AuthenticationException e) throws IOException, ServletException {
            Response<ResponseError> response = Response.unauthorized();
            SecurityHandlers.handle(httpServletResponse, response, e);
        }

    }

    @Component
    public static class CustomAccessDeniedHandler implements AccessDeniedHandler {

        @Override
        public void handle(HttpServletRequest httpServletRequest,
                           HttpServletResponse httpServletResponse,
                           AccessDeniedException e) throws IOException, ServletException {
            Response<ResponseError> response = Response.forbidden();
            SecurityHandlers.handle(httpServletResponse, response, e);
        }

    }

}
