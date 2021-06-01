package com.example.auth.model.mapper;

import com.example.auth.model.entity.Role;
import com.example.data.model.dto.auth.RoleDto;

public class RoleMapper {

    public static RoleDto toRoleDto(Role role) {
        return RoleDto.builder()
                .role(role.getRole().toString())
                .build();
    }

}
