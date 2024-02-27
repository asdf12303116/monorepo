package xyz.chen.member.services;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.chen.member.entity.Role;
import xyz.chen.member.entity.UserRole;
import xyz.chen.member.repository.UserRoleRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserRoleService extends ServiceImpl<UserRoleRepository, UserRole> {

    @Autowired
    private RoleService roleService;

    @Autowired
    private OAuthRoleService oAuthRoleService;


    @Transactional
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
        saveOrUpdateBatch(userRoles);
    }

    public List<Role> getUserRoles(Long userId) {
        return this.lambdaQuery().eq(UserRole::getUserId, userId)
                .list().stream().map(userRole -> new Role(userRole.getRoleName(), userRole.getRoleCode(), userRole.getRoleId())).toList();
    }

    @Transactional
    public Boolean grantRoles(Long userId, List<Long> roleIds) {
        var userRoles = lambdaQuery().eq(UserRole::getUserId, userId)
                .in(UserRole::getRoleId, roleIds)
                .list();
        var roles = roleService.getBaseMapper().selectBatchIds(roleIds);
        List<UserRole> userRoleList = new ArrayList<>();
        roles.forEach(role -> {
            UserRole userRole = userRoles.stream()
                    .filter(userRoleData -> userRoleData.getRoleId().equals(role.getId()))
                    .findFirst()
                    .orElseGet(UserRole::new);
            userRole.setUserId(userId);
            userRole.setRoleId(role.getId());
            userRole.setRoleCode(role.getRoleCode());
            userRole.setRoleName(role.getRoleName());
            userRoleList.add(userRole);
        });
        saveOrUpdateBatch(userRoleList);

        return true;
    }
}
