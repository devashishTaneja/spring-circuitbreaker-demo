package com.spring.circuitbreakerdemo.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class SampleController {

    CircuitBreaker circuitBreaker;

    @GetMapping
    ResponseEntity<?> getData(@RequestParam(defaultValue = "fast") String type) {
        Map<String, Object> response = new HashMap<>();
        return circuitBreaker.run(
            () -> {
                try {
                    HttpClient httpClient = HttpClient.newHttpClient();
                    URI uri = URI.create("http://localhost:8081/api/" + type);
                    HttpRequest request = HttpRequest.newBuilder()
                        .uri(uri)
                        .build();
                    HttpResponse<String> value = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                    response.put("Value", value.body());
                    return ResponseEntity.ok(response);
                } catch (Exception ex) {
                    log.error(ex.toString());
                    response.put("Value", null);
                    response.put("Error", ex.toString());
                    return ResponseEntity.internalServerError().body(response);
                }
            },
            ex -> {
                log.error(ex.toString());
                response.put("Value", null);
                response.put("Error", ex.toString());
                return ResponseEntity.badRequest().body(response);
            }
        );
    }

}
