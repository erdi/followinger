package followinger

import followinger.message.Message
import followinger.message.MessagePrinter
import followinger.message.MessageStore
import spock.lang.Specification
import spock.lang.Unroll

import java.time.Instant

import static java.time.temporal.ChronoUnit.MINUTES

class CommandProcessorSpec extends Specification {

    MessageStore messageStore
    FollowerStore followerStore
    MessagePrinter printer = Mock()

    CommandProcessor getProcessor() {
        new CommandProcessor(messageStore, printer, followerStore)
    }

    @Unroll("processing command: #command")
    def "processing message post command results in a message being stored and no messages being printed"() {
        given:
        messageStore = Mock()

        when:
        processor.processCommand(command)

        then:
        1 * messageStore.addMessage(author, text)
        0 * printer._

        where:
        command                             | author  | text
        "Alice -> I love the weather today" | "Alice" | "I love the weather today"
        "Bob -> Damn! We lost!"             | "Bob"   | "Damn! We lost!"
    }

    @Unroll("processing command: #author")
    def "processing timeline view command results in messages from that timeline being printed"() {
        given:
        messageStore = Stub() {
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
        messagesInStore = messagesTextInStore.collect { new Message(author, it, now) }
        messagesPrinted = messagesTextPrinted.collect { new Message(author, it, now) }
    }
    
    def "processing following command"() {
        given:
        followerStore = Mock()
        
        when:
        processor.processCommand("Charlie follows Bob")
        
        then:
        1 * followerStore.addFollower("Charlie", "Bob")
        0 * printer._
    }
    
    def "processing wall command"() {
        given:
        
        followerStore = Stub() {
            followedBy("Charlie") >> ["Alice", "Bob"]
        }
        messageStore = Stub() {
            messagesFor("Charlie") >> [charliesMessage] 
            messagesFor("Alice") >> [alicesMessage] 
            messagesFor("Bob") >> [firstBobsMessage, secondBobsMessage] 
        }
        
        when:
        processor.processCommand("Charlie wall")
        
        then:
        1 * printer.printMessagesWithAuthors([charliesMessage, secondBobsMessage, firstBobsMessage, alicesMessage])
        
        where:
        now = Instant.now()
        charliesMessage = new Message("Charlie", "I'm in New York today! Anyone want to have a coffee?", now)
        alicesMessage = new Message("Alice", "I love the weather today", now.minus(5, MINUTES))
        firstBobsMessage = new Message("Bob", "Damn! We lost!", now.minus(2, MINUTES))
        secondBobsMessage = new Message("Bob", "Good game though", now.minus(1, MINUTES))
    }
}
