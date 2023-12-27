package xyz.chen.member;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.chen.commons.services.redisCacheService;
import xyz.chen.commons.utils.JwtUtils;
import xyz.chen.member.entity.AuthUser;
import xyz.chen.member.services.AuthService;

import java.util.List;


@RestController
public class TestController {

    @Autowired
    private final redisCacheService redisCacheService;
    @Autowired
    private final AuthService authService;

    public TestController(redisCacheService redisCacheService, AuthService authService) {
        this.redisCacheService = redisCacheService;
        this.authService = authService;
    }

    @GetMapping("/1")
    public Object test(){
        AuthUser authUser = (AuthUser) authService.loadUserByUsername("admin");
        String token = JwtUtils.generateSignedJwt(authUser.getUsername(),authUser.getId(), authUser.getRoles());
        return token;
    }

    @GetMapping("/2")
    public Object test1(){
        return redisCacheService.get("test111");
    }

    @GetMapping("/3")
    public Object test2(){
        return "ok1";
    }
}
