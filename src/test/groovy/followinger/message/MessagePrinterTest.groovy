package followinger.message

import followinger.stream.TimingOutReader
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

import static java.time.temporal.ChronoUnit.*

class MessagePrinterTest extends Specification {

    @Shared
    def now = Instant.now()

    def clock = Clock.fixed(now, ZoneOffset.UTC)
    def input = new PipedInputStream()
    def reader = new BufferedReader(new TimingOutReader(new InputStreamReader(input)))
    def output = new PipedOutputStream(input)
    def messagePrinter = new MessagePrinter(output, clock)

    def "messages are printed to the stream that the printer operates on"() {
        when:
        messagePrinter.printMessages([
                new Message("first author", "first message", now),
                new Message("second author", "second message", now)
        ])
        output.close()

        then:
        reader.readLine() == "first message (just now)"
        reader.readLine() == "second message (just now)"
        reader.readLine() == null
    }

    def "messages can be prefixed with author"() {
        when:
        messagePrinter.printMessagesWithAuthors([
                new Message("first author", "first message", now),
                new Message("second author", "second message", now)
        ])
        output.close()

        then:
        reader.readLine() == "first author - first message (just now)"
        reader.readLine() == "second author - second message (just now)"
        reader.readLine() == null
    }

    @Unroll("printing a message that was posted #when")
    def "printed messages contain the time that has elapsed since they were posted"() {
        given:
        def message = new Message("author", "text", timestamp)
        
        when:
        messagePrinter.printMessages([message])
        messagePrinter.printMessagesWithAuthors([message])
        output.close()

        then:
        reader.readLine() == "text ($when)"
        reader.readLine() == "author - text ($when)"
        reader.readLine() == null

        where:
        when             | timestamp
        "11 days ago"    | now.minus(11, DAYS).minus(3, HOURS).minus(10, MINUTES).minusSeconds(5)
        "1 day ago"      | now.minus(1, DAYS).minus(3, HOURS).minus(10, MINUTES).minusSeconds(5)
        "3 hours ago"    | now.minus(3, HOURS).minus(10, MINUTES).minusSeconds(5)
        "1 hour ago"     | now.minus(1, HOURS).minus(10, MINUTES).minusSeconds(5)
        "10 minutes ago" | now.minus(10, MINUTES).minusSeconds(5)
        "1 minute ago"   | now.minus(1, MINUTES).minusSeconds(5)
        "5 seconds ago"  | now.minusSeconds(5)
        "1 second ago"   | now.minusSeconds(1)
        "just now"       | now.minusNanos(1)
    }
}
