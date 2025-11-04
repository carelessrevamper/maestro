package com.the.maestro.utils;

import java.util.logging.Logger;

public class CommonUtils {

  private static final Logger logger = Logger.getLogger(CommonUtils.class.getName());


  public static boolean isWindows() {
    logger.info("Running WhoOs from commons");
    return System.getProperty("os.name").toLowerCase().contains("win");
  }

}
