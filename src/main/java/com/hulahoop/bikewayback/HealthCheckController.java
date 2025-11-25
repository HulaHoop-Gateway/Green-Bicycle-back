package com.hulahoop.bikewayback;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthCheckController {

    @Value("${server.port}")
    private int port;

    @GetMapping("/health")
    public Map<String, Object> healthCheck() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "UP");
        status.put("server", "server=\"Cinema Service\"");
        status.put("port", port);
        status.put("timestamp", LocalDateTime.now().toString());
        return status;
    }
}