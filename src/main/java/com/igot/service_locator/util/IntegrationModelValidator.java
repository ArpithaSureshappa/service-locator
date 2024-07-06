package com.igot.service_locator.util;

import com.igot.service_locator.entity.IntegrationModel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.igot.service_locator.exceptions.ServiceLocatorException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class IntegrationModelValidator {

    @Autowired
    private ObjectMapper mapper;


    public void validateModel(IntegrationModel integrationModel) {

        if (integrationModel.getStrictCache() == null) {
            integrationModel.setStrictCache(Boolean.FALSE);
        }

        if (integrationModel.getRequestBody() != null) {
            boolean isValidJson = false;
            try {
                mapper.writeValueAsString(integrationModel.getRequestBody());
                isValidJson = true;
            } catch (JsonProcessingException e) {

            }
            if (!isValidJson) {
                throw new ServiceLocatorException("REQUEST_BODY", "Request body is not a valid json object",HttpStatus.BAD_REQUEST);
            }
        }
        if (StringUtils.isBlank(integrationModel.getServiceCode())) {
            throw new ServiceLocatorException("SERVICE_CODE", "Service Code is missing in the request", HttpStatus.BAD_REQUEST);
        }
    }
}
