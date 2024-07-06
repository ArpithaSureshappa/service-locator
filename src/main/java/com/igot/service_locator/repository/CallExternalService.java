package com.igot.service_locator.repository;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.igot.service_locator.exceptions.ServiceLocatorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Repository;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Repository
@Slf4j
public class CallExternalService {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private RestTemplate restTemplate;


    public Object fetchResult(StringBuilder uri, Object requestData) {

        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        Object response = null;
        try {
            response = restTemplate.postForObject(uri.toString(), requestData, Map.class);

        } catch (HttpClientErrorException e) {
            log.error("External Service threw an Exception: {}", e);
            throw new ServiceLocatorException("EXTERNAL_SERVICE_CALL_EXCEPTION", e.getResponseBodyAsString(),HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Exception while fetching from searcher: {}", e);
            throw new ServiceLocatorException("EXTERNAL_SERVICE_CALL_EXCEPTION", e.getMessage(),HttpStatus.BAD_REQUEST);
        }

        return response;
    }
    public Object fetchResultForFile(StringBuilder uri,MultiValueMap<String, Object> body ) {
        JsonNode responseBody=null;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            ResponseEntity<JsonNode> responseEntity = restTemplate.exchange(uri.toString(), HttpMethod.POST, requestEntity, JsonNode.class);
            responseBody  = responseEntity.getBody();
            return responseBody;

        } catch (HttpClientErrorException e) {
            log.error("External Service threw an Exception: {}", e);
            throw new ServiceLocatorException("EXTERNAL_SERVICE_CALL_EXCEPTION", e.getResponseBodyAsString(),HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("Exception while fetching from searcher: {}", e);
        }
        return responseBody;
    }

}

