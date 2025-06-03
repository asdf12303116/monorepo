package xyz.chen.member.controller;


import cn.hutool.core.collection.CollUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import xyz.chen.commons.base.BaseResponse;
import xyz.chen.commons.base.OAuthUserInfo;
import xyz.chen.commons.base.STATUS_CODE;
import xyz.chen.commons.base.UserInfo;
import xyz.chen.commons.utils.JwtUtils;
import xyz.chen.member.entity.AuthUser;
import xyz.chen.member.entity.LoginData;
import xyz.chen.member.entity.dto.SsoInfo;
import xyz.chen.member.services.AuthService;
import xyz.chen.member.services.OAuthService;
import xyz.chen.member.services.UserService;
import xyz.chen.member.utils.UserUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;


@Slf4j
@RestController
public class LoginController {


    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    @Autowired
    ClientRegistrationRepository clientRegistrationRepository;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    OAuthService oAuthService;
    @Autowired
    UserService userService;

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
                case BadCredentialsException ignored -> BaseResponse.fail(STATUS_CODE.LOGIN_FAIL_AUTH);
                case DisabledException ignored -> BaseResponse.fail(STATUS_CODE.LOGIN_FAIL_EXPIRED);
                case LockedException ignored -> BaseResponse.fail(STATUS_CODE.LOGIN_FAIL_LOCKED);
                case AccountExpiredException ignored -> BaseResponse.fail(STATUS_CODE.LOGIN_FAIL_EXPIRED);
                case CredentialsExpiredException ignored -> BaseResponse.fail(STATUS_CODE.LOGIN_FAIL_EXPIRED);
                case UsernameNotFoundException ignored -> BaseResponse.fail(STATUS_CODE.LOGIN_FAIL_AUTH);
                default -> BaseResponse.fail(STATUS_CODE.LOGIN_UNKNOWN_ERROR);
            };
        }
        AuthUser authUser = (AuthUser) authService.loadUserByUsername(loginData.username());
        String token = JwtUtils.generateSignedJwt(authUser.getUsername(), authUser.getId(), authUser.getRoles());

        return BaseResponse.ok("登录成功", token);
    }


    @GetMapping("/login/oauth2Callback")
    public BaseResponse<Object> getCode(@RequestParam String code, @RequestParam String state) {
        ClientRegistration registration = clientRegistrationRepository.findByRegistrationId("azure-dev");
        String codeUrl = registration.getProviderDetails().getTokenUri();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");
        MultiValueMap<String, Object> postParameters = new LinkedMultiValueMap<>();
        postParameters.add("grant_type", "authorization_code");
        postParameters.add("client_id", registration.getClientId());
        postParameters.add("client_secret", registration.getClientSecret());
        postParameters.add("scope", CollUtil.join(registration.getScopes(), ","));
        postParameters.add("redirect_uri", registration.getRedirectUri());
        postParameters.add("code", code);
        postParameters.add("state", state);
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(postParameters, headers);

        try {
            Map data = restTemplate.postForEntity(codeUrl, request, Map.class).getBody();
            if (data == null) {
                return BaseResponse.fail(STATUS_CODE.LOGIN_FAIL_TOKEN);
            }
            String token = (String) data.get("access_token");
            OAuthUserInfo userInfo = JwtUtils.getOAuthUserInfo(token);
            Boolean isExistsUser = userService.existsUserByOAuthUUID(userInfo.uuid());

            if (!isExistsUser) {
                userService.createOAuthUser(userInfo);
            } else {
                userService.updateOAuthUser(userInfo);
            }
            AuthUser authUser = (AuthUser) authService.loadUserByOAuthUUID(userInfo.uuid());
            String loginToken = JwtUtils.generateSignedJwt(authUser.getUsername(), authUser.getId(), authUser.getRoles());
            return BaseResponse.ok("登录成功", loginToken);
        } catch (RuntimeException e) {
            log.warn("获取token出错", e);
            return BaseResponse.fail(STATUS_CODE.LOGIN_FAIL_TOKEN);
        }


    }
    
    @PostMapping("/login/oauth2CallbackPost")
    public BaseResponse<Object> getCodePost(@RequestBody SsoInfo ssoInfo){
        log.info("sso info :{}",ssoInfo);
        return getCode(ssoInfo.code(),ssoInfo.state());
    }

    @GetMapping("/refreshToken")
    public BaseResponse<Object> flushToken() {
        UserInfo userInfo = UserUtils.getUserInfo();
        AuthUser authUser = (AuthUser) authService.loadUserByUsername(userInfo.userName());
        String token = JwtUtils.generateSignedJwt(authUser.getUsername(), authUser.getId(), authUser.getRoles());
        return BaseResponse.ok(token);
    }

    @RequestMapping("/login/logout")
    public void signOut(HttpServletRequest httpRequest, HttpServletResponse response) throws IOException {
        
        String endSessionEndpoint = "https://login.microsoftonline.com/common/oauth2/v2.0/logout";

        String redirectUrl = "http://localhost:8080/msal4jsample/";
        response.sendRedirect(endSessionEndpoint + "?post_logout_redirect_uri=" +
                URLEncoder.encode(redirectUrl, "UTF-8"));
    }


}
