package xyz.chen.commons.base;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum STATUS_CODE {
    OK(20000, ""),
    REQUEST_TYPE_ERROR(40500, "请求类型错误,不允许的类型:"),

    LOGIN_UNKNOWN_ERROR(400000, "登录时遇到问题，请稍后重试"),
    LOGIN_FAIL_TOKEN(400001, "获取token时遇到问题，请稍后重试"),
    LOGIN_FAIL_AUTH(40100, "用户名或密码错误"),
    LOGIN_FAIL_CAPTCHA(401100, "验证码错误"),
    LOGIN_FAIL_DISABLE(401200, "用户已禁用，请联系管理员"),
    LOGIN_FAIL_LOCKED(401210, "用户已锁定，请联系管理员"),
    LOGIN_FAIL_EXPIRED(401220, "用户已过期，请联系管理员"),
    LOGIN_FAIL_STATUS(401230, "用户状态异常，请联系管理员"),
    LOGIN_EXPIRED(401500, "登陆过期"),
    ACCESS_DENIED_REQUIRE(40310, "需要登陆"),
    ACCESS_DENIED_ERROR(40300, "权限不足"),
    NOT_FOUND(40400, "资源不存在"),
    UNKNOWN_ERROR(50000, "未知错误");
    private final Integer code;
    private final String codeMessage;

    public Integer value() {
        return this.code;
    }

    public String message() {
        return this.codeMessage;
    }

}
