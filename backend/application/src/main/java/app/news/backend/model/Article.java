package app.news.backend.model;

import java.time.OffsetDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "articles")
public class Article {
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @EqualsAndHashCode.Include
  private Long id;

  @Size(max = 100, message = "Article Title characters can't exceed 100")
  @Column(name = "title", nullable = false)
  private String title;

  @Size(max = 1000)
  @Column(name = "description", nullable = true, columnDefinition = "TEXT")
  private String description;

  @Column(name = "content", nullable = false, columnDefinition = "TEXT")
  private String content;

  @Size(max = 2048)
  @Column(name = "source_url", nullable = false, unique = true, columnDefinition = "TEXT")
  private String sourceUrl;

  // @Column(name = "likes", nullable = false, columnDefinition = "BIGINT DEFAULT 0")
  // private Long likes = 0L;

  @Column(name = "expires_at", nullable = false)
  private OffsetDateTime expiresAt;

  @Column(name = "created_at", nullable = false)
  @CreationTimestamp
  private OffsetDateTime createdAt;

}
