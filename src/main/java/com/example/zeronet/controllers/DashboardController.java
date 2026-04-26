package com.example.zeronet.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/v1/dashboard")
public class DashboardController {
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats(@RequestParam(required = false) String organizationId) {
        Map<String, Object> resp = new HashMap<>();
        resp.put("activeSosAlerts", Map.of("count", 4, "changePercent", 24, "changeDirection", "up"));
        resp.put("highPriorityIncidents", Map.of("count", 12, "description", "Requiring immediate action"));
        resp.put("respondersInField", Map.of("count", 87, "connectionRate", 92));
        resp.put("todayResolved", Map.of("count", 142, "chartData", new int[]{28, 34, 56, 28, 25, 78, 12}));
        return ResponseEntity.ok(resp);
    }
}
