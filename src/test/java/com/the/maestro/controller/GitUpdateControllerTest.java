package com.the.maestro.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.the.maestro.service.GitUpdaterService;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(GitUpdateController.class)
@Import(GitUpdateControllerTestConfig.class) // Import the config providing the mock
class GitUpdateControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private GitUpdaterService gitUpdaterService; // this is the mock

  @Test
  void testUpdateRepositoriesWithDefaults() throws Exception {
    when(gitUpdaterService.updateRepositories(null, null, null))
        .thenReturn(ResponseEntity.ok(Map.of("updated", "2", "failed", "1")));

    mockMvc.perform(post("/maestro/git/update"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.updated").value("2"))
        .andExpect(jsonPath("$.failed").value("1"));
  }

  @Test
  void testUpdateRepositoriesWithParams() throws Exception {
    when(gitUpdaterService.updateRepositories(eq("D:/Test"), eq("user"), eq("token123")))
        .thenReturn(ResponseEntity.ok(Map.of("updated", "3", "failed", "0")));

    mockMvc.perform(post("/maestro/git/update")
            .param("rootDir", "D:/Test")
            .param("username", "user")
            .param("token", "token123"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.updated").value("3"))
        .andExpect(jsonPath("$.failed").value("0"));
  }

  @Test
  void testUpdateRepositoriesServerError() throws Exception {
    when(gitUpdaterService.updateRepositories(any(), any(), any()))
        .thenReturn(ResponseEntity.internalServerError().body(Map.of("error", "Something went wrong")));

    mockMvc.perform(post("/maestro/git/update"))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.error").value("Something went wrong"));
  }
}
