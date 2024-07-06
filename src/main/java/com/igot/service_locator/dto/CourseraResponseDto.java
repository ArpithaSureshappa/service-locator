package com.igot.service_locator.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CourseraResponseDto {
    private String access_token;
    private String refresh_token;
    private String expires_in;
    private String token_type;
}
