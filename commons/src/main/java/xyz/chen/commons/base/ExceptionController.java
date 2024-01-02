package xyz.chen.commons.base;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ExceptionController {

    @ExceptionHandler(Exception.class)
    public BaseResponse<String> exceptionHandler(Exception e) {
        log.warn("触发异常: {},异常描述: {}",e.getClass().getName(),e.getMessage());
        log.error("异常详情",e);
        return BaseResponse.fail(STATUS_CODE.UNKNOWN_ERROR, e.getLocalizedMessage());
    }

}
