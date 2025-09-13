package com.honwhy.examples.qrcode.config;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// @Component
public class RequestForwardInterceptor implements HandlerInterceptor {

    private final RestTemplate restTemplate;
    private final String targetBaseUrl;

    public RequestForwardInterceptor(RestTemplate restTemplate, String targetBaseUrl) {
        this.restTemplate = restTemplate;
        this.targetBaseUrl = targetBaseUrl;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        if (request instanceof CachedBodyHttpServletRequest) {
            CachedBodyHttpServletRequest cachedRequest = (CachedBodyHttpServletRequest) request;

            // Build target URL
            String targetUrl = targetBaseUrl + request.getRequestURI();

            // Build headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Forward request
            HttpEntity<byte[]> entity = new HttpEntity<>(cachedRequest.getBody(), headers);
            restTemplate.exchange(targetUrl,
                    HttpMethod.valueOf(request.getMethod()),
                    entity,
                    String.class);
        }
        return true;
    }
}
