package followinger;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import followinger.message.Message;
import followinger.message.MessagePrinter;
import followinger.message.MessageStore;

import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandProcessor {
    
    private final List<CommandParser> parsers;

    public CommandProcessor(MessageStore messageStore, MessagePrinter messagePrinter) {
        this.parsers = ImmutableList.of(
                new CommandParser("(?<author>\\w+) -> (?<text>.*)", matcher -> messageStore.addMessage(matcher.group("author"), matcher.group("text"))),
                new CommandParser("(?<author>\\w+)", matcher -> {
                    List<Message> messages = messageStore.messagesFor(matcher.group("author"));
                    messagePrinter.printMessages(Lists.reverse(messages));
                })
        );
    }

    void processCommand(String command) {
        parsers.stream().anyMatch(parser -> parser.handle(command));
    }
    
    private final static class CommandParser {
        private final Pattern pattern;
        private final Consumer<Matcher> handler;
        
        public CommandParser(String regex, Consumer<Matcher> handler) {
            this.handler = handler;
            this.pattern = Pattern.compile(regex);
        }
        
        public boolean handle(String command) {
            Matcher matcher = pattern.matcher(command);
            if (matcher.matches()) {
                handler.accept(matcher);
                return true;
            }
            return false;
        }
    }
}
