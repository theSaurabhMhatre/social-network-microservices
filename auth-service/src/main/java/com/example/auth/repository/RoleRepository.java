package com.example.auth.repository;

import com.example.auth.model.constant.ERole;
import com.example.auth.model.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository
        extends JpaRepository<Role, String> {

    Role findByRole(ERole role);

}
