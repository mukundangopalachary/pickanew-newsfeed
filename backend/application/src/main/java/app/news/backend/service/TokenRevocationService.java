package app.news.backend.service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Service;

import app.news.backend.security.JwtService;

@Service
public class TokenRevocationService {

  @Autowired
  private RedisTemplate<String, String> redisTemplate;  

  @Autowired
  JwtService jwtService;

  public void revokeJwt(String jwtToken){

    String jti = jwtService.extractJti(jwtToken);
    Date expiry = jwtService.extractExpiration(jwtToken);

    long ttl = expiry.getTime() - System.currentTimeMillis();

    if(ttl > 0){
      String key = "auth:revoked-access:" + jti;

      Expiration expiration = Expiration.from(ttl,TimeUnit.MILLISECONDS);

      redisTemplate.opsForValue().set(key,"revoked", expiration);
    }
  }

  public boolean isRevoked(String jti){
    String key = "auth:revoked-access:" + jti;

    return redisTemplate.opsForValue().get(key) != null;
  }
}
