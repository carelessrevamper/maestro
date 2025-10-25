package com.the.maestro.controller;

import com.the.maestro.service.GitUpdaterService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GitUpdateControllerTestConfig {

  @Bean
  public GitUpdaterService gitUpdaterService() {
    // return a Mockito mock
    return org.mockito.Mockito.mock(GitUpdaterService.class);
  }
}

