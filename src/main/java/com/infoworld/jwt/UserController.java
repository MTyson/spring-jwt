package com.infoworld.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/public")
final class UserController {
  @Autowired
  UserAuthenticationService authentication;
  UserService users;

  @GetMapping("/foo")
  String foo(){
    return "Foobar";
  }
  @PostMapping("/register")
  String register(
    @RequestParam("username") final String username,
    @RequestParam("password") final String password) {
    users.save(User.builder().id(username).username(username).password(password).build());

      return authentication.login(username, password)
        .orElseThrow(() -> new RuntimeException("invalid login and/or password"));
  }

  @PostMapping("/login")
  String login(
    @RequestBody Map<String, String> body) {
      System.out.println("/login: " + body);
      return authentication
        .login(body.get("username"), body.get("password"))
        .orElseThrow(() -> new RuntimeException("invalid login and/or password"));
  }
}
