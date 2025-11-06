package com.the.maestro.service;


import java.util.Map;

public interface TomcatService {

    Map<String, String> getTomcatProperties();

    String stopTomcat() throws Exception;

    String startTomcat() throws Exception;

    boolean isServiceRunning() throws Exception;
}
