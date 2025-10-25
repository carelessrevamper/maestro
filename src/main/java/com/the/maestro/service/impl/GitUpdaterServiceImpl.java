package com.the.maestro.service.impl;


import com.the.maestro.service.GitUpdaterService;
import com.the.maestro.utils.ExtractProperties;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
public class GitUpdaterServiceImpl implements GitUpdaterService {

  @Autowired
  private ExtractProperties extractProperties;

  private static final Logger logger = Logger.getLogger(GitUpdaterServiceImpl.class.getName());

  @Override
  public ResponseEntity<Map<String, String>> updateRepositories(String rootDir, String username, String token)
      throws IOException {
    Map<String, String> results;
    Map<String, String> properties = extractProperties.getGitProperties(rootDir, username, token);
    properties.put("finalDirPath", properties.get("finalRootDir"));
    results = walkFolders(properties, Integer.MAX_VALUE);

    return renderResponse(results);

  }

  @Override
  public ResponseEntity<Map<String, String>> updateRepo(String dir, String username, String token) throws IOException {
    Map<String, String> properties = extractProperties.getGitProperties();
    properties.put("finalDirPath", properties.get("finalRootDir") + "\\" + dir);
    Map<String, String> result;

   result = walkFolders(properties,1);
   return renderResponse(result);
  }


  private Map<String, String> walkFolders(Map<String, String> properties, int maxDepth) throws IOException {
    Map<String, String> result = new TreeMap<>();

    try (var stream = Files.walk(Path.of(properties.get("finalDirPath")), maxDepth)) {
      stream
          .filter(Files::isDirectory)
          .forEach(path -> {
            File repoDir = path.toFile();
            if (new File(repoDir, ".git").exists()) {
              try (Git git = Git.open(repoDir)) {
                logger.info(String.format("Git repository found at %s", repoDir.getAbsolutePath()));
                git.pull()
                    .setCredentialsProvider(
                        new UsernamePasswordCredentialsProvider(properties.get("finalUserName"), properties.get("finalToken")))
                    .call();
                logger.info(String.format("Git repository successfully pulled at %s", repoDir.getAbsolutePath()));
                result.put(repoDir.getName(), "Success");
              } catch (IOException | GitAPIException e) {
                logger.severe("Failed to update " + repoDir + ": " + e.getMessage());
                result.put(repoDir.getName(), "Failed" + e.getMessage());
              }
            }
          });
    } catch (IOException e) {
      logger.severe("Error walking directory: " + e.getMessage());
      result.put("error", "Failed to traverse root directory: " + e.getMessage());
    }
    return result;
  }

  public ResponseEntity<Map<String, String>> renderResponse(Map<String, String> inputResults){

    if(inputResults.isEmpty()){
      logger.severe("Error, No action has been produced");
      return ResponseEntity.internalServerError()
          .body(Map.of("error", "Failed to produced on given directory: "));
    }else{
      logger.info("Building a success response");
      long successCount = inputResults.values().stream().filter("Success"::equals).count();
      inputResults.put("successCount", String.valueOf(successCount));
      inputResults.put("ErrorCount", String.valueOf(successCount - (inputResults.size() - 1)));
      return ResponseEntity.ok(inputResults);
    }
  }

}