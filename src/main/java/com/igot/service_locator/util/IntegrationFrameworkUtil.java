package com.igot.service_locator.util;

import com.bazaarvoice.jolt.Chainr;
import com.bazaarvoice.jolt.JsonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.igot.service_locator.dto.CourseraResponseDto;
import com.igot.service_locator.entity.IntegrationModel;
import com.igot.service_locator.entity.ServiceLocatorEntity;
import com.igot.service_locator.repository.CallExternalService;
import com.igot.service_locator.config.IntegrationConfig;

import com.igot.service_locator.security.CourseraSecurity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;


@Component
@Slf4j
public class IntegrationFrameworkUtil {

    @Autowired
    private IntegrationConfig config;

    @Autowired
    CourseraSecurity courseraSecurity;


    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private CallExternalService callExternalService;

    public Object callExternalServiceApi(
        IntegrationModel integrationModel, ServiceLocatorEntity serviceLocator) throws JsonProcessingException {

        ObjectNode requestObject = this.createRequestObject(serviceLocator, integrationModel);
        log.info("IntegrationFrameworkUtil::requestObject {} ", requestObject);

        Object responseObject = callExternalService.fetchResult(getIntegrationFrameWorkUrl(), requestObject);
        log.info("Got successful response from external system service");

        if (integrationModel.getResponseFormat() != null) {
            Object FormattedData = transformData(responseObject, integrationModel.getResponseFormat());
            if (FormattedData != null) {

                return FormattedData;
            } else {
                return responseObject;
            }
        } else {
            return responseObject;
        }
    }

    // Method to perform Jolt transformation on the response
    public Object transformData(Object source, List<Object> responseFormat) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        String inputJson = "";
        Object transformedOutput;
        try {
            inputJson = objectMapper.writeValueAsString(source);

            Chainr chainr = Chainr.fromSpec(responseFormat);
            transformedOutput = chainr.transform(JsonUtils.jsonToObject(inputJson));

            return transformedOutput;

        } catch (Exception e) {
            ObjectNode response = mapper.createObjectNode();
            ObjectNode errorMessage = objectMapper.createObjectNode();

            errorMessage.put("message", "Jolt Transformation Spec Error, Check in responseFormat");
            ObjectNode extResponse = objectMapper.valueToTree(source);
            response.set("error", errorMessage);

            response.set("response", extResponse);
            return response;
        }


    }
    private ObjectNode mergeHeaders(ObjectNode secureHeader, ObjectNode reqHeader) {
        ObjectNode mergedHeader = mapper.createObjectNode();
        mergedHeader.setAll(secureHeader);
        mergedHeader.setAll(reqHeader);
        return mergedHeader;
    }
    private ObjectNode createRequestObject(ServiceLocatorEntity serviceLocator, IntegrationModel integrationModel) {
        ObjectNode requestObject = mapper.createObjectNode();
        requestObject.put("url", serviceLocator.getUrl());
        requestObject.put("requestMethod", serviceLocator.getRequestMethod().name());
        requestObject.put("operationType", serviceLocator.getOperationType());
        ObjectNode reqHeaderNode = mapper.createObjectNode();
        reqHeaderNode.put("content-type", "application/json");
        if (serviceLocator.isSecureHeader()) {
            String accessToken = "";
            if(serviceLocator.getSecureVendorName().equalsIgnoreCase(integrationModel.getSecureVendorName())){
                accessToken=courseraSecurity.accessToken();
            }

            reqHeaderNode.put("Authorization", "Bearer "+accessToken);
            ObjectNode secureHeader =reqHeaderNode;
            if (integrationModel.getHeaderMap() != null && !integrationModel.getHeaderMap().isEmpty()) {
                ObjectNode requestHeader = getRequestHeader(integrationModel);
                reqHeaderNode = mergeHeaders(secureHeader, requestHeader);
            } else {
                reqHeaderNode = secureHeader;

            }
        } else {
            if (integrationModel.getHeaderMap() != null && !integrationModel.getHeaderMap().isEmpty()) {
                reqHeaderNode = getRequestHeader(integrationModel);

            }
        }
        requestObject.putPOJO("requestHeader", reqHeaderNode);

        if (integrationModel.getRequestBody() != null) {
            requestObject.putPOJO("requestBody", integrationModel.getRequestBody());

        }
        requestObject.put("serviceCode", serviceLocator.getServiceCode());

        requestObject.put("serviceName", serviceLocator.getServiceName());
        requestObject.put("serviceDescription", serviceLocator.getServiceDescription());
        requestObject.put("strictCache", integrationModel.getStrictCache());
        requestObject.put("strictCacheTimeInMinutes", integrationModel.getStrictCacheTimeInMinutes());
        requestObject.put("alwaysDataReadFromCache",integrationModel.isAlwaysDataReadFromCache());
        return requestObject;
    }

    public ObjectNode getRequestHeader(IntegrationModel integrationModel) {
        Map<String, String> headerMap = integrationModel.getHeaderMap();

        // Create the ObjectNode to store the request header
        ObjectNode reqHeaderNode = mapper.createObjectNode();

        // Iterate over the headerMap and add key-value pairs to reqHeaderNode
        if (headerMap != null) {
            for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                reqHeaderNode.put(key, value);
            }
        }
        return reqHeaderNode;
    }

    private StringBuilder getIntegrationFrameWorkUrl() {
        StringBuilder uriBuilder = new StringBuilder();
        return (uriBuilder.append(config.getIntegrationFwHost())
                .append(config.getIntegrationFwPath()));
    }

}
