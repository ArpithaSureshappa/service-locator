package com.igot.service_locator.controller;

import com.igot.service_locator.dto.ServiceLocatorDto;
import com.igot.service_locator.entity.ServiceLocatorEntity;
import com.igot.service_locator.service.ServiceLocatorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/service-locator/config")
@Slf4j
public class ServiceLocatorController {

    @Autowired
    private ServiceLocatorService serviceLocatorService;


    @PostMapping("/create")
    public ServiceLocatorEntity createServiceConfig(@RequestBody ServiceLocatorEntity batchService) {
        return serviceLocatorService.createServiceConfig(batchService);
    }

    @PutMapping("/update")
    public ServiceLocatorEntity updateServiceConfig(@RequestBody ServiceLocatorEntity batchService) {
        return serviceLocatorService.updateServiceConfig(batchService);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteServiceConfig(@PathVariable String id) {
        serviceLocatorService.deleteServiceConfig(id);
        return "Data deleted successfully with id " + id;
    }

    @PostMapping("/search")
    public List<ServiceLocatorEntity> searchServiceConfig(@RequestBody ServiceLocatorDto serviceLocatorDto) {
        return serviceLocatorService.searchServiceConfig(serviceLocatorDto);
    }

    @GetMapping("/fetch")
    public Page<ServiceLocatorEntity> getAllServiceConfig(@RequestParam(required = false, defaultValue = "0") int page, @RequestParam(required = false, defaultValue = "-1") int size){
        return serviceLocatorService.getAllServiceConfig(page,size);
    }


}
