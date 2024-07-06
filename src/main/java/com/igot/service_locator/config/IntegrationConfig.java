package com.igot.service_locator.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class IntegrationConfig {

    @Value("${integration.fw.host}")
    private String integrationFwHost;

    @Value("${integration.fw.path}")
    private String integrationFwPath;

}
