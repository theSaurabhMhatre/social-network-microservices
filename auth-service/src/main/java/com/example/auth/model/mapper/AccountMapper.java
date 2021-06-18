package com.example.auth.model.mapper;

import com.example.auth.model.entity.Account;
import com.example.generic.model.dto.auth.AccountDto;

import java.util.stream.Collectors;

public class AccountMapper {

    public static AccountDto toAccountDto(Account account) {
        return AccountDto
                .builder()
                .id(account.getId())
                .handle(account.getHandle())
                .password(account.getPassword())
                .roles(account
                        .getRoles()
                        .stream()
                        .map(RoleMapper::toRoleDto)
                        .collect(Collectors.toSet()))
                .build();
    }

    public static Account toAccount(AccountDto accountDto) {
        return Account
                .builder()
                .id(accountDto.getId())
                .handle(accountDto.getHandle())
                .password(accountDto.getPassword())
                .roles(accountDto
                        .getRoles()
                        .stream()
                        .map(RoleMapper::toRole)
                        .collect(Collectors.toSet()))
                .build();
    }

}
