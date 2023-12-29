package xyz.chen.commons.base;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class BaseResponse<T> {
    private Integer code;
    private String message;
    private Boolean success;
    private T body;
    private PageInfo page;

    public BaseResponse(T body, Boolean isSuccess, Integer code, String message) {
        this.code = code;
        this.message = message;
        this.success = isSuccess;
        this.body = body;
    }

    public BaseResponse(Boolean isSuccess, STATUS_CODE statusCode, String message,T body) {
        this.code = statusCode.value();
        this.message = message;
        this.success = isSuccess;
        this.body = body;
    }

    public BaseResponse(Boolean isSuccess, STATUS_CODE statusCode, String message,T body, PageInfo page) {
        this.code = statusCode.value();
        this.message = message;
        this.success = isSuccess;
        this.body = body;
        this.page = page;
    }

    public BaseResponse(Boolean isSuccess, STATUS_CODE statusCode) {
        this.code = statusCode.value();
        this.message = statusCode.message();
        this.success = isSuccess;
    }

    public static <T> BaseResponse<T> ok(T body, PageInfo page) {
        return new BaseResponse<T>(true, STATUS_CODE.OK, "操作成功", body, page);
    }

    public static <T> BaseResponse<T> ok(T body) {
        return new BaseResponse<T>(true, STATUS_CODE.OK, "操作成功", body);
    }

    public static <T> BaseResponse<T> ok(String message,T body) {
        return new BaseResponse<T>(true, STATUS_CODE.OK, message, body);
    }

    public static <T> BaseResponse<T> fail(STATUS_CODE statusCode, String message,T body) {
        return new BaseResponse<T>(false, statusCode, message, body);
    }

    public static <T> BaseResponse<T> fail(STATUS_CODE statusCode, String message) {
        return new BaseResponse<T>(false, statusCode, message, null);
    }

    public static <T> BaseResponse<T> fail(STATUS_CODE statusCode) {
        return new BaseResponse<T>(false, statusCode);
    }

}
