package com.spring.circuitbreakerdemo.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreaker;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class Resilience4jCircuitBreakerConfig {

    @Bean
    Resilience4JCircuitBreaker circuitBreaker(Resilience4JCircuitBreakerFactory cbFactory) {
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig
            .custom()
            .minimumNumberOfCalls(3)
            .waitDurationInOpenState(Duration.ofSeconds(10))
            .enableAutomaticTransitionFromOpenToHalfOpen()
            .build();
        TimeLimiterConfig timeLimiterConfig = TimeLimiterConfig
            .custom()
            .cancelRunningFuture(true)
            .timeoutDuration(Duration.ofMillis(500))
            .build();

        cbFactory.configureDefault(id -> new Resilience4JConfigBuilder(id)
            .timeLimiterConfig(timeLimiterConfig)
            .circuitBreakerConfig(circuitBreakerConfig)
            .build());

        return (Resilience4JCircuitBreaker) cbFactory.create("default1");
    }
}
