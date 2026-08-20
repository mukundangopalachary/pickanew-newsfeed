package app.news.backend.service;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import app.news.backend.exception.TokenExpiredException;
import app.news.backend.exception.TokenNotFoundException;
import app.news.backend.model.RefreshToken;
import app.news.backend.model.User;
import app.news.backend.repository.RefreshTokenRepository;

@Service
public class RefreshTokenService {
  private static final SecureRandom secureRandom = new SecureRandom();
  private static final int TOKEN_LENGTH = 64;

  @Autowired private RefreshTokenRepository refreshTokenRepository;
  // @Autowired private UserRepository userRepository;

  public RefreshToken verifyRefreshToken(String refreshToken) {
    Optional<RefreshToken> opt = refreshTokenRepository.findByToken(refreshToken);

    RefreshToken token = opt.orElseThrow(() -> new TokenNotFoundException("Token is not found"));

    boolean revoked = token.isRevoked();

    if (revoked) {
      throw new TokenExpiredException("The token is revoked");
    }

    if (OffsetDateTime.now().isAfter(token.getExpiresAt())) {
      throw new TokenExpiredException("The token has expired");
    }

    return token;
  }

  public RefreshToken createRefreshToken(User user) throws UsernameNotFoundException {

    RefreshToken refreshToken = new RefreshToken();

    refreshToken.setUser(user);

    OffsetDateTime now = OffsetDateTime.now();

    refreshToken.setExpiresAt(now.plusDays(30));

    byte[] bytes = new byte[TOKEN_LENGTH];
    secureRandom.nextBytes(bytes);

    String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    refreshToken.setToken(token);

    return refreshTokenRepository.save(refreshToken);
  }

  @Transactional
  public RefreshToken rotateRefreshToken(String oldToken) {
    RefreshToken existing = verifyRefreshToken(oldToken);
    existing.setRevoked(true);

    RefreshToken newToken = createRefreshToken(existing.getUser());
    return newToken;
  }

  @Transactional
  public RefreshToken revokeRefreshToken(String token) {
    RefreshToken refreshToken =
        refreshTokenRepository
            .findByToken(token)
            .orElseThrow(() -> new TokenNotFoundException("Token Not Found!"));

    refreshToken.setRevoked(true);
    return refreshToken;
  }

  @Transactional
  public void revokeIfExists(String token) {

    if (token == null) {
        return;
    }

    refreshTokenRepository
        .findByToken(token)
        .ifPresent(refreshToken -> {
            refreshToken.setRevoked(true);
        });
  }

  @Transactional
  public void revokeAllUserTokens(User user) {
    refreshTokenRepository.revokeAllByUser(user);
  }

  @Scheduled(cron = "0 0 * * * *")
  @Transactional
  public void deleteExpiredTokens() {
    refreshTokenRepository.deleteExpiredTokens(OffsetDateTime.now());
  }
}
