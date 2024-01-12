package xyz.chen.member.services;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.chen.commons.base.OAuthUserInfo;
import xyz.chen.member.entity.User;
import xyz.chen.member.repository.UserRepository;

import java.util.Optional;

@Service
public class UserService extends ServiceImpl<UserRepository, User> {

    @Autowired
    private RoleService roleService;

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

    public void createOAuthUser(OAuthUserInfo oAuthUserInfo) {
        Long userId = this.createUser(oAuthUserInfo);
        roleService.grantUserRoles(oAuthUserInfo.groups(), userId);
    }


}
