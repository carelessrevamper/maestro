package com.the.maestro.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

  /**
   * Utility to run local commands and capture stdout/stderr.
   *
   * Example usage:
   *  - Windows:  runCommand(new String[]{"cmd.exe", "/c", "dir"}, ...)
   *  - Unix/macOS: runCommand(new String[]{"/bin/sh", "-c", "ls -la"}, ...)
   */
  public class RunLocalCommand {

    public static class Result {
      public final int exitCode;
      public final List<String> stdout;
      public final List<String> stderr;
      public Result(int exitCode, List<String> stdout, List<String> stderr) {
        this.exitCode = exitCode;
        this.stdout = stdout;
        this.stderr = stderr;
      }
    }

    private static class StreamReader implements Callable<List<String>> {
      private final InputStream stream;
      private final String label;

      StreamReader(InputStream stream, String label) {
        this.stream = stream;
        this.label = label;
      }

      @Override
      public List<String> call() throws Exception {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(stream))) {
          String line;
          while ((line = br.readLine()) != null) {
            // Optionally print as it arrives:
            System.out.println("[" + label + "] " + line);
            lines.add(line);
          }
        }
        return lines;
      }
    }

    /**
     * Run a command with a timeout.
     *
     * @param command array form of command (recommended) or single element for entire command
     * @param workingDir nullable working directory
     * @param envVars nullable map of environment variables to set/override
     * @param timeout maximum time to wait (null for no timeout)
     * @return Result with exit code, stdout and stderr
     * @throws IOException on process start failure
     * @throws InterruptedException if interrupted while waiting
     * @throws TimeoutException if timeout reached
     * @throws ExecutionException if reading streams fails
     */
    public static Result runCommand(
        String[] command,
        String workingDir,
        Map<String, String> envVars,
        Duration timeout
    ) throws IOException, InterruptedException, ExecutionException, TimeoutException {

      ProcessBuilder pb = new ProcessBuilder(command);
      if (workingDir != null) {
        pb.directory(new java.io.File(workingDir));
      }
      if (envVars != null) {
        Map<String, String> env = pb.environment();
        env.putAll(envVars);
      }

      Process process = pb.start();

      ExecutorService ex = Executors.newFixedThreadPool(2);
      Future<List<String>> stdoutFuture = ex.submit(new StreamReader(process.getInputStream(), "OUT"));
      Future<List<String>> stderrFuture = ex.submit(new StreamReader(process.getErrorStream(), "ERR"));

      boolean finished;
      if (timeout != null) {
        finished = process.waitFor(timeout.toMillis(), TimeUnit.MILLISECONDS);
        if (!finished) {
          process.destroyForcibly();
          ex.shutdownNow();
          throw new TimeoutException("Command timed out after " + timeout.toString());
        }
      } else {
        process.waitFor();
      }

      // process has exited
      int exitCode = process.exitValue();

      // collect outputs (give some time for readers to finish)
      List<String> stdout = stdoutFuture.get(1, TimeUnit.SECONDS);
      List<String> stderr = stderrFuture.get(1, TimeUnit.SECONDS);

      ex.shutdownNow();

      return new Result(exitCode, stdout, stderr);
    }

    // --- Example main demonstrating Windows and Unix usage ---
    public static void main(String[] args) {
      // Detect platform and choose an example command
      boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");

      String[] command;
      if (isWindows) {
        // run 'dir' via cmd.exe on Windows
        command = new String[]{"cmd.exe", "/c", "dir"};
      } else {
        // run 'ls -la' via shell on Unix-like systems
        command = new String[]{"/bin/sh", "-c", "ls -la"};
      }

      try {
        System.out.println("Running: " + String.join(" ", command));
        Result r = runCommand(command, null, null, Duration.ofSeconds(20));

        System.out.println("\n=== EXIT CODE: " + r.exitCode + " ===");
        System.out.println("-- STDOUT --");
        r.stdout.forEach(System.out::println);
        System.out.println("-- STDERR --");
        r.stderr.forEach(System.out::println);

      } catch (TimeoutException te) {
        System.err.println("Command timed out: " + te.getMessage());
      } catch (Exception e) {
        System.err.println("Error executing command:");
        e.printStackTrace();
      }
    }
  }
