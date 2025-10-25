package com.the.maestro;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class AppLogger {
  private static final Logger rootLogger = Logger.getLogger("");

  static {
    try {

      int limit = 1_000_000; //1mb
      int fileCount = 5;

      Files.createDirectories(Paths.get("logs"));

      Handler[] handlers = rootLogger.getHandlers();
      for (Handler h : handlers) {
        rootLogger.removeHandler(h);
      }

      // Create file handler (append = true)
      FileHandler fileHandler = new FileHandler("logs/app.log", limit, fileCount, true);
      fileHandler.setFormatter(new SimpleFormatter());
      fileHandler.setLevel(Level.ALL);

      ConsoleHandler consoleHandler = new ConsoleHandler();
      consoleHandler.setFormatter(new SimpleFormatter());
      consoleHandler.setLevel(Level.ALL);

      rootLogger.addHandler(fileHandler);
      rootLogger.addHandler(consoleHandler);

      rootLogger.setLevel(Level.INFO);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private AppLogger() {}

  public static Logger getLogger(Class<?> clazz) {
    return Logger.getLogger(clazz.getName());
  }
}