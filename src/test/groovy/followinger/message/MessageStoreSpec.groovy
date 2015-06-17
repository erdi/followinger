package followinger.message

import spock.lang.Specification

import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset

class MessageStoreSpec extends Specification {
    
    def now = Instant.now()
    def clock = new MutableClock(now)
    def messageStore = new MessageStore(clock)
    
    def "messages are returned for their respective authors in the order in which they are posted"() {
        when:
        messageStore.addMessage("Alice", "I love the weather today")
        messageStore.addMessage("Bob", "Damn! We lost!")
        messageStore.addMessage("Bob", "Good game though.")
        
        then:
        messageStore.messagesFor("Alice") == [new Message("Alice", "I love the weather today", now)]
        messageStore.messagesFor("Bob") == [new Message("Bob", "Damn! We lost!", now), new Message("Bob", "Good game though.", now)]
    }
    
    def "messages are timestamped upon storing"() {
        given:
        def fiveSecondsAgo = now.minusSeconds(5)
        
        when:
        clock.instant = fiveSecondsAgo
        messageStore.addMessage("Bob", "Damn! We lost!")
        
        and:
        clock.instant = now
        messageStore.addMessage("Bob", "Good game though.")
        
        then:
        messageStore.messagesFor("Bob") == [
                new Message("Bob", "Damn! We lost!", fiveSecondsAgo),
                new Message("Bob", "Good game though.", now)
        ]
    }
    
    private final class MutableClock extends Clock {

        private final static ZoneOffset zoneOffset = ZoneOffset.UTC;
        private Instant instant;
        
        MutableClock(Instant instant) {
            this.instant = instant
        }
        
        ZoneId getZone() {
            zoneOffset
        }

        Clock withZone(ZoneId zone) {
            throw new UnsupportedOperationException()
        }

        Instant instant() {
            return instant
        }
    }
}
