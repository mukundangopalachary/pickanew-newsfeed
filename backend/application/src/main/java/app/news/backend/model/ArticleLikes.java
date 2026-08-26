package app.news.backend.model;

import java.time.OffsetDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.persistence.FetchType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "article_likes")
public class ArticleLikes {
  
  @EmbeddedId
  private ArticleUserId articleUserId;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("articleId")
  @JoinColumn(name = "article_id", nullable = false)
  private Article article;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("userID")
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "created_at", nullable = false)
  @CreationTimestamp
  private OffsetDateTime createdAt;
}
