package com.example.post.logging;

import com.example.generic.component.logging.LoggingAdvice;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ServiceLoggingAdvice
        extends LoggingAdvice {

    @Autowired
    public ServiceLoggingAdvice(
            ObjectMapper mapper) {
        super(mapper);
    }

    @Override
    @Pointcut(value = "execution(public * com.example.post.service.concrete.*.*(..))")
    public void pointcut() {

    }

}
