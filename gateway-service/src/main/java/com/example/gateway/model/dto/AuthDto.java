package com.example.gateway.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthDto {

    private String token;

    private Map<String, Object> claims;

    private UserDto userDto;

}
