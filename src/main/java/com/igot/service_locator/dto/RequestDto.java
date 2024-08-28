package com.igot.service_locator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RequestDto {
    private int offset;
    private int limit;
    private Boolean isActive;
}
