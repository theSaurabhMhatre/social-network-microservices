package com.example.auth.model.dto;

import lombok.Data;

@Data
public class AuthDto {

    private String token;

    private UserDto userDto;

}
