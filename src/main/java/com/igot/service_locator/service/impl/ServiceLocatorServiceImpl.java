package com.igot.service_locator.service.impl;


import com.igot.service_locator.exceptions.ServiceLocatorException;
import com.igot.service_locator.repository.ServiceLocatorRepository;
import com.igot.service_locator.repository.rowMapper.ServiceLocatorMapper;
import com.igot.service_locator.repository.rowMapper.ServiceLocatorQueryBuilder;
import com.igot.service_locator.dto.ServiceLocatorDto;
import com.igot.service_locator.entity.ServiceLocatorEntity;

import com.igot.service_locator.service.ServiceLocatorService;
import com.igot.service_locator.validator.ServiceLocatorValidator;
import com.fasterxml.uuid.Generators;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import java.time.Duration;
import java.util.*;


@Service
public class ServiceLocatorServiceImpl implements ServiceLocatorService {
    @Value("${cache.data.ttl.in.minutes}")
    public Long cacheDataTtl;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ServiceLocatorRepository serviceLocaterRepository;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private ServiceLocatorValidator locatorValidator;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ServiceLocatorQueryBuilder queryBuilder;

    @Autowired
    private ServiceLocatorMapper locatorMapper;

    public static final String SERVICE_LOCATOR_KEY = "servicelocator_";


private static final String ERROR_MESSAGE="ERROR";
    @Override
    public ServiceLocatorEntity createServiceConfig(ServiceLocatorEntity locatorEntity) {
        locatorValidator.validate(locatorEntity);
        Optional<ServiceLocatorEntity> optSchemeDetails = serviceLocaterRepository.findByServiceCodeAndIsActiveTrue(locatorEntity.getServiceCode());
        if (optSchemeDetails.isPresent()) {
            throw new ServiceLocatorException("SERVICE_CODE", "One service is already there in the system with service code : " + locatorEntity.getServiceCode() + " , " +
                    "Please create a service config with unique service code", HttpStatus.BAD_REQUEST);
        }
        UUID uuid = Generators.timeBasedGenerator().generate();
        String id = uuid.toString();
        locatorEntity.setId(id);
        locatorEntity.setActive(Boolean.TRUE);
        //save to the database
        ServiceLocatorEntity serviceLocatorEntity=serviceLocaterRepository.save(locatorEntity);
        // Save to Redis
        redisTemplate.opsForValue().set(SERVICE_LOCATOR_KEY + serviceLocatorEntity.getServiceCode(), serviceLocatorEntity, Duration.ofMinutes(cacheDataTtl));
        return serviceLocatorEntity;
    }

    @Override
    public String deleteServiceConfig(String id) {

        Optional<ServiceLocatorEntity> dataFromDb = serviceLocaterRepository.findById(id, true);
        if (dataFromDb.isPresent()) {
            ServiceLocatorEntity entity = dataFromDb.get();
            entity.setActive(false);
            serviceLocaterRepository.save(entity);
            String serviceCode=entity.getServiceCode();
            String redisKey=SERVICE_LOCATOR_KEY+serviceCode;
            redisTemplate.delete(redisKey);
            return "Data deleted successfully with id " + id;
        } else {
            throw new ServiceLocatorException(ERROR_MESSAGE, "Data Not found to delete with given id " + id,HttpStatus.BAD_REQUEST);
        }

    }

    @Override
    public ServiceLocatorEntity getServiceConfigByServiceCode(String serviceCode) {
        ServiceLocatorEntity serviceLocator;
        serviceLocator= (ServiceLocatorEntity) redisTemplate.opsForValue().get(SERVICE_LOCATOR_KEY + serviceCode);
        if(serviceLocator!=null){

            return serviceLocator;
        }
        else{
            // Data not found in Redis, query the database
            Optional<ServiceLocatorEntity> serviceLocatorFromDb = serviceLocaterRepository.findByServiceCodeAndIsActiveTrue(serviceCode);
            if (serviceLocatorFromDb.isPresent()) {
                // Data found in the database
                serviceLocator = serviceLocatorFromDb.get();

                // Save the data to Redis for future access
                redisTemplate.opsForValue().set(SERVICE_LOCATOR_KEY + serviceLocator.getServiceCode(), serviceLocator,Duration.ofMinutes(cacheDataTtl));
                return serviceLocator;
            } else {
                // Data not found in Redis or the database

                throw new ServiceLocatorException("SERVICE_CODE", "Service code is not configured in our system, please configure it first",HttpStatus.BAD_REQUEST);
                // Handle the case when the data is not found
            }
        }
    }

    @Override
    public List<ServiceLocatorEntity> searchServiceConfig(ServiceLocatorDto searchCriteria) {

        if (CollectionUtils.isEmpty(searchCriteria.getIds())
                && StringUtils.isBlank(searchCriteria.getUrl())
                && StringUtils.isBlank(searchCriteria.getServiceCode())
                && StringUtils.isBlank(searchCriteria.getServiceName())
                && StringUtils.isBlank(searchCriteria.getOperationType())) {
            throw new ServiceLocatorException("SEARCH_CRITERIA", "One search criteria must be provided.",HttpStatus.BAD_REQUEST);
        }

        List<Object> preparedStmtList = new ArrayList<>();

        String query = queryBuilder.getServiceLocatorQuery(searchCriteria, preparedStmtList);
        List<ServiceLocatorEntity> serviceLocatorEntityList = jdbcTemplate.query(query, locatorMapper, preparedStmtList.toArray());

        if (CollectionUtils.isEmpty(serviceLocatorEntityList)) {
            throw new ServiceLocatorException(ERROR_MESSAGE,"No data available for the search result",HttpStatus.BAD_REQUEST);
        }
        return serviceLocatorEntityList;
    }


    @Override
    public Page<ServiceLocatorEntity> getAllServiceConfig(int page, int size) {
        if (page >= 0 && size >= -1) {
            if (size == -1) {
                // Fetch all records
                List<ServiceLocatorEntity> allServiceConfigs = serviceLocaterRepository.findAll(true);
                return new PageImpl<>(allServiceConfigs);
            } else {
                // Perform pagination
                Pageable pageable = PageRequest.of(page, size);
                return serviceLocaterRepository.findAll(pageable);
            }
        } else {
            throw new IllegalArgumentException("Invalid value for 'page' parameter. It should be >= 0.");
        }
    }
    @Override
    public ServiceLocatorEntity updateServiceConfig(ServiceLocatorEntity updatedServiceLocatoerConfig) {

        if (updatedServiceLocatoerConfig.getId() == null) {
            throw new ServiceLocatorException(ERROR_MESSAGE, "Service Locator id is required for updating the record",HttpStatus.BAD_REQUEST);
        }
        locatorValidator.validate(updatedServiceLocatoerConfig);
        Optional<ServiceLocatorEntity> optSchemeDetails = serviceLocaterRepository.findById(updatedServiceLocatoerConfig.getId());
        if (optSchemeDetails.isPresent()) {
            ServiceLocatorEntity batchService = optSchemeDetails.get();

            // Copy the property values from updatedSchemeDetails to schemeDetails, Exclude the "id" fields from being copied
            BeanUtils.copyProperties(updatedServiceLocatoerConfig, batchService, "id");
            redisTemplate.opsForValue().set(SERVICE_LOCATOR_KEY + batchService.getServiceCode(), batchService,Duration.ofMinutes(cacheDataTtl));
            return serviceLocaterRepository.save(batchService);
        }
        throw new ServiceLocatorException(ERROR_MESSAGE, "Service Locate not found with ID: " + updatedServiceLocatoerConfig.getId(),HttpStatus.BAD_REQUEST);
    }
}
