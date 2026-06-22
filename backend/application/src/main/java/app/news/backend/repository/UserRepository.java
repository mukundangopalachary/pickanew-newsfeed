package app.news.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import app.news.backend.model.User;

// Basic
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  boolean existsByUsername(String username);

  boolean existsByEmail(String username);

  User findByUsername(String username);

  User findByEmail(String email);
}
