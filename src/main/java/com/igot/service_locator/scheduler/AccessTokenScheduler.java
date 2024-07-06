package com.igot.service_locator.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.igot.service_locator.security.CourseraSecurity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
public class AccessTokenScheduler {
    @Value("${scheduler.enabled}")
    private boolean schedulerEnabled;

    @Autowired
    CourseraSecurity courseraSecurity;

    @Scheduled(fixedRateString = "${scheduler.fixedRate}")
    public void initBrandWatchSchedulerForNews() throws JsonProcessingException {
        log.info("Job scheduler enabled: " + schedulerEnabled);
        if(schedulerEnabled) {
            courseraSecurity.refreshToken();
        }
    }
}
