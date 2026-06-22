package app.news.backend.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import app.news.backend.model.User;
import app.news.backend.repository.UserRepository;

@Service
public class CustomerUserDetailsService implements UserDetailsService {

  @Autowired
  private UserRepository repository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    if (!repository.existsByEmail(email))
      throw new UsernameNotFoundException("username or password not found");
    User user = repository.findByEmail(email);
    UserPrincipal principal = new UserPrincipal(user);
    return principal;
  }
}
