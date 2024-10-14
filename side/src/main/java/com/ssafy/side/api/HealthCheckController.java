package com.ssafy.side.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "HealthCheck Controller", description = "HealthCheck API Document")
public class HealthCheckController {

    @GetMapping("/health")
    @Operation(summary = "HealthCheck", description = "HealthCheck API입니다.")
    public String healthCheck() {
        return "OK";
    }
}
