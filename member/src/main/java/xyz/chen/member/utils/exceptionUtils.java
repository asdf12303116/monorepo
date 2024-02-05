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
public class exceptionUtils {

    public static void genExceptionResp(HttpServletResponse response, RuntimeException exception) {
        BaseResponse<Object> respObj;
        respObj = switch (exception) {
            case InsufficientAuthenticationException ignored -> BaseResponse.fail(STATUS_CODE.ACCESS_DENIED_REQUIRE);
            case AccessDeniedException ignored -> BaseResponse.fail(STATUS_CODE.ACCESS_DENIED_ERROR);
            case SessionAuthenticationException ignored -> BaseResponse.fail(STATUS_CODE.LOGIN_EXPIRED);
            case BadCredentialsException ignored -> BaseResponse.fail(STATUS_CODE.LOGIN_FAIL_AUTH);
            case AccountExpiredException ignored -> BaseResponse.fail(STATUS_CODE.LOGIN_FAIL_STATUS);
            case DisabledException ignored -> BaseResponse.fail(STATUS_CODE.LOGIN_FAIL_STATUS);
            case LockedException ignored -> BaseResponse.fail(STATUS_CODE.LOGIN_FAIL_STATUS);
            case AccountStatusException ignored -> BaseResponse.fail(STATUS_CODE.LOGIN_FAIL_STATUS);
            case UsernameNotFoundException ignored -> BaseResponse.fail(STATUS_CODE.LOGIN_FAIL_AUTH);
            default -> BaseResponse.fail(STATUS_CODE.LOGIN_UNKNOWN_ERROR, exception.getLocalizedMessage());
        };
        log.warn("触发异常: {},异常描述: {}",exception.getClass().getName(),exception.getMessage());
//        log.error("异常详情",exception);
        applyResp(response,respObj);
    }

    public static void genExceptionResp(HttpServletResponse response, Exception exception) {
        BaseResponse<Object> respObj = BaseResponse.fail(STATUS_CODE.LOGIN_UNKNOWN_ERROR, exception.getLocalizedMessage());
        log.warn("触发异常: {},异常描述: {}", exception.getClass().getName(), exception.getMessage());
//        log.error("异常详情", exception);
        applyResp(response, respObj);
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
