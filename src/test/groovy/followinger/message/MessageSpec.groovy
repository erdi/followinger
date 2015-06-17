package followinger.message

import spock.lang.Specification

import java.time.Instant

class MessageSpec extends Specification {
    
    def now = Instant.now()
    
    def "equality"() {
        given:
        def message = new Message("text", now)
        
        expect:
        message == message
        message == new Message("text", now)
        message != new Message("other text", now)
        message != new Message("text", now.plusSeconds(1))
    }
}
