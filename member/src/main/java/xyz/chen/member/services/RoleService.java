package xyz.chen.member.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.chen.member.entity.Role;
import xyz.chen.member.repository.RoleRepository;

@Service
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;
    public void test(){
        Role  role = new Role();
        role.setRoleCode("test");
        role.setRoleName("test");
        role.setRoleDesc("test111");
        roleRepository.insert(role);
    }

}
