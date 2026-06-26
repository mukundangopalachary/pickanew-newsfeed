package app.news.backend.repository;

import java.time.OffsetDateTime;
import org.springframework.data.jpa.repository.JpaRepository;

import app.news.backend.model.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
  boolean existsByExpiresAtBefore(OffsetDateTime time);

  String findTokenById(Long id);
}
