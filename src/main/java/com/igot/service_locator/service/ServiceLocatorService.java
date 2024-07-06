package com.igot.service_locator.service;

import com.igot.service_locator.dto.ServiceLocatorDto;
import com.igot.service_locator.entity.ServiceLocatorEntity;
import java.util.List;
import org.springframework.data.domain.Page;

public interface ServiceLocatorService {

  ServiceLocatorEntity createServiceConfig(ServiceLocatorEntity batchService);

  ServiceLocatorEntity updateServiceConfig(ServiceLocatorEntity batchService);

  String deleteServiceConfig(String id);

  ServiceLocatorEntity getServiceConfigByServiceCode(String serviceCode);

  List<ServiceLocatorEntity> searchServiceConfig(ServiceLocatorDto serviceLocatorDto);

  Page<ServiceLocatorEntity> getAllServiceConfig(int page, int size);
}
