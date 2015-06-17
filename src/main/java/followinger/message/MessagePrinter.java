package followinger.message;

import com.google.common.base.Function;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.time.temporal.ChronoUnit.*;

public class MessagePrinter {

    private final PrintWriter out;
    private final Clock clock;
    
    public MessagePrinter(OutputStream stream, Clock clock) {
        this.out = new PrintWriter(new OutputStreamWriter(stream), true);
        this.clock = clock;
    }
    
    private String formatTimeSince(Instant timestamp, Instant now) {
        return Stream.of(DAYS, HOURS, MINUTES, SECONDS).map(unit -> {
            long amount = unit.between(timestamp, now);
            return amount > 0 ? new PassedDuration(amount, unit) : null;
        }).filter(Objects::nonNull).map(PassedDuration::format).findFirst().orElse("just now");
    }

    private void printMessages(List<Message> messages, Function<Message, String> textExtractor) {
        Instant now = Instant.now(clock);
        messages.stream()
                .map(message -> format("%s (%s)", textExtractor.apply(message), formatTimeSince(message.getTimestamp(), now)))
                .forEach(out::println);
    }
    
    public void printMessages(List<Message> messages) {
        printMessages(messages, Message::getText);
    }

    public void printMessagesWithAuthors(List<Message> messages) {
        printMessages(messages, message -> String.format("%s - %s", message.getAuthor(), message.getText()));
    }
    private static class PassedDuration {
        private final long amount;
        private final ChronoUnit unit;

        private PassedDuration(long amount, ChronoUnit unit) {
            this.amount = amount;
            this.unit = unit;
        }

        public String format() {
            String unitName = unit.name().toLowerCase();
            unitName = amount == 1 ? unitName.substring(0, unitName.length() - 1) : unitName;
            return String.format("%d %s ago", amount, unitName);
        }
    }
}
