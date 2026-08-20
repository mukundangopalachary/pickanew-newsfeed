package app.news.backend.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

  @Value("${app.jwt.secret}")
  private String secretKey;

  @Value("${app.jwt.expiration}")
  private long expiration;

  private SecretKey getKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  public String generateToken(UserPrincipal principal) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("userId", principal.getId());
    claims.put("role", principal.getUser().getRole().name());

    String jti = UUID.randomUUID().toString();
    claims.put("jti", jti);

    return Jwts.builder()
        .claims(claims)
        .subject(principal.getEmail())
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + expiration))
        .signWith(getKey())
        .compact();
  }

  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public String extractJti(String token) {
    return extractClaim(token, claims -> claims.get("jti", String.class));
  }

  public boolean validateToken(String token, UserDetails userDetails) {
    String email = extractUsername(token);
    return email.equals(userDetails.getUsername()) && !isTokenExpired(token);
  }

  public boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  public Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
    return claimResolver.apply(extractAllClaims(token));
  }

  public Claims extractAllClaims(String token) {
    return Jwts.parser()
        .verifyWith(getKey())
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }
}
