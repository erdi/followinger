package followinger;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import followinger.message.Message;
import followinger.message.MessagePrinter;
import followinger.message.MessageStore;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommandProcessor {
    
    private final List<CommandParser> parsers;

    public CommandProcessor(MessageStore messageStore, MessagePrinter messagePrinter, FollowerStore followerStore) {
        this.parsers = ImmutableList.of(
                new CommandParser("(?<author>\\w+) -> (?<text>.*)", matcher -> messageStore.addMessage(matcher.group("author"), matcher.group("text"))),
                new CommandParser("(?<author>\\w+)", matcher -> {
                    List<Message> messages = messageStore.messagesFor(matcher.group("author"));
                    messagePrinter.printMessages(Lists.reverse(messages));
                }),
                new CommandParser("(?<follower>\\w+) follows (?<followed>\\w+)", matcher -> {
                    followerStore.addFollower(matcher.group("follower"), matcher.group("followed"));
                }),
                new CommandParser("(?<user>\\w+) wall", matcher -> {
                    String user = matcher.group("user");
                    Set<String> followed = followerStore.followedBy(user);
                    List<Message> messages = Stream.concat(followed.stream(), Stream.of(user))
                            .flatMap(author -> messageStore.messagesFor(author).stream())
                            .sorted(Comparator.comparing(Message::getTimestamp).reversed())
                            .collect(Collectors.toList());
                    messagePrinter.printMessagesWithAuthors(messages);
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
