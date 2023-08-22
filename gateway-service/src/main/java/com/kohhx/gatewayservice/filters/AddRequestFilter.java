package com.kohhx.gatewayservice.filters;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.sun.jersey.api.client.filter.LoggingFilter;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AddRequestFilter extends ZuulFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 2;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        // Get Request Context
        RequestContext ctx = RequestContext.getCurrentContext();
        LOGGER.info("Adding into context");

        // Add ThreadID
        String threadId = RandomStringUtils.randomAlphanumeric(10);
        LOGGER.info("Thread id is {}", threadId);
        ctx.set("threadId", threadId);

        // Add Epoch InTime
        Long epochInTime = System.currentTimeMillis();
        LOGGER.info("In-time {}", epochInTime);
        ctx.set("inTime", epochInTime);

        // Add any custom header and data to pass along to microservices
        ctx.addZuulRequestHeader("myMessage", "Hello from Gateway");
        return null;
    }
}
