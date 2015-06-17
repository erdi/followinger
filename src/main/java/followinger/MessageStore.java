package followinger;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public class MessageStore {
    
    private final Map<String, List<String>> messages = Maps.newHashMap();
    
    public List<String> mutableMessagesFor(String author) {
        return messages.computeIfAbsent(author, key -> Lists.newArrayList());
    }
    
    public void addMessage(String author, String text) {
        mutableMessagesFor(author).add(text);
    }
    
    public List<String> messagesFor(String author) {
        return ImmutableList.copyOf(mutableMessagesFor(author));
    }
}
