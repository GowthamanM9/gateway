package com.poc.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import reactor.netty.http.server.HttpServer;

@SpringBootApplication
public class GatewayApplication {
	private static final Logger logger = LoggerFactory.getLogger(GatewayApplication.class);
	
	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
		logger.info("Server max HTTP header size from system property: {}", System.getProperty("server.max-http-header-size", "Not set"));
	}

	@Bean
    public HttpServer httpServer() {
        return HttpServer.create()
            .httpRequestDecoder(spec -> spec.maxHeaderSize(32768)) // Force 32KB
            .port(8080);
    }

}
