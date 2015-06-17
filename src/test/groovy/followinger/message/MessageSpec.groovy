package followinger.message

import spock.lang.Specification

import java.time.Instant

class MessageSpec extends Specification {
    
    def now = Instant.now()
    
    def "equality"() {
        given:
        def message = new Message("Alice", "text", now)
        
        expect:
        message == message
        message == new Message("Alice", "text", now)
        message != new Message("Bob", "text", now)
        message != new Message("Alice", "other text", now)
        message != new Message("Alice", "text", now.plusSeconds(1))
    }
}
