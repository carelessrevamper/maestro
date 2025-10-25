package com.the.maestro.controller;


import com.the.maestro.AppLogger;
import com.the.maestro.model.GradleRequest;
import com.the.maestro.service.GitUpdaterService;
import com.the.maestro.service.GradleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/maestro/busboy")
@RequiredArgsConstructor
@Tag(name = "Prep repo", description = "Pulling latest then cleaning for a given directory")
public class BusBoyController {

  private static final Logger logger = AppLogger.getLogger(BusBoyController.class);
  private final GitUpdaterService gitUpdaterService;
  private final GradleService gradleService;


  @Operation(
      summary = "Pulling latest then cleaning for a given directory",
      description = "Working on a given root path and performs `git pull` then gradle clean (using wrapper).",
      responses = {
          @ApiResponse(responseCode = "200", description = "Project is ready"),
          @ApiResponse(responseCode = "500", description = "Error while working on a given project")
      }
  )
  @PostMapping("/prepRepo")
  public ResponseEntity<Map<String, String>> updateRepositories(
      @RequestBody GradleRequest request,
      @RequestParam() String gitDir,
      @RequestParam(required = false) String username,
      @RequestParam(required = false) String token) throws Exception {

    logger.info("Start working on that project");

    gitUpdaterService.updateRepo(gitDir, username, token);
    gradleService.runGradleCommand(request.getRepoPath(), request.getCommand());
    return ResponseEntity.ok().body(Map.of("status", "success"));
  }
}
