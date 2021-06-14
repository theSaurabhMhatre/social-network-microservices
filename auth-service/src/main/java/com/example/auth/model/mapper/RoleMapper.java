package com.example.auth.model.mapper;

import com.example.auth.model.constant.ERole;
import com.example.auth.model.entity.Role;
import com.example.data.model.dto.auth.RoleDto;

public class RoleMapper {

    public static RoleDto toRoleDto(Role role) {
        return RoleDto
                .builder()
                .id(role.getId())
                .role(role.getRole().toString())
                .build();
    }

    public static Role toRole(RoleDto roleDto) {
        return Role
                .builder()
                .id(roleDto.getId())
                .role(ERole.valueOf(roleDto.getRole()))
                .build();
    }

}
