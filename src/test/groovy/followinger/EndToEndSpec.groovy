package followinger

import followinger.fixture.FollowingerRunner
import spock.lang.Specification

class EndToEndSpec extends Specification implements FollowingerRunner {

    def postAlicesMessage() {
        postMessage("Alice", "I love the weather today")
    }
    
    def postBobsMessages() {
        postMessage("Bob", "Damn! We lost!")
        postMessage("Bob", "Good game though.")
    }
    
    def "can post and read messages for a single user"() {
        when:
        postMessage("Alice", "I love the weather today")
        showMessages("Alice")
        
        then:
        messagePrinted("I love the weather today")
    }
    
    def "multiple users can post and read messages"() {
        when:
        postAlicesMessage()
        postBobsMessages()
        showMessages("Alice")

        then:
        messagePrinted("I love the weather today")
        
        when:
        showMessages("Bob")
        
        then:
        messagePrinted("Good game though.")
        messagePrinted("Damn! We lost!")
    }
    
    def "following users"() {
        given:
        postAlicesMessage()
        postBobsMessages()
        postMessage("Charlie", "I'm in New York today! Anyone want to have a coffee?")
        
        when:
        follow("Charlie", "Alice")
        
        and:
        showWall("Charlie")
        
        then:
        messagePrinted("Charlie", "I'm in New York today! Anyone want to have a coffee?")
        messagePrinted("Alice", "I love the weather today")
        
        when:
        follow("Charlie", "Bob")
        
        and:
        showWall("Charlie")
        
        then:
        messagePrinted("Charlie", "I'm in New York today! Anyone want to have a coffee?")
        messagePrinted("Bob", "Good game though.")
        messagePrinted("Bob", "Damn! We lost!")
        messagePrinted("Alice", "I love the weather today")
    }
}