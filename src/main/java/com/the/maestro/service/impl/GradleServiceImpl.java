package com.the.maestro.service.impl;


import com.the.maestro.service.GradleService;
import com.the.maestro.utils.CommonUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.logging.Logger;
import org.springframework.stereotype.Service;

@Service
public class GradleServiceImpl implements GradleService {

  private static final Logger logger = Logger.getLogger(GradleServiceImpl.class.getName());

  public String runGradleCommand(String repoPath, String gradleCommand) throws Exception {
    File directory = new File(repoPath);
    if (!directory.exists() || !directory.isDirectory()) {
      throw new IllegalArgumentException("Invalid repository path: " + repoPath);
    }

    String command = isWindows()
        ? "cmd /c gradlew " + gradleCommand
        : "./gradlew " + gradleCommand;

    ProcessBuilder pb = new ProcessBuilder(command.split(" "));
    pb.directory(directory);
    pb.redirectErrorStream(true);
    Process process = pb.start();

    logger.info("Process started with command: " +
        command + " and directory: " + directory.getAbsolutePath() );
    StringBuilder output = new StringBuilder();
    try (BufferedReader reader = new BufferedReader(
        new InputStreamReader(process.getInputStream()))) {
      String line;
      while ((line = reader.readLine()) != null) {
        output.append(line).append(System.lineSeparator());
      }
    }

    int exitCode = process.waitFor();
    output.append("\nExit Code: ").append(exitCode);
    return output.toString();
  }

  private boolean isWindows() {
    return CommonUtils.isWindows();
  }

}
