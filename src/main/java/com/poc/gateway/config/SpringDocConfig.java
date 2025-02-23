package com.poc.gateway.config;

import org.springdoc.core.properties.SwaggerUiConfigParameters;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfig {

    @Bean
    public CommandLineRunner openApiGroups(RouteDefinitionLocator locator,
            SwaggerUiConfigParameters swaggerUiConfigParams) {
        return args -> {
            locator.getRouteDefinitions()
                    .collectList()
                    .block()
                    .stream()
                    .map(RouteDefinition::getId)
                    .forEach(swaggerUiConfigParams::addGroup);
        };
    }
}
