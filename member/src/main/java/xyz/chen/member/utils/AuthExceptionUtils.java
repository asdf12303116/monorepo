package xyz.chen.member.utils;

import cn.hutool.json.JSONUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import xyz.chen.commons.base.BaseResponse;
import xyz.chen.commons.base.STATUS_CODE;

import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
public class AuthExceptionUtils {

    public static void genExceptionResp(HttpServletResponse response, RuntimeException exception) {
        BaseResponse<Object> respObj;
        respObj = switch (exception) {
            case InsufficientAuthenticationException insufficientAuthenticationException -> {
                yield BaseResponse.fail(STATUS_CODE.ACCESS_DENIED_REQUIRE);
            }
            case AccessDeniedException accessDeniedException -> {
                yield BaseResponse.fail(STATUS_CODE.ACCESS_DENIED_ERROR);
            }
            case SessionAuthenticationException sessionAuthenticationException -> {
                log.debug("异常信息: {}", sessionAuthenticationException.getMessage());
                yield BaseResponse.fail(STATUS_CODE.LOGIN_EXPIRED);
            }
            case BadCredentialsException badCredentialsException -> {
                log.debug("异常信息: {}", badCredentialsException.getMessage());
                yield BaseResponse.fail(STATUS_CODE.LOGIN_FAIL_AUTH);
            }
            case AccountExpiredException accountExpiredException -> {
                log.debug("异常信息: {}", accountExpiredException.getMessage());
                yield BaseResponse.fail(STATUS_CODE.LOGIN_FAIL_STATUS);
            }
            case DisabledException disabledException -> {
                log.debug("异常信息: {}", disabledException.getMessage());
                yield BaseResponse.fail(STATUS_CODE.LOGIN_FAIL_STATUS);
            }
            case LockedException lockedException -> {
                log.debug("异常信息: {}", lockedException.getMessage());
                yield BaseResponse.fail(STATUS_CODE.LOGIN_FAIL_STATUS);
            }
            case AccountStatusException accountStatusException -> {
                log.debug("异常信息: {}", accountStatusException.getMessage());
                yield BaseResponse.fail(STATUS_CODE.LOGIN_FAIL_STATUS);
            }
            case UsernameNotFoundException usernameNotFoundException -> {
                log.debug("异常信息: {}", usernameNotFoundException.getMessage());
                yield BaseResponse.fail(STATUS_CODE.LOGIN_FAIL_AUTH);
            }
            default -> {
                log.debug("异常信息: {}", exception.getLocalizedMessage());
                yield BaseResponse.fail(STATUS_CODE.LOGIN_UNKNOWN_ERROR, exception.getLocalizedMessage());
            }
        };
        applyResp(response,respObj);
    }

    public static void applyResp(HttpServletResponse response, BaseResponse<Object> respObj) {
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        PrintWriter writer;
        try {
            writer = response.getWriter();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        writer.write(JSONUtil.toJsonStr(respObj));
        writer.flush();
        writer.close();
    }
}
