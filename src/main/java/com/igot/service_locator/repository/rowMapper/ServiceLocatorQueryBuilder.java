package com.igot.service_locator.repository.rowMapper;

import com.igot.service_locator.dto.ServiceLocatorDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;

@Component
@Slf4j
public class ServiceLocatorQueryBuilder {

    private static final String FETCH_SERVICE_LOCATOR_QUERY = "SELECT sl.* from service_locator as sl";


    public String getServiceLocatorQuery(ServiceLocatorDto searchCriteria, List<Object> preparedStmtList) {
        log.info("ServiceLocatorQueryBuilder::getServiceLocatorQuery");

        StringBuilder queryBuilder = new StringBuilder(FETCH_SERVICE_LOCATOR_QUERY);

        List<String> ids = searchCriteria.getIds();
        if (!CollectionUtils.isEmpty(ids)) {
            addClauseIfRequired(preparedStmtList, queryBuilder);
            queryBuilder.append(" sl.id IN (").append(createQuery(ids)).append(")");
            addToPreparedStatement(preparedStmtList, ids);
        }

        if (StringUtils.isNotBlank(searchCriteria.getServiceCode())) {
            addClauseIfRequired(preparedStmtList, queryBuilder);
            queryBuilder.append(" sl.service_code=? ");
            preparedStmtList.add(searchCriteria.getServiceCode());
        }

        if (StringUtils.isNotBlank(searchCriteria.getServiceName())) {
            addClauseIfRequired(preparedStmtList, queryBuilder);
            queryBuilder.append(" sl.service_name=? ");
            preparedStmtList.add(searchCriteria.getServiceName());
        }

        if (StringUtils.isNotBlank(searchCriteria.getUrl())) {
            addClauseIfRequired(preparedStmtList, queryBuilder);
            queryBuilder.append(" sl.url_value=? ");
            preparedStmtList.add(searchCriteria.getUrl());
        }

        if (StringUtils.isNotBlank(searchCriteria.getOperationType())) {
            addClauseIfRequired(preparedStmtList, queryBuilder);
            queryBuilder.append(" sl.operation_type=? ");
            preparedStmtList.add(searchCriteria.getOperationType());
        }

        //added the default as active
        /*addClauseIfRequired(preparedStmtList, queryBuilder);
        queryBuilder.append(" sl.is_active=? ");
        if (searchCriteria.getActive() == null) {
            preparedStmtList.add(Boolean.TRUE);
        } else {
            preparedStmtList.add(searchCriteria.getActive());
        }*/

        return queryBuilder.toString();
    }

    private void addToPreparedStatement(List<Object> preparedStmtList, Collection<String> ids) {
        ids.forEach(id -> {
            preparedStmtList.add(id);
        });
    }

    private String createQuery(Collection<String> ids) {
        StringBuilder builder = new StringBuilder();
        int length = ids.size();
        for (int i = 0; i < length; i++) {
            builder.append(" ? ");
            if (i != length - 1) builder.append(",");
        }
        return builder.toString();
    }


    private static void addClauseIfRequired(List<Object> values, StringBuilder queryString) {
        if (values.isEmpty())
            queryString.append(" WHERE ");
        else {
            queryString.append(" AND");
        }
    }
}
