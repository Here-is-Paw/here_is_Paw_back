package com.ll.hereispaw.domain.post;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class tempController {
  @PostMapping("/temp")
  public String temp() {

    return "temp";
  }
}
