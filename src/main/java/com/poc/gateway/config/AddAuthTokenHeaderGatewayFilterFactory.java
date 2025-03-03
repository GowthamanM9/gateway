package com.poc.gateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.stereotype.Component;

@Component
public class AddAuthTokenHeaderGatewayFilterFactory extends AbstractGatewayFilterFactory<AddAuthTokenHeaderGatewayFilterFactory.Config> {

    private static final Logger logger = LoggerFactory.getLogger(AddAuthTokenHeaderGatewayFilterFactory.class);

    public AddAuthTokenHeaderGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            return exchange.getPrincipal()
                    .cast(BearerTokenAuthentication.class)
                    .map(auth -> {
                        Object preferredUsername = auth.getTokenAttributes().get("preferred_username");
                        String username = preferredUsername != null ? preferredUsername.toString() : "anonymous";

                        // Add it as a custom header
                        ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                                .header("X-Preferred-Username", username)
                                .build();
                        logger.info("Request headers after mutation: {}", modifiedRequest.getHeaders());
                        return exchange.mutate().request(modifiedRequest).build();
                    })
                    .defaultIfEmpty(exchange)
                    .doOnNext(ex -> logger.info("Forwarding request with headers: {}", ex.getRequest().getHeaders()))
                    .flatMap(chain::filter)
                    .doOnSuccess(aVoid -> {
                        if (exchange.getResponse().getStatusCode() != null) {
                            logger.info("Response status: {}", exchange.getResponse().getStatusCode().value());
                        } else {
                            logger.info("Response status not available");
                        }
                    });
        };
    }

    public static class Config {
        // No config needed
    }
}