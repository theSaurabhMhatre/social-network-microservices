package com.example.auth.service.concrete;

import com.example.auth.model.constant.ERole;
import com.example.auth.model.mapper.RoleMapper;
import com.example.auth.repository.RoleRepository;
import com.example.auth.service.blueprint.IRoleService;
import com.example.data.component.utility.MessageUtils;
import com.example.data.model.constant.AuthConstants;
import com.example.data.model.dto.auth.RoleDto;
import com.example.exception.hierarchy.FetchException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
public class RoleService
        implements IRoleService {

    private final RoleRepository roleRepository;

    public RoleService(
            RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public RoleDto getDefaultRole() {
        ERole defaultRole = ERole.valueOf(AuthConstants.DEFAULT_ROLE);
        RoleDto roleDto = Optional
                .ofNullable(roleRepository.findByRole(defaultRole))
                .map(RoleMapper::toRoleDto)
                .orElseThrow(() -> fetchException("generic.error.fetch.failed"));
        return roleDto;
    }

    private FetchException fetchException(String key) {
        return new FetchException(MessageUtils.getMessage(key));
    }

}
