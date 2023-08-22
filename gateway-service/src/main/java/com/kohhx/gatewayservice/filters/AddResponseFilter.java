package com.kohhx.gatewayservice.filters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.sun.jersey.api.client.filter.LoggingFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
public class AddResponseFilter extends ZuulFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public String filterType() {
        return "post";
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
        LOGGER.info("Getting from context");
        RequestContext ctx = RequestContext.getCurrentContext();

        // Get inputstream and return response body in String format
        String responseBody = getResponseBody(ctx.getResponseDataStream());

        // Get all data to append to the response
        String threadId = (String) ctx.get("threadId");
        Long inTime = (Long) ctx.get("inTime");
        Long outTime = System.currentTimeMillis();
        Long latency = outTime - inTime;

        try {
            // Parse the existing JSON response
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);

            // Add data to the JSON response
            ((ObjectNode) jsonNode).put("thread-id", threadId);
            ((ObjectNode) jsonNode).put("in-time", inTime);
            ((ObjectNode) jsonNode).put("out-time", outTime);
            ((ObjectNode) jsonNode).put("latency", latency);

            // Serialize the updated JSON back to a string
            String updatedResponseBody = objectMapper.writeValueAsString(jsonNode);

            // Set the updated JSON response back to the context
            ctx.setResponseBody(updatedResponseBody);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private String getResponseBody (InputStream inputstream) {
        String responseBody = null;
        try {
            return StreamUtils.copyToString(inputstream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
