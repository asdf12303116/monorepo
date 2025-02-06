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
    public void handleError(ClientHttpResponse response) throws IOException {
        log.warn("网络请求错误,状态码: {}, 返回内容: {}", response.getStatusCode().value(), new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8));
    }
    
}
