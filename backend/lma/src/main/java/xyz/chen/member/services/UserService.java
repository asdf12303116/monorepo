package xyz.chen.member.services;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.chen.commons.base.OAuthUserInfo;
import xyz.chen.member.entity.Role;
import xyz.chen.member.entity.User;
import xyz.chen.member.entity.dto.RoleDto;
import xyz.chen.member.entity.dto.UserDto;
import xyz.chen.member.entity.dto.UserSearchDto;
import xyz.chen.member.entity.dto.UserWithRole;
import xyz.chen.member.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserService extends ServiceImpl<UserRepository, User> {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRoleService userRoleService;


    public Boolean existsUserByOAuthUUID(String uuid) {
        Optional<User> user = this.lambdaQuery().eq(User::getOauth_uuid, uuid).oneOpt();
        return user.isPresent();
    }

    public Long createUser(OAuthUserInfo oAuthUserInfo) {
        User user = new User();
        user.setUsername(oAuthUserInfo.username());
        user.setNickName(oAuthUserInfo.showName());
        user.setOauth_uuid(oAuthUserInfo.uuid());
        this.baseMapper.insert(user);
        return user.getId();
    }

    public void updateUser(OAuthUserInfo oAuthUserInfo) {
        User user = lambdaQuery().eq(User::getOauth_uuid, oAuthUserInfo.uuid()).one();
        if (!user.getNickName().equals(oAuthUserInfo.showName())) {
            saveOrUpdate(user);
        }
    }


    @Transactional
    public void createOAuthUser(OAuthUserInfo oAuthUserInfo) {
        Long userId = this.createUser(oAuthUserInfo);
        userRoleService.grantUserRoles(oAuthUserInfo.groups(), userId);
    }

    @Transactional
    public void updateOAuthUser(OAuthUserInfo oAuthUserInfo) {
        this.updateUser(oAuthUserInfo);
        userRoleService.grantUserRoles(oAuthUserInfo.groups(), lambdaQuery().eq(User::getOauth_uuid, oAuthUserInfo.uuid()).one().getId());
    }

    public Boolean deleteUserById(Long userId) {
        return removeById(userId);
    }

    public UserWithRole getUserById(Long userId) {
        User user = this.getById(userId);
        List<Role> userRole = userRoleService.getUserRoles(userId);
        return new UserWithRole(user, userRole);
    }

    @Transactional
    public void saveUser(UserDto userDto) {
        User user = new User();
        BeanUtils.copyProperties(userDto, user);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        save(user);
        userRoleService.grantRoles(user.getId(), userDto.getRoles().stream().map(RoleDto::roleId).toList());
    }

    public boolean checkUsernameExists(String username) {
        return this.lambdaQuery().eq(User::getUsername, username).oneOpt().isPresent();
    }

    public boolean checkUserExists(Long userId) {
        return lambdaQuery().eq(User::getId, userId).oneOpt().isPresent();
    }

    @Transactional
    public void updateUser(UserDto userDto) {
        editUser(userDto);
        userRoleService.updateRoles(userDto.getId(), userDto.getRoles().stream().map(RoleDto::roleId).toList());
    }

    @Transactional
    public void editUser(UserDto userDto) {
        User user = lambdaQuery().eq(User::getId, userDto.getId()).one();
        BeanUtils.copyProperties(userDto, user);
        if (userDto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }
        updateById(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        boolean isDelete = lambdaUpdate().eq(User::getId, userId).remove();
        if (isDelete) {
            userRoleService.removeUserRolesByUserId(userId);
        }

    }

    public Page<UserWithRole> getAllUsers() {
        Page<User> page = new Page<>(1, 10);
        var users = baseMapper.selectPage(page, lambdaQuery().getWrapper());

        var userWithRoles = userRoleService.getUserWithRoles(users);
        Page<UserWithRole> userWithRolePage = new Page<>();
        BeanUtils.copyProperties(users, userWithRolePage);
        userWithRolePage.setRecords(userWithRoles);
        return userWithRolePage;
    }

    public Page<UserWithRole> searchUsers(UserSearchDto userSearchDto) {
        Page<User> page = new Page<>(userSearchDto.pageInfo().getPageNum(), userSearchDto.pageInfo().getPageSize());
        var users = baseMapper.selectPage(page, lambdaQuery()
                .eq(NumberUtil.isValidNumber(userSearchDto.id()),User::getId,userSearchDto.id())
                .likeRight(StrUtil.isNotEmpty(userSearchDto.username()),User::getUsername,userSearchDto.username())
                .likeRight(StrUtil.isNotEmpty(userSearchDto.nickName()),User::getNickName,userSearchDto.nickName())
                .getWrapper()
        );
        if (users.getRecords().isEmpty()) {
          return   new Page<>();
        }
        var userWithRoles = userRoleService.getUserWithRoles(users);
        Page<UserWithRole> userWithRolePage = new Page<>();
        BeanUtils.copyProperties(users, userWithRolePage);
        userWithRolePage.setRecords(userWithRoles);
        return userWithRolePage;
    }

}
