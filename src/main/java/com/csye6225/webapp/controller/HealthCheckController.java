package com.csye6225.webapp.controller;

import com.csye6225.webapp.service.DatabaseHealthCheckService;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/healthz")
public class HealthCheckController {
    DatabaseHealthCheckService databaseHealthCheckService;

    public HealthCheckController(DatabaseHealthCheckService databaseHealthCheckService) {
        this.databaseHealthCheckService = databaseHealthCheckService;
    }

    @GetMapping
    public ResponseEntity<Void> healthCheck(@RequestParam Map<String, String> queryParams, @RequestBody(required = false) String requestBody) {
        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl(CacheControl.noCache().mustRevalidate());
        headers.setPragma("no-cache");
        headers.add("X-Content-Type-Options", "nosniff");

        if((null != queryParams && !queryParams.isEmpty()) ||
                (null != requestBody && !requestBody.isEmpty()))
        {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .headers(headers)
                    .build();
        }

        boolean isDatabaseConnected = databaseHealthCheckService.checkDatabaseConnection();

        if(isDatabaseConnected)
        {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .headers(headers)
                    .build();
        }
        else
        {
            return ResponseEntity
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .headers(headers)
                    .build();
        }
    }

    @RequestMapping(method = {RequestMethod.HEAD, RequestMethod.OPTIONS})
    public ResponseEntity<Void> handleHeadAndOptionsMethods() {
        HttpHeaders headers = new HttpHeaders();
        headers.setCacheControl(CacheControl.noCache().mustRevalidate());
        headers.setPragma("no-cache");
        headers.add("X-Content-Type-Options", "nosniff");

        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .headers(headers)
                .build();
    }
}
