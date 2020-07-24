package local.socialnetwork.core.service.impl;

import local.socialnetwork.core.repository.RoleRepository;

import local.socialnetwork.core.service.RoleService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {

    private RoleRepository roleRepository;

    @Autowired
    public void setRoleRepository(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }
}