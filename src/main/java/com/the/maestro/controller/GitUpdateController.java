package com.the.maestro.controller;


import com.the.maestro.AppLogger;
import com.the.maestro.service.GitUpdaterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/maestro/git")
@RequiredArgsConstructor
@Tag(name = "Git Repository Updater", description = "API for updating local Git repositories under a given directory")
public class GitUpdateController {

  private static final Logger logger = AppLogger.getLogger(GitUpdateController.class);
  private final GitUpdaterService gitUpdaterService;



  @Operation(
      summary = "Update all Git repositories under a root directory",
      description = "Traverses directories under the given root path and performs `git pull` on each repository.",
      responses = {
          @ApiResponse(responseCode = "200", description = "Repositories updated successfully"),
          @ApiResponse(responseCode = "500", description = "Error while updating repositories")
      }
  )
  @PostMapping("/update")
  public ResponseEntity<Map<String, String>> updateRepositories(
      @RequestParam(required = false) String rootDir,
      @RequestParam(required = false) String username,
      @RequestParam(required = false) String token) throws IOException {

    logger.info("init using the api");
    return gitUpdaterService.updateRepositories(rootDir, username, token);
  }

  @PostMapping("/onlyOne")
  public ResponseEntity<Map<String, String>> updateRepo(
      @RequestParam(required = false) String gitDir,
      @RequestParam(required = false) String username,
      @RequestParam(required = false) String token) throws IOException {

    logger.info("init using the api");
    return gitUpdaterService.updateRepo(gitDir, username, token);

  }
}
