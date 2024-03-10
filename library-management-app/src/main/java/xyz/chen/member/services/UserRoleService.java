package xyz.chen.member.services;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.chen.commons.base.BaseEntity;
import xyz.chen.member.entity.Role;
import xyz.chen.member.entity.User;
import xyz.chen.member.entity.UserRole;
import xyz.chen.member.entity.dto.UserRoleSearchDto;
import xyz.chen.member.entity.dto.UserWithRole;
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

    public Page<UserRole> getUserRoles(UserRoleSearchDto userRoleSearchDto) {
        Page<UserRole> page = new Page<>(userRoleSearchDto.pageInfo().getPageNum(), userRoleSearchDto.pageInfo().getPageSize());
        return baseMapper.selectPage(page, lambdaQuery()
                .eq(NumberUtil.isValidNumber(userRoleSearchDto.id()),UserRole::getId,userRoleSearchDto.id())
                .eq(NumberUtil.isValidNumber(userRoleSearchDto.userId()),UserRole::getUserId,userRoleSearchDto.userId())
                .likeRight(StrUtil.isNotBlank(userRoleSearchDto.roleName()),UserRole::getRoleName,userRoleSearchDto.roleName())
                .likeRight(StrUtil.isNotBlank(userRoleSearchDto.roleCode()),UserRole::getRoleCode,userRoleSearchDto.roleCode())
                .getWrapper()
        );
    }

    @Transactional
    public void grantRoles(Long userId, List<Long> roleIds) {
        var userRoles = lambdaQuery().eq(UserRole::getUserId, userId)
                .in(UserRole::getRoleId, roleIds)
                .list();
        var roles = roleService.getRolesByIds(roleIds);
        List<UserRole> userRoleList = new ArrayList<>();
        roles.forEach(role -> userRoleList.add(getUserRoleInfo(userId, userRoles, role)));
        saveOrUpdateBatch(userRoleList);

    }

    private UserRole getUserRoleInfo(Long userId, List<UserRole> userRoles, Role role) {
        UserRole userRole = userRoles.stream()
                .filter(userRoleData -> userRoleData.getRoleId().equals(role.getId()))
                .findFirst()
                .orElseGet(UserRole::new);
        userRole.setUserId(userId);
        userRole.setRoleId(role.getId());
        userRole.setRoleCode(role.getRoleCode());
        userRole.setRoleName(role.getRoleName());
        return userRole;
    }

    @Transactional
    public void removeUserRolesByUserId(Long userId) {
        lambdaUpdate().eq(UserRole::getUserId, userId).remove();
    }

//    @Transactional
//    public void updateRoles(Long userId, List<Long> roleIds) {
//        removeUserRolesByUserId(userId);
//        var newRoles = roleService.getRolesByIds(roleIds);
//        List<UserRole> userRoleList = newRoles.stream().map(role -> {
//            UserRole userRole = new UserRole();
//            userRole.setUserId(userId);
//            userRole.setRoleId(role.getId());
//            userRole.setRoleCode(role.getRoleCode());
//            userRole.setRoleName(role.getRoleName());
//            return userRole;
//        }).toList();
//        saveOrUpdateBatch(userRoleList);
//    }

    @Transactional
    public void updateRoles(Long userId, List<Long> roleIds) {
        var ownedUserRoles = lambdaQuery().eq(UserRole::getUserId, userId).list();
        var targetRoles = roleService.getRolesByIds(roleIds);
        var needDeleteRoles = ownedUserRoles.stream().filter(ownedUserRole -> targetRoles.stream()
                        .noneMatch(role -> role.getId().equals(ownedUserRole.getRoleId())))
                .map(BaseEntity::getId).toList();
        if (!needDeleteRoles.isEmpty()) {
            getBaseMapper().deleteBatchIds(needDeleteRoles);
        }
        List<UserRole> userRoleList = targetRoles.stream().map(role -> getUserRoleInfo(userId, ownedUserRoles, role)).toList();
        saveOrUpdateBatch(userRoleList);
    }


    public List<UserWithRole> getUserWithRoles(Page<User> page) {
        List<Long> userIds = page.getRecords().stream().map(User::getId).toList();
        List<UserRole> userRoles = lambdaQuery().in(!userIds.isEmpty(),UserRole::getUserId, userIds).list();

        List<UserWithRole> userWithRoles = new ArrayList<>();
        page.getRecords().forEach(user -> {
            List<Role> userRolesData = userRoles.stream()
                    .filter(userRole -> userRole.getUserId().equals(user.getId()))
                    .map(userRole -> new Role(userRole.getRoleName(), userRole.getRoleCode(), userRole.getRoleId())).toList();
            userWithRoles.add(new UserWithRole(user, userRolesData));
        });
        return userWithRoles;
    }


}
