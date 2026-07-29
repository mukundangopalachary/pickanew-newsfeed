package app.news.backend.controller;

import app.news.backend.dto.request.LoginRequest;
import app.news.backend.dto.response.AuthResponse;
import app.news.backend.model.User;
import app.news.backend.service.AuthService;
import app.news.backend.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/")
public class AuthController {

  @Autowired RefreshTokenService refreshTokenService;

  @Autowired AuthService authService;

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@PathVariable LoginRequest request) {
    User user = new User();

    return new ResponseEntity(response, HttpStatus.OK);
  }
}
