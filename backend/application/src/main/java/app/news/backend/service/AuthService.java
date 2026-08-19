package app.news.backend.service;

import app.news.backend.dto.request.LoginRequest;
import app.news.backend.dto.request.RegisterRequest;
import app.news.backend.exception.UserAlreadyExistsException;
import app.news.backend.model.User;
import app.news.backend.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

  @Autowired UserRepository userRepository;

  @Autowired private AuthenticationManager authManager;

  //SAVE NEW USER
  public User save(RegisterRequest request) throws UserAlreadyExistsException{
    if(userRepository.existsByEmail(request.email())) throw new UserAlreadyExistsException("Email already exists!");
    String name = request.name();
    String password = encoder.encode(request.password());
    String email = request.email();

    User user = new User();
    user.setUsername(name);
    user.setPasswordHash(password);
    user.setEmail(email);

    return userRepository.save(user);
  }

  //VERIFY USER LOGIN
  public Authentication verify(LoginRequest user) throws BadCredentialsException, DisabledException, LockedException{
    try {
      Authentication authentication =
          authManager.authenticate(
              new UsernamePasswordAuthenticationToken(user.name(), user.password()));
      return authentication;
    } catch (BadCredentialsException e) {
      throw new BadCredentialsException("Email or Password is Wrong", e);
    } catch (DisabledException e){
      throw new DisabledException("User is disabled", e);
    }catch (LockedException e){
      throw new LockedException("User account is locked", e);
    }
  }

}
