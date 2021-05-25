package com.example.auth.model.entity;

import lombok.Data;

@Data
public class User {

    private String id;

    private String handle;

    private String password;

    private String role;

}
