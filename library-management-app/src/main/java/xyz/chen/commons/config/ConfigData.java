package xyz.chen.commons.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "config")
public class ConfigData {
    private Boolean showTraceInfo = false;
    private Long defaultSystemId = -1L;
    private String defaultSystemName = "system";
    private String defaultSharedSecret = "asdf12303116";
    private List<String> permitAllUrls = List.of("");
}
