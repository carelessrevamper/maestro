package com.the.maestro.controller;

import com.the.maestro.service.TomcatService;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@RestController
@RequestMapping("/tomcat")
public class TomcatController {

  private final TomcatService tomcatService;

  @Autowired
  public TomcatController(TomcatService tomcatService) {
    this.tomcatService = tomcatService;
  }

  @GetMapping("/properties")
  public Map<String, String> getTomcatProperties() {
    return tomcatService.getTomcatProperties();
  }

  @PostMapping("/start")
  public ResponseEntity<String> startTomcat() {
    try {
      String message = tomcatService.startTomcat();
      return ResponseEntity.ok(message);
    } catch (Exception e) {
      return ResponseEntity.status(500).body("Failed to start Tomcat: " + e.getMessage());
    }
  }

  @PostMapping("/stop")
  public ResponseEntity<String> stopTomcat() {
    try {
      String message = tomcatService.stopTomcat();
      return ResponseEntity.ok(message);
    } catch (Exception e) {
      return ResponseEntity.status(500).body("Failed to stop Tomcat: " + e.getMessage());
    }
  }

  @GetMapping("/status")
  public ResponseEntity<String> getTomcatStatus() {
    try {
      boolean running = tomcatService.isServiceRunning();
      String status = running ? "RUNNING" : "STOPPED";
      return ResponseEntity.ok(status);
    } catch (Exception e) {
      return ResponseEntity.status(500).body("Failed to get Tomcat status: " + e.getMessage());
    }
  }
}

