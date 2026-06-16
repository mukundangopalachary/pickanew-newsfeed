package app.news.backend.model;

import java.time.OffsetDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Size;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Size(max = 100, message = "Name cant exceed more than 100 characters!")
  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "email", nullable = true, unique = true)
  private String email;

  @Column(name = "password_hash", nullable = false)
  @JsonProperty("password_hash")
  private String passwordHash;

  @Column(name = "role", nullable = false, columnDefinition = "VARCHAR DEFAULT USER")
  private Role role;

  @Column(name = "joined_at", nullable = false, updatable = false)
  @CreationTimestamp
  private OffsetDateTime joinedAt;

}
