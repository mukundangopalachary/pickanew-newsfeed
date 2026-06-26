package app.news.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.news.backend.dto.request.LoginRequest;
import app.news.backend.dto.response.AuthResponse;
import app.news.backend.service.AuthService;
import app.news.backend.service.RefreshTokenService;

@RestController
@RequestMapping("/api/v1/auth/")
public class AuthController {

  @Autowired
  RefreshTokenService refreshTokenService;

  @Autowired
  AuthService authService;

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@PathVariable LoginRequest request) {

    return new ResponseEntity<>().ok();
  }
}
