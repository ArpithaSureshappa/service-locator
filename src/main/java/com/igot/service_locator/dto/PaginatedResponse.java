package com.igot.service_locator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaginatedResponse<T> {
    private List<T> result;
    private int totalPages;
    private long totalElements;
    private int numberOfElements;
    private int offset;
    private int limit;
}
