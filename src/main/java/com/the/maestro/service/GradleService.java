package com.the.maestro.service;



public interface GradleService {
  String runGradleCommand(String repoPath, String command) throws Exception ;
}
