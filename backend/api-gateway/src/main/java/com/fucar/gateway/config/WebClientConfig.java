package com.fucar.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient customerWebClient(WebClient.Builder loadBalancedWebClientBuilder,
                                       @Value("${app.services.customer-url}") String customerUrl) {
        return loadBalancedWebClientBuilder.baseUrl(customerUrl).build();
    }
}
