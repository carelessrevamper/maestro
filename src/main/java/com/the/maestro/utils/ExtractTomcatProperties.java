package com.the.maestro.utils;

import com.the.maestro.config.TomcatProperties;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExtractTomcatProperties {

  private final TomcatProperties tomcatProperties;

  @Autowired
  public ExtractTomcatProperties(TomcatProperties tomcatProperties) {
    this.tomcatProperties = tomcatProperties;
  }

  public Map<String,String> getTomcatProperties() {
    Map<String, String> map = new HashMap<>();
    map.put("path", tomcatProperties.getPath());
    map.put("serviceName", tomcatProperties.getServiceName());
    return map;
  }
}
