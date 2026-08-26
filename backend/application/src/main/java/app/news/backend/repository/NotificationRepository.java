package app.news.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import app.news.backend.model.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long>{
  
}