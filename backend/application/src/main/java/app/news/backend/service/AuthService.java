package app.news.backend.service;

import app.news.backend.dto.request.LoginRequest;
import app.news.backend.model.User;
import app.news.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

  @Autowired
  UserRepository userRepository;

  @Autowired
  private AuthenticationManager authManager;

  public void save(LoginRequest request) {
    String name = request.name();
    String password = encoder.encode(request.password());
    String email = request.email();

    User user = new User();
    user.setUsername(name);
    user.setPasswordHash(password);
    user.setEmail(email);

    userRepository.save(user);
  }

  public boolean verify(LoginRequest user) {
    try {
      Authentication authentication = authManager.authenticate(
          new UsernamePasswordAuthenticationToken(user.name(), user.password()));
      return true;
    } catch (BadCredentialsException e) {
      return false;
    }
  }
}
