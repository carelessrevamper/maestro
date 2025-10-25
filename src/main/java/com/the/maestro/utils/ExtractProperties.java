package com.the.maestro.utils;

import com.the.maestro.config.GitProperties;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class ExtractProperties {

  private final GitProperties gitProperties;

  @Autowired
  public ExtractProperties(GitProperties gitProperties) {
    this.gitProperties = gitProperties;
  }

  public Map<String,String> getGitProperties(){
    Map<String,String> map = new HashMap<>();
    map.put("finalRootDir",gitProperties.getRootDir());
    map.put("finalUserName",gitProperties.getUsername());
    map.put("finalToken",gitProperties.getToken());
    return map;
  }

  public Map<String,String> getGitProperties(String rootDir, String username, String token){
    Map<String,String> map = new HashMap<>();
    map.put("finalRootDir",
        (rootDir != null && !rootDir.isBlank()) ? rootDir : gitProperties.getRootDir());
    map.put("finalUserName",
        (username != null && !username.isBlank()) ? username : gitProperties.getUsername());
    map.put("finalToken",
        (token != null && !token.isBlank()) ? token : gitProperties.getToken());
    return map;
  }
}
