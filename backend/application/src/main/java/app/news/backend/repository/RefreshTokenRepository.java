package app.news.backend.repository;

import app.news.backend.model.RefreshToken;
import app.news.backend.model.User;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

  // used on refresh + logout — look up token by its string value
  Optional<RefreshToken> findByToken(String token);

  // used on logout all devices + account deletion
  @Modifying
  @Query("DELETE FROM RefreshToken t WHERE t.user = :user")
  void revokeAllByUser(@Param("user") User user);

  // cleanup job — remove expired tokens from DB
  @Modifying
  @Query("DELETE FROM RefreshToken t WHERE t.expiresAt < :now")
  void deleteExpiredTokens(@Param("now") OffsetDateTime now);
}
