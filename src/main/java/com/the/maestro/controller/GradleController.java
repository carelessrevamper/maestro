package com.the.maestro.controller;


import com.the.maestro.model.GradleRequest;
import com.the.maestro.service.GradleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/maestro/gradle")
@Tag(name = "Gradle ToolKit", description = "Running gradle commands for a given directory")
public class GradleController {

  @Autowired
  private GradleService gradleService;

  @Operation(
      summary = "Running gradle commands for a given directory",
      description = "Using gradle wrapper from a given directory to perform build commands.",
      responses = {
          @ApiResponse(responseCode = "200", description = "Command ran successfully"),
          @ApiResponse(responseCode = "500", description = "Error while running a command")
      }
  )

  @PostMapping("/run")
  public String runGradle(@RequestBody GradleRequest request) throws Exception {
    return gradleService.runGradleCommand(request.getRepoPath(), request.getCommand());
  }
}

