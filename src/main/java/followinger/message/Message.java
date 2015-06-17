package followinger.message;

import java.time.Instant;
import java.util.Objects;

public class Message {
    private final String text;
    private final Instant timestamp;
    private final String author;

    public Message(String author, String text, Instant timestamp) {
        this.author = author;
        this.text = text;
        this.timestamp = timestamp;
    }

    public String getText() {
        return text;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getAuthor() {
        return author;
    }

    public int hashCode() {
        return Objects.hash(author, text, timestamp);
    }

    public boolean equals(Object obj) {
        if (obj instanceof Message) {
            Message other = (Message) obj;
            return Objects.equals(text, other.text) && Objects.equals(timestamp, other.timestamp) && Objects.equals(author, other.author);
        }
        return false;
    }
    
    public String toString() {
        return String.format("'%s' posted by %s on %s", text, author, timestamp);
    }
}
