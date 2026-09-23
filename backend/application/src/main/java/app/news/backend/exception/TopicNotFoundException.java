package app.news.backend.exception;

public class TopicNotFoundException extends RuntimeException{
  public TopicNotFoundException(String message){
    super(message);
  }
}
