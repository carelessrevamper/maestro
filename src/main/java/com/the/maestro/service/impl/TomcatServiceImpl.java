package com.the.maestro.service.impl;

import com.the.maestro.config.TomcatProperties;
import com.the.maestro.service.TomcatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Service
public class TomcatServiceImpl implements TomcatService {

  private final TomcatProperties tomcatProperties;

  @Autowired
  public TomcatServiceImpl(TomcatProperties tomcatProperties) {
    this.tomcatProperties = tomcatProperties;
  }

  @Override
  public Map<String, String> getTomcatProperties() {
    Map<String, String> map = new HashMap<>();
    map.put("path", tomcatProperties.getPath());
    map.put("serviceName", tomcatProperties.getServiceName());
    return map;
  }

  @Override
  public boolean isServiceRunning() throws IOException {
    String serviceName = tomcatProperties.getServiceName();
    Process process = new ProcessBuilder("cmd.exe", "/c", "sc query \"" + serviceName + "\"").start();
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.trim().startsWith("STATE") && line.contains("RUNNING")) {
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public String startTomcat() throws IOException, InterruptedException {
    if (isServiceRunning()) {
      return "Tomcat service is already running.";
    }
    String serviceName = tomcatProperties.getServiceName();
    Process process = new ProcessBuilder("cmd.exe", "/c", "net start " + serviceName)
        .inheritIO()
        .start();
    process.waitFor();
    return "Tomcat service started successfully.";
  }

  @Override
  public String stopTomcat() throws IOException, InterruptedException {
    if (!isServiceRunning()) {
      return "Tomcat service is already stopped.";
    }
    String serviceName = tomcatProperties.getServiceName();
    Process process = new ProcessBuilder("cmd.exe", "/c", "net stop " + serviceName)
        .inheritIO()
        .start();
    process.waitFor();
    return "Tomcat service stopped successfully.";
  }
}
