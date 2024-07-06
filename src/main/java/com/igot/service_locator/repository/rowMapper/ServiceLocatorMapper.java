package com.igot.service_locator.repository.rowMapper;

import com.igot.service_locator.entity.ServiceLocatorEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class ServiceLocatorMapper implements ResultSetExtractor<List<ServiceLocatorEntity>> {

    @Autowired
    private ObjectMapper mapper;

    @Override
    public List<ServiceLocatorEntity> extractData(ResultSet rs) throws SQLException, DataAccessException {
        log.info("ServiceLocatorMapper::extractData");
        List<ServiceLocatorEntity> serviceLocatorEntityList = new ArrayList<>();

        while (rs.next()) {
            ServiceLocatorEntity locatorEntity = ServiceLocatorEntity.builder()
                    .id(rs.getString("id"))
                    .isActive(rs.getBoolean("is_active"))
                    .operationType(rs.getString("operation_type"))
                    .serviceCode(rs.getString("service_code"))
                    .serviceName(rs.getString("service_name"))
                    .serviceDescription(rs.getString("service_description"))
                    .requestMethod(ServiceLocatorEntity.RequestMethod.values()[rs.getInt("request_method")])
                    .url(rs.getString("url_value"))
                    .urlPlaceholder(rs.getString("url_placeholder"))
                    .isSecureHeader(rs.getBoolean("is_secure_header"))
                    .build();
            serviceLocatorEntityList.add(locatorEntity);
        }

        return serviceLocatorEntityList;
    }
}
