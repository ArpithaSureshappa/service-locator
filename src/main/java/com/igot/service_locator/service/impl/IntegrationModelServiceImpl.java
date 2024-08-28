package com.igot.service_locator.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.igot.service_locator.entity.IntegrationModel;
import com.igot.service_locator.entity.ServiceLocatorEntity;
import com.igot.service_locator.exceptions.ServiceLocatorException;
import com.igot.service_locator.repository.ServiceLocatorRepository;
import com.igot.service_locator.service.IntegrationModelService;
import com.igot.service_locator.util.IntegrationFrameworkUtil;
import com.igot.service_locator.util.IntegrationModelValidator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class IntegrationModelServiceImpl implements IntegrationModelService {

    @Autowired
    private ServiceLocatorRepository serviceLocatorRepository;

    @Autowired
    private IntegrationFrameworkUtil integrationFrameworkUtil;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private IntegrationModelValidator modelValidator;


    @Override
    public Object getDetailsFromExternalService(IntegrationModel integrationModel, HttpServletRequest httpServletRequest) throws IOException {
        log.info("IntegrationModelServiceImpl::callExternalServiceApi");
        modelValidator.validateModel(integrationModel);
        ServiceLocatorEntity serviceLocator;
        Optional<ServiceLocatorEntity> serviceLocatorFromDb = serviceLocatorRepository.findByServiceCodeAndIsActiveTrue(integrationModel.getServiceCode());
        if (serviceLocatorFromDb.isPresent()) {
            serviceLocator = serviceLocatorFromDb.get();
            log.info("serviceLocator::isPreset");
            // Replace placeholders in the URL using the URL map
            String resultantUrl = replaceUrlPlaceholders(serviceLocator, integrationModel.getUrlMap(), integrationModel);
            log.info("The url {} to call the service", resultantUrl);
            serviceLocator.setUrl(resultantUrl);
            return integrationFrameworkUtil.callExternalServiceApi(integrationModel, serviceLocator);
        }
        throw new ServiceLocatorException("SERVICE_CODE", "Service code is not configured in our system, please configure it first", HttpStatus.BAD_REQUEST);

    }

    private String replaceUrlPlaceholders(ServiceLocatorEntity serviceLocator, Map<String, String> urlMap, IntegrationModel integrationModel) {
        log.info("IntegrationModelServiceImpl::replaceUrlPlaceholders");
        String urlToModify = serviceLocator.getUrl();
        if (StringUtils.isNotBlank(serviceLocator.getUrlPlaceholder()) || !CollectionUtils.isEmpty(urlMap)) {
            String urlPlaceholder = serviceLocator.getUrlPlaceholder();

            String[] urlPlaceholderArr = urlPlaceholder.split(",");
            String placeholderValue = null;
            for (int i = 0; i < urlPlaceholderArr.length; i++) {
                String placeholder = urlPlaceholderArr[i];
                if (placeholder.equalsIgnoreCase("{hostAddress}")) {
                    placeholderValue = integrationModel.getHostAddress();
                } else {
                    String placeholderWithoutCurlyBraces = placeholder.substring(1, placeholder.length() - 1);
                    if (urlMap.containsKey(placeholderWithoutCurlyBraces)) {
                        String value = urlMap.get(placeholderWithoutCurlyBraces);
                        if (StringUtils.isNotBlank(value)) {
                            placeholderValue = value;
                        }
                    } else {
                        placeholderValue = ""; // Assign an empty value if the field is not present in urlMap
                    }
                }
                if (placeholderValue != null) {
                    urlToModify = urlToModify.replace(placeholder, placeholderValue);
                } else {
                    // Replace the placeholder with an empty string if the value is null
                    urlToModify = urlToModify.replace(placeholder, "");
                }
            }
        }
        return urlToModify;
    }


}
