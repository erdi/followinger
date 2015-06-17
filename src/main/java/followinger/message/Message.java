package followinger.message;

import java.time.Instant;
import java.util.Objects;

public class Message {
    private final String text;
    private final Instant timestamp;

    public Message(String text, Instant timestamp) {
        this.text = text;
        this.timestamp = timestamp;
    }

    public String getText() {
        return text;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public int hashCode() {
        return Objects.hash(text, timestamp);
    }

    public boolean equals(Object obj) {
        if (obj instanceof Message) {
            Message other = (Message) obj;
            return Objects.equals(text, other.text) && Objects.equals(timestamp, other.timestamp);
        }
        return false;
    }
    
    public String toString() {
        return String.format("'%s' posted on %s", text, timestamp);
    }
}
