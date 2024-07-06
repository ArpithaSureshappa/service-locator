package com.igot.service_locator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class ServiceLocatorDto {
  private List<String> ids;
  private String url;
  private String ServiceCode;
  private String  serviceName;
  private String operationType;
  private Boolean active;

}
