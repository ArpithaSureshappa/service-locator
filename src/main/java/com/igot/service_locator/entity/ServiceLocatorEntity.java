package com.igot.service_locator.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonValue;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.Where;

import java.io.Serializable;
import java.util.HashMap;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "service_locator")
@Builder
public class ServiceLocatorEntity implements Serializable {

    @Id
    private String id;

    @JsonProperty("requestMethod")
    @Column(name = "request_method")
    private RequestMethod requestMethod = null;

    @Column(name = "url_value",length = 1000)
    @JsonProperty("url")
    private String url;

    @Column(name = "service_code")
    @JsonProperty("serviceCode")
    private String serviceCode;

    @Column(name = "service_name")
    @JsonProperty("serviceName")
    private String serviceName;

    @Column(name = "service_description")
    @JsonProperty("serviceDescription")
    private String serviceDescription;

    @Column(name = "operation_type")
    @JsonProperty("operationType")
    private String operationType;

    @Column(name = "url_placeholder")
    @JsonProperty("urlPlaceholder")
    private String urlPlaceholder;

    @Column(name = "is_active")
    @JsonProperty("isActive")
    private boolean isActive;

    @Column(name = "is_secure_header")
    @JsonProperty("isSecureHeader")
    private boolean isSecureHeader=true;

    @Column(name = "url_segment")
    @JsonProperty("urlSegment")
    private String urlSegment;

    public enum RequestMethod {
        GET("GET"),
        HEAD("HEAD"),
        POST("POST"),
        PUT("PUT"),
        PATCH("PATCH"),
        DELETE("DELETE"),
        OPTIONS("OPTIONS"),
        TRACE("TRACE");

        private String value;

        RequestMethod(String value) {
            this.value = value;
        }

        @JsonValue
        public static RequestMethod fromValue(String text) {
            for (RequestMethod b : RequestMethod.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }

        @Override
        @JsonValue
        public String toString() {
            return String.valueOf(value);
        }
    }

}
