package xyz.chen.commons.base;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
@Slf4j
public class ExceptionController {

    @ExceptionHandler(Exception.class)
    public BaseResponse<String> exceptionHandler(Exception e) {
        log.warn("触发异常: {},异常描述: {}", e.getClass().getName(), e.getMessage());
        log.error("异常详情", e);
        return BaseResponse.fail(STATUS_CODE.UNKNOWN_ERROR, e.getLocalizedMessage());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public BaseResponse<String> noHandlerException(Exception e) {
//        log.warn("触发异常: {},异常描述: {}", e.getClass().getName(), e.getMessage());
        return BaseResponse.fail(STATUS_CODE.UNKNOWN_ERROR, e.getLocalizedMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public BaseResponse<String> accessDeniedException(Exception e) {
        return BaseResponse.fail(STATUS_CODE.ACCESS_DENIED_ERROR);
    }

}
