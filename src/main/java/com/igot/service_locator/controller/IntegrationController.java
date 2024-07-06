package com.igot.service_locator.controller;

import com.igot.service_locator.entity.IntegrationModel;
import com.igot.service_locator.service.IntegrationModelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("servicelocator/api")
@Slf4j
public class IntegrationController {

    @Autowired
    IntegrationModelService service;

    @PostMapping("/v1/callExternalApi")
    public Object callExternalApiService(@RequestBody IntegrationModel integrationModel, HttpServletRequest httpServletRequest) throws IOException {
        return service.getDetailsFromExternalService(integrationModel, httpServletRequest);
    }

    @GetMapping("/v1/callBackForToken")
    public void acceptTokenCallBack() {
        log.info("code");
    }


}
