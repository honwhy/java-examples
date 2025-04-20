package com.honwhy.examples.copy.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class ContentCachingFilter extends OncePerRequestFilter {

    @Value("${source.intercept.urlList}")
    private String urlList;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (!urlList.contains(request.getRequestURI())) {
            filterChain.doFilter(request, response);
        } else {
            CachedBodyHttpServletRequest cachedRequest = new CachedBodyHttpServletRequest(request);
            filterChain.doFilter(cachedRequest, response);
        }
    }
}
