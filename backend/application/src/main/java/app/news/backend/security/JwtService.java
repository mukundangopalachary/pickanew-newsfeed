package app.news.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

// Service class responsible for handling JSON Web Token (JWT) operations.
// This includes generating tokens, extracting information (claims) from tokens,
// and validating tokens.
@Service
public class JwtService {

  private String secret_key = "";

  // Constructs a new JwtService.
  // Initializes a secure secret key used for signing and verifying JWTs
  // using the HMAC-SHA256 algorithm.
  // Throws NoSuchAlgorithmException if the specified algorithm is not available.
  public JwtService() throws NoSuchAlgorithmException {
    KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
    SecretKey secretKey = keyGenerator.generateKey();
    secret_key = Base64.getEncoder().encodeToString(secretKey.getEncoded());
  }

  // Retrieves the secret key used for JWT signing and verification.
  // Returns the SecretKey derived from the base64 encoded secret string.
  private SecretKey getKey() {
    byte[] keyBytes = Decoders.BASE64.decode(secret_key);
    System.out.println(keyBytes);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  // Generates a new JWT token for the specified username.
  // The token includes the username as the subject, the issue date, and an
  // expiration date.
  // Takes the username for which to generate the token and returns the generated
  // JWT token as a string.
  public String generateToken(String username) {

    Map<String, Object> claims = new HashMap<>();
    return Jwts.builder()
        .claims(claims)
        .subject(username)
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + 60 * 60 * 100))
        .signWith(getKey())
        .compact();

  }

  // Parses the given JWT token and extracts all its claims.
  // It also verifies the token's signature using the secret key.
  // Takes the JWT token to parse and returns the Claims extracted from the token.
  private Claims extractAllClaims(String token) {
    return Jwts.parser()
        .verifyWith(getKey())
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  // Extracts a specific claim from the given JWT token using a provided resolver
  // function.
  // Takes the JWT token and a function that defines how to extract the desired
  // claim.
  // Returns the extracted claim.
  private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
    final Claims claims = extractAllClaims(token);
    return claimResolver.apply(claims);
  }

  // Extracts the username (subject) from the given JWT token.
  // Returns the extracted username.
  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  // Validates a given JWT token against the provided user details.
  // Checks if the username in the token matches the user's username and if the
  // token is not expired.
  // Returns true if the token is valid, false otherwise.
  public boolean validateToken(String token, UserDetails userDetails) {
    String userName = extractUsername(token);
    return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
  }

  // Checks if the given JWT token has expired.
  // Returns true if the token's expiration date is before the current time, false
  // otherwise.
  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  // Extracts the expiration date from the given JWT token.
  // Returns the token's expiration date.
  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }
}
