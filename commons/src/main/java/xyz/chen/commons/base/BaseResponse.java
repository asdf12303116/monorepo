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

}
