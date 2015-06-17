package followinger

import followinger.message.Message
import followinger.message.MessagePrinter
import followinger.message.MessageStore
import spock.lang.Specification
import spock.lang.Unroll

import java.time.Instant

class CommandProcessorSpec extends Specification {

    MessageStore store
    MessagePrinter printer = Mock()

    CommandProcessor getProcessor() {
        new CommandProcessor(store, printer)
    }

    @Unroll("processing command: #command")
    def "processing message post command results in a message being stored and no messages being printed"() {
        given:
        store = Mock()

        when:
        processor.processCommand(command)

        then:
        1 * store.addMessage(author, text)
        0 * printer._

        where:
        command                             | author  | text
        "Alice -> I love the weather today" | "Alice" | "I love the weather today"
        "Bob -> Damn! We lost!"             | "Bob"   | "Damn! We lost!"
    }

    @Unroll("processing command: #author")
    def "processing timeline view command results in messages from that timeline being printed"() {
        given:
        store = Stub() {
            messagesFor(author) >> messagesInStore
        }

        when:
        processor.processCommand(author)

        then:
        1 * printer.printMessages(messagesPrinted)

        where:
        author  | messagesTextInStore                     | messagesTextPrinted
        "Bob"   | ["Damn! We lost!", "Good game though."] | ["Good game though.", "Damn! We lost!"]
        "Alice" | []                                      | []

        now = Instant.now()
        messagesInStore = messagesTextInStore.collect { new Message(it, now) }
        messagesPrinted = messagesTextPrinted.collect { new Message(it, now) }
    }
}
