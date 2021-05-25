package com.example.auth.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {

    private String id;

    private String handle;

    private String password;

    private String role;

}
