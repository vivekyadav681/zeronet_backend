package com.example.zeronet.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/v1/heatmap")
public class HeatmapController {
    @GetMapping
    public ResponseEntity<Map<String, Object>> getHeatmap(
            @RequestParam(required = false) String timeRange,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String organizationId) {
        
        Map<String, Object> resp = new HashMap<>();
        resp.put("points", List.of(
            Map.of("lat", 19.0760, "lng", 72.8777, "type", "critical", "weight", 1.0),
            Map.of("lat", 13.0827, "lng", 80.2707, "type", "moderate", "weight", 0.6)
        ));
        resp.put("stats", Map.of("totalHotspots", 30, "avgResponseTime", "4.2m"));
        return ResponseEntity.ok(resp);
    }
}
