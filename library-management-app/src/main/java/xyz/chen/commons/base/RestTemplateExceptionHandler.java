package xyz.chen.commons.base;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
public class RestTemplateExceptionHandler extends DefaultResponseErrorHandler {

    @Override
    protected void handleError(ClientHttpResponse response, HttpStatusCode statusCode) throws IOException {
        log.warn("网络请求错误,状态码: {}, 返回内容: {}", statusCode.value(), new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8));
    }
}
