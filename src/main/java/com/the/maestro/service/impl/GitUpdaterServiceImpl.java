package com.the.maestro.service.impl;


import com.the.maestro.service.GitUpdaterService;
import com.the.maestro.utils.ExtractProperties;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.lib.Ref;
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
    properties.put("branch", properties.get("main"));
    results = walkFolders(properties, properties.get("branch"), Integer.MAX_VALUE);

    return renderResponse(results);

  }

  @Override
  public ResponseEntity<Map<String, String>> updateRepo(String dir, String username, String token) throws IOException {
    Map<String, String> properties = extractProperties.getGitProperties();
    properties.put("finalDirPath", properties.get("finalRootDir") + "\\" + dir);
    properties.put("branch", properties.get("main"));
    Map<String, String> result;

   result = walkFolders(properties, properties.get("branch"), 1);
   return renderResponse(result);
  }

  @Override
  public ResponseEntity<Map<String, String >> gitWithGivenBranch(String dir, String username, String token, String branch) throws IOException {
    Map<String, String> result;
    Map<String, String> properties = extractProperties.getGitProperties();
    properties.put("finalDirPath", properties.get("finalRootDir") + "\\" + dir);
    properties.put("branch", branch);
    logger.info("AAAA "+ properties.get("branch"));
    result = walkFolders(properties, properties.get("branch"), 1);
    return renderResponse(result);

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


  private Map<String, String> walkFolders(Map<String, String> properties, String branch, int maxDepth) throws IOException {
    Map<String, String> result = new TreeMap<>();
    UsernamePasswordCredentialsProvider creds =
        new UsernamePasswordCredentialsProvider(
            properties.get("finalUserName"),
            properties.get("finalToken"));


    try (var stream = Files.walk(Path.of(properties.get("finalDirPath")), maxDepth)) {
      stream
          .filter(Files::isDirectory)
          .forEach(path -> {
            File repoDir = path.toFile();
            if (new File(repoDir, ".git").exists()) {
              try (Git git = Git.open(repoDir)) {
                logger.info("Git repo found at " + repoDir.getAbsolutePath());
                git.fetch()
                    .setCredentialsProvider(creds)
                    .call();
                    Optional<Ref> matchingRef = git.lsRemote()
                        .setRemote("origin")
                        .setHeads(true)
                        .setCredentialsProvider(creds)
                        .call()
                        .stream()
                        .filter(ref -> ref.getName().endsWith("/" + branch))
                        .findFirst();
                if (matchingRef.isEmpty()) {
                  logger.warning(String.format(
                      "Branch '%s' not found remotely for repo %s",
                      branch, repoDir.getAbsolutePath()));
                  result.put(repoDir.getName(), "Skipped: branch not found on remote");
                  return;
                }

                String remoteBranchRef = matchingRef.get().getName();
                String shortBranchName = remoteBranchRef.substring("refs/heads/".length());

                try {
                  git.checkout().setName(shortBranchName).call();
                } catch (RefNotFoundException e) {
                  logger.info(String.format("Error while trying to checkout /%s", shortBranchName));}

                git.pull()
                    .setRemoteBranchName(shortBranchName)
                    .setCredentialsProvider(creds)
                    .call();

                logger.info(String.format("Branch '%s' updated successfully in %s",
                    shortBranchName, repoDir.getAbsolutePath()));
                result.put(repoDir.getName(), "Success");

              } catch (Exception e) {
                logger.severe("Failed to update " + repoDir + ": " + e.getMessage());
                result.put(repoDir.getName(), "Failed: " + e.getMessage());
              }
            }
          });
    } catch (IOException e) {
      logger.severe("Error walking directory: " + e.getMessage());
      result.put("error", "Failed to traverse root directory: " + e.getMessage());
    }

    return result;
  }
}