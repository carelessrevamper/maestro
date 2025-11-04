package com.the.maestro.service;


import java.util.Map;

public interface TomcatService {
    Map<String, String> getTomcatProperties();

    /**
     * Stop Tomcat service
     */
    String stopTomcat() throws Exception;

    /**
     * Start Tomcat service
     *
     * @return
     */
    String startTomcat() throws Exception;
}
