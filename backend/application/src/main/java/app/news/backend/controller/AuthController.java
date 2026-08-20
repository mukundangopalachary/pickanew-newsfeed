package app.news.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.news.backend.dto.request.LoginRequest;
import app.news.backend.dto.request.RegisterRequest;
import app.news.backend.dto.response.AuthResponse;
import app.news.backend.dto.response.UserResponse;
import app.news.backend.model.RefreshToken;
import app.news.backend.model.User;
import app.news.backend.security.CustomUserDetailsService;
import app.news.backend.security.JwtService;
import app.news.backend.security.UserPrincipal;
import app.news.backend.service.AuthService;
import app.news.backend.service.CookieService;
import app.news.backend.service.RefreshTokenService;
import app.news.backend.service.TokenRevocationService;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/v1/auth/")
public class AuthController {

  @Autowired RefreshTokenService refreshTokenService;

  @Autowired AuthService authService;

  @Autowired CookieService cookieService;

  @Autowired JwtService jwtService;

  @Autowired
  TokenRevocationService tokenRevocationService;

  @Autowired CustomUserDetailsService userDetailsService;

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest, HttpServletResponse response) throws BadCredentialsException{
    
    User user = authService.save(registerRequest);
    
    LoginRequest req = new LoginRequest(user.getUsername(), user.getEmail(), registerRequest.password());
    Authentication authentication = authService.verify(req);

    //user details from the AuthenticationManager
    UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
    String jwt = jwtService.generateToken(principal);
    String refreshToken = refreshTokenService.createRefreshToken(user).getToken();

    cookieService.setJwtCookie(response, jwt);
    cookieService.setRefreshTokenCookie(response, refreshToken);

    return ResponseEntity.status(HttpStatus.CREATED).body("Registration Successful!");
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response){
    
    try{
      Authentication authentication = authService.verify(loginRequest);
      
      UserPrincipal principal = (UserPrincipal)authentication.getPrincipal();
      User user = principal.getUser();


      String jwt = jwtService.generateToken(principal);
      String refreshToken = refreshTokenService.createRefreshToken(user).getToken();

      cookieService.setJwtCookie(response, jwt);
      cookieService.setRefreshTokenCookie(response, refreshToken);

      return ResponseEntity.ok(new AuthResponse(new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRole().toString(), user.getProvider().toString()) , "Login successful"));

    }catch(AuthenticationException e){
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Wrong Username or Password");
    }
    
  }


  // request comes from frontend when status => 401 on expiry of jwt and refreshToken(expiredOrNot)
  @PostMapping("/refresh")
  public ResponseEntity<?> refresh(HttpServletResponse response, @CookieValue("refreshToken") String refreshToken){
    RefreshToken newRefreshToken = refreshTokenService.rotateRefreshToken(refreshToken);
    User user = newRefreshToken.getUser();

    UserPrincipal principal = (UserPrincipal) userDetailsService.loadUserByUsername(user.getEmail());
  
    String jwt = jwtService.generateToken(principal);

    cookieService.setJwtCookie(response, jwt);
    cookieService.setRefreshTokenCookie(
        response,
        newRefreshToken.getToken()
    );
    
    return ResponseEntity.ok("Token Refreshed");
  }

  @PostMapping("/logout")
  public ResponseEntity<?> logout(HttpServletResponse response, @CookieValue("jwt") String jwtToken, @CookieValue("refreshToken") String refreshToken){
    
    if(jwtToken != null)tokenRevocationService.revokeJwt(jwtToken);

    refreshTokenService.revokeIfExists(refreshToken);

    cookieService.clearJwtCookie(response);
    cookieService.clearRefreshTokenCookie(response);

    return ResponseEntity.noContent().build();
  }
}
