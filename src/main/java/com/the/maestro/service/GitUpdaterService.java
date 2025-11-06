package com.the.maestro.service;

import java.io.IOException;
import java.util.Map;
import org.springframework.http.ResponseEntity;


public interface GitUpdaterService {
  ResponseEntity<Map<String, String>> updateRepositories(String rootDir, String username, String token) throws IOException;

  ResponseEntity<Map<String, String>> updateRepo(String dir, String username, String token) throws IOException;

  ResponseEntity<Map<String, String >> gitWithGivenBranch(String dir, String username, String token, String branch) throws IOException;
}
