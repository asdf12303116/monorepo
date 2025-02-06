package xyz.chen.commons.utils;

import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class TokenUtils {

    public static HttpServletRequest getRequest() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            throw new RuntimeException("invalid request");
        }
        return requestAttributes.getRequest();
    }

    public static String getToken() {
        HttpServletRequest request = getRequest();

        String token = request.getHeader("token");
        if (StrUtil.isNotBlank(token)) {
            return token;
        }

        throw new RuntimeException("ERROR-SS-0009");
    }

}
