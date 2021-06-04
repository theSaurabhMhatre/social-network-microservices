package com.example.auth.service.concrete;

import com.example.auth.model.constant.ERole;
import com.example.auth.model.entity.Role;
import com.example.auth.model.mapper.RoleMapper;
import com.example.auth.repository.RoleRepository;
import com.example.auth.service.blueprint.IRoleService;
import com.example.data.model.constant.AuthConstants;
import com.example.data.model.dto.auth.RoleDto;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class RoleService
        implements IRoleService {

    private RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public RoleDto getDefaultRole() {
        Role role = roleRepository
                .findByRole(ERole.valueOf(AuthConstants.DEFAULT_ROLE));
        return RoleMapper.toRoleDto(role);
    }

}
