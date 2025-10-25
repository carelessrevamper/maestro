package com.the.maestro.model;

public class GradleRequest {

  private String repoPath;
  private String command; // e.g. "clean build"

  public String getRepoPath() {
    return repoPath;
  }
  public void setRepoPath(String repoPath) {
    this.repoPath = repoPath;
  }
  public String getCommand() {
    return command;
  }
  public void setCommand(String command) {
    this.command = command;
  }

}
