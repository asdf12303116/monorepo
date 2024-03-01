package xyz.chen.commons.base;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class BaseResponse<T> {
    private Integer code;
    private String message;
    private Boolean success;
    private T body;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private PageInfo page;

    public BaseResponse(T body, Boolean isSuccess, Integer code, String message) {
        this.code = code;
        this.message = message;
        this.success = isSuccess;
        this.body = body;
    }

    public BaseResponse(Boolean isSuccess, STATUS_CODE statusCode, String message, T body) {
        this.code = statusCode.value();
        this.message = message;
        this.success = isSuccess;
        this.body = body;
    }

    public BaseResponse(Boolean isSuccess, STATUS_CODE statusCode, String message, T body, PageInfo page) {
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
        return new BaseResponse<>(true, STATUS_CODE.OK, "操作成功", body, page);
    }

    public static <T> BaseResponse<List<T>> ok(Page<T> page) {
        return new BaseResponse<>(true, STATUS_CODE.OK, "操作成功", page.getRecords(), new PageInfo(page));
    }

    public static <T> BaseResponse<T> ok(T body) {
        return new BaseResponse<>(true, STATUS_CODE.OK, "操作成功", body);
    }

    public static <T> BaseResponse<T> ok(String message, T body) {
        return new BaseResponse<>(true, STATUS_CODE.OK, message, body);
    }

    public static <T> BaseResponse<T> fail(STATUS_CODE statusCode, String message, T body) {
        return new BaseResponse<>(false, statusCode, message, body);
    }

    public static <T> BaseResponse<T> fail(STATUS_CODE statusCode, String message) {
        return new BaseResponse<>(false, statusCode, message, null);
    }

    public static <T> BaseResponse<T> fail(STATUS_CODE statusCode) {
        return new BaseResponse<>(false, statusCode);
    }

}
