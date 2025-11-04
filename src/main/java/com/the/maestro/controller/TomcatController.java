package com.the.maestro.controller;

import com.the.maestro.service.TomcatService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tomcat")
public class TomcatController {

  private final TomcatService tomcatService;

  public TomcatController(TomcatService tomcatService) {
    this.tomcatService = tomcatService;
  }

  @PostMapping("/start")
  public String startTomcat() throws Exception {
    return tomcatService.startTomcat();
  }

  @PostMapping("/stop")
  public String stopTomcat() throws Exception {
    return tomcatService.stopTomcat();
  }

}
