package com.example.auth.model.mapper;

import com.example.auth.model.dto.UserDto;
import com.example.auth.model.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .handle(user.getHandle())
                .password(user.getPassword())
                .role(user.getRole())
                .build();
    }

}
