package followinger.message;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public class MessageStore {
    
    private final Map<String, List<Message>> messages = Maps.newHashMap();
    
    private final Clock clock;

    public MessageStore(Clock clock) {
        this.clock = clock;
    }

    private List<Message> mutableMessagesFor(String author) {
        return messages.computeIfAbsent(author, key -> Lists.newArrayList());
    }
    
    public void addMessage(String author, String text) {
        mutableMessagesFor(author).add(new Message(author, text, Instant.now(clock)));
    }
    
    public List<Message> messagesFor(String author) {
        return ImmutableList.copyOf(mutableMessagesFor(author));
    }
}
