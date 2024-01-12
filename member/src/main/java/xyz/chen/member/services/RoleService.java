package xyz.chen.member.services;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.chen.member.entity.Role;
import xyz.chen.member.entity.UserRole;
import xyz.chen.member.repository.RoleRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoleService extends ServiceImpl<RoleRepository, Role> {

    @Autowired
    private OAuthRoleService oAuthRoleService;

    @Autowired
    private UserRoleService userRoleService;


    public void grantUserRoles(List<String> groups, Long userId) {
        List<Role> roles = oAuthRoleService.getRolesByGroups(groups);

        List<UserRole> userRoles = new ArrayList<>();
        roles.forEach(role -> {
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(role.getId());
            userRole.setRoleCode(role.getRoleCode());
            userRole.setRoleName(role.getRoleName());
            userRoles.add(userRole);
        });
        userRoleService.saveBatch(userRoles);
    }
}
