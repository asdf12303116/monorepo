package xyz.chen.member.controller;


import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import xyz.chen.commons.base.BaseResponse;
import xyz.chen.commons.base.STATUS_CODE;
import xyz.chen.commons.utils.JwtUtils;
import xyz.chen.member.entity.AuthUser;
import xyz.chen.member.entity.LoginData;
import xyz.chen.member.services.AuthService;


@RestController
public class LoginController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;

    public LoginController(AuthService authService, AuthenticationManager authenticationManager) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
    }



    @PostMapping("/login")
    public BaseResponse<String> loginUser(@RequestBody LoginData loginData) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginData.username(), loginData.password())
            );
        } catch (AuthenticationException e) {
            return switch (e) {
                case BadCredentialsException exception -> BaseResponse.fail(STATUS_CODE.LOGIN_FAIL_AUTH);
                case DisabledException exception -> BaseResponse.fail(STATUS_CODE.LOGIN_FAIL_EXPIRED);
                case LockedException exception -> BaseResponse.fail(STATUS_CODE.LOGIN_FAIL_LOCKED);
                case AccountExpiredException exception -> BaseResponse.fail(STATUS_CODE.LOGIN_FAIL_EXPIRED);
                case CredentialsExpiredException exception -> BaseResponse.fail(STATUS_CODE.LOGIN_FAIL_EXPIRED);
                case UsernameNotFoundException exception -> BaseResponse.fail(STATUS_CODE.LOGIN_FAIL_AUTH);
                default -> BaseResponse.fail(STATUS_CODE.LOGIN_UNKNOWN_ERROR);
            };
        }
        AuthUser authUser = (AuthUser) authService.loadUserByUsername(loginData.username());
        String token = JwtUtils.generateSignedJwt(authUser.getUsername(), authUser.getId(), authUser.getRoles());

        return BaseResponse.ok("登录成功", token);
    }

}
