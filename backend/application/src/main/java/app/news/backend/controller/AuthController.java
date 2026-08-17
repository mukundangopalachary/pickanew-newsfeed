package app.news.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.news.backend.dto.request.LoginRequest;
import app.news.backend.dto.request.RegisterRequest;
import app.news.backend.dto.response.AuthResponse;
import app.news.backend.dto.response.UserResponse;
import app.news.backend.model.User;
import app.news.backend.security.JwtService;
import app.news.backend.security.UserPrincipal;
import app.news.backend.service.AuthService;
import app.news.backend.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/v1/auth/")
public class AuthController {

  @Autowired RefreshTokenService refreshTokenService;

  @Autowired AuthService authService;

  @Autowired JwtService jwtService;

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest, HttpServletResponse response) throws BadCredentialsException{
    
    User user = authService.save(registerRequest);
    LoginRequest req = new LoginRequest(user.getUsername(), user.getEmail(), registerRequest.password());
    Authentication authentication = authService.verify(req);

    //user details from the AuthenticationManager
    UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
    String jwt = jwtService.generateToken(principal);
    String refreshToken = refreshTokenService.createRefreshToken(user).getToken();

    authService.setJwtCookie(response, jwt);
    authService.setRefreshTokenCookie(response, refreshToken);

    return ResponseEntity.status(HttpStatus.OK).body("Registration Successful!");
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response){
    
    try{
      Authentication authentication = authService.verify(loginRequest);
      
      UserPrincipal principal = (UserPrincipal)authentication.getPrincipal();
      User user = principal.getUser();


      String jwt = jwtService.generateToken(principal);
      String refreshToken = refreshTokenService.createRefreshToken(user).getToken();

      authService.setJwtCookie(response, jwt);
      authService.setRefreshTokenCookie(response, refreshToken);

      return ResponseEntity.ok(new AuthResponse(new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRole().toString(), user.getProvider().toString()) , "Login successful"));

    }catch(AuthenticationException e){
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Wrong Username or Password");
    }
    

  }
}
