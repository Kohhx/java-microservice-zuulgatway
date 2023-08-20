package com.kohhx.gatewayservice.filters;

import com.kohhx.gatewayservice.services.JwtService;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.sun.jersey.api.client.filter.LoggingFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JwtFilter extends ZuulFilter {
    private final JwtService jwtService;
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingFilter.class);

    public JwtFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext ctx = RequestContext.getCurrentContext();
        LOGGER.info("Request to {} from {}", ctx.getRequest().getRequestURI(), ctx.getRequest().getRemoteAddr());
        if (checkIfURIisSecured(ctx.getRequest().getRequestURI())) {
            LOGGER.info("Request to {} from {}", ctx.getRequest().getRequestURI(), ctx.getRequest().getRemoteAddr());
            String authorizationHeader = ctx.getRequest().getHeader("Authorization");
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                throw new ZuulException("Missing Authorization Header", 401, "Authorization header is missing");
            }
            String token = authorizationHeader.substring(7);
            try {
                jwtService.validateToken(token);
            } catch (Exception e) {
                throw new ZuulException("Invalid Token", 401, "Invalid Token");
            }
        }
        return null;
    }

    private boolean checkIfURIisSecured(String requestURI) {
        List<String> notSecuredURI = List.of("/auth/token", "/auth/register", "/auth/validate");
        if (notSecuredURI.contains(requestURI)) {
            return false;
        }
        return true;
    }
}
