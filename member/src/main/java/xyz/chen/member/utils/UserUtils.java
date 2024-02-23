package xyz.chen.member.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import xyz.chen.commons.base.UserInfo;
import xyz.chen.member.entity.AuthUser;

public class UserUtils {


    public static UserInfo getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AuthUser authUser = (AuthUser) authentication.getPrincipal();
        return new UserInfo(authUser.getId(), authUser.getUsername(), authUser.getRoles());
    }
}
