package com.example.auth.core;

import com.example.auth.model.constant.ERole;
import com.example.auth.model.entity.Role;
import com.example.auth.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@Component
public class DataLoader {

    private RoleRepository roleRepository;

    @Autowired
    public DataLoader(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @PostConstruct
    private void createRoles() {
        if (roleRepository.findAll().size() == 0) {
            Arrays.stream(ERole.values()).forEach((role) -> {
                Role curr = Role.builder()
                        .role(role)
                        .build();
                roleRepository.save(curr);
            });
        }
    }

}
