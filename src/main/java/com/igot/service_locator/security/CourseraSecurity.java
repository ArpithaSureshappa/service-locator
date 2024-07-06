package com.igot.service_locator.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.igot.service_locator.dto.CourseraResponseDto;
import com.igot.service_locator.exceptions.ServiceLocatorException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.*;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Component
@Slf4j
public class CourseraSecurity {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    RestTemplate restTemplate;


    public static final String COURSERA_ACCESS_TOKEN = "coursera-access-token";

    @Value("${coursera.redirect_uri}")
    private String courseraRedirectUri;
    @Value("${coursera.client_id}")
    private String courseraClientId;
    @Value("${coursera.client_secret}")
    private String courseraClientSecret;
    @Value("${coursera.access_type}")
    private String courseraAccessType;
    @Value("${coursera.code}")
    private String courseraCode;
    @Value("${coursera.accesstoken.grant_type}")
    private String courseraAccesstokenGrantType;
    @Value("${coursera.refreshtoken.grant_type}")
    private String courseraRefreshtokenGrantType;
    @Value("${coursera.host_address}")
    private String courseraHostAddress;
    @Value("${coursera.token.path_url}")
    private String courseraTokenPathUrl;
    @Value("${coursera.refresh.token.cache.data.ttl.in.days}")
    private Long courseraRefreshTokenCacheData;


    public String accessToken() {
        try {
            String loginUrl = courseraHostAddress + courseraTokenPathUrl;
            String refreshToken = refreshToken();
            //String refreshToken= (String) redisTemplate.opsForValue().get(COURSERA_ACCESS_TOKEN);
            String requestBody = "redirect_uri=" + courseraRedirectUri + "&client_id=" + courseraClientId + "&client_secret=" + courseraClientSecret
                    + "&access_type=" + courseraAccessType + "&grant_type=" + courseraRefreshtokenGrantType + "&refresh_token=" + refreshToken;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(loginUrl))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Cookie", "CSRF3-Token=1720887152.j8AgoL15G2WYj0QT; __204u=3172334549-1720023152258")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();
            HttpResponse<String> response = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.ALWAYS)
                    .build()
                    .send(request, HttpResponse.BodyHandlers.ofString());
            String tokenResponse = response.body();
            CourseraResponseDto courseraResponseDto = objectMapper.readValue(tokenResponse, CourseraResponseDto.class);
            // redisTemplate.opsForValue().set(COURSERA_ACCESS_TOKEN,courseraResponseDto.getAccess_token(), Duration.ofMinutes(30));
            return courseraResponseDto.getAccess_token();
        } catch (Exception e) {
            throw new ServiceLocatorException("ERROR", "Failed to refresh authentication token", HttpStatus.BAD_REQUEST);
        }
    }


    public String refreshToken() {
        try {
            String loginUrl = courseraHostAddress + courseraTokenPathUrl;
            String requestBody = "redirect_uri=" + courseraRedirectUri + "&client_id=" + courseraClientId + "&client_secret=" + courseraClientSecret
                    + "&access_type=" + courseraAccessType + "&code=" + getCode() + "&grant_type=" + courseraAccesstokenGrantType;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(loginUrl))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Cookie", "CSRF3-Token=1720887152.j8AgoL15G2WYj0QT; __204u=3172334549-1720023152258")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();
            HttpResponse<String> response = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.ALWAYS)
                    .build()
                    .send(request, HttpResponse.BodyHandlers.ofString());
            String tokenResponse = response.body();
            CourseraResponseDto courseraResponseDto = objectMapper.readValue(tokenResponse, CourseraResponseDto.class);
            //redisTemplate.opsForValue().set(COURSERA_ACCESS_TOKEN,courseraResponseDto.getRefresh_token(), Duration.ofDays(courseraRefreshTokenCacheData));
            return courseraResponseDto.getRefresh_token();
        } catch (Exception e) {
            throw new ServiceLocatorException("ERROR", "Failed to refresh authentication token", HttpStatus.BAD_REQUEST);
        }
    }
//getCode method not working
    public String getCode() {
        try {
            URI uri = new URIBuilder("https://accounts.coursera.org/oauth2/v1/auth")
                    .addParameter("scope", "view_profile access_business_api")
                    .addParameter("redirect_uri", "http://localhost:9876/callback")
                    .addParameter("access_type", "offline")
                    .addParameter("grant_type", "authorization_code")
                    .addParameter("response_type", "code")
                    .addParameter("client_id", "IiHgQlJU4rV38KMEOcG4bQ")
                    .build();
            List<Header> defaultHeaders = new ArrayList<>();
            defaultHeaders.add(new BasicHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9"));

            CloseableHttpClient httpClient = HttpClientBuilder.create()
                    .setRedirectStrategy(new LaxRedirectStrategy())
                    .setDefaultHeaders(defaultHeaders)
                    .build();
            HttpGet authRequest = new HttpGet(uri);
            CloseableHttpResponse authResponse = httpClient.execute(authRequest);

            // Check if the response is a redirect (status code 302)
            int authStatusCode = authResponse.getStatusLine().getStatusCode();
            if (authStatusCode == 302) {
                String redirectUrl = authResponse.getFirstHeader("Location").getValue();
                System.out.println("Redirect URL from Coursera: " + redirectUrl);

                // Step 2: Extract code parameter from the redirect URL and make second request
                URI callbackUri = new URI(redirectUrl);
                String code = callbackUri.getQuery().split("=")[1];
                return code;
                //String redirectUrl = response.getFirstHeader("Location").getValue();
            }
            return "";
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}