package followinger

import followinger.fixture.FollowingerRunner
import spock.lang.AutoCleanup
import spock.lang.Specification

class EndToEndSpec extends Specification {

    @AutoCleanup
    FollowingerRunner app = new FollowingerRunner()
    
    def "can post and read messages for a single user"() {
        when:
        app.postMessage("Alice", "I love the weather today")
        app.showMessages("Alice")
        
        then:
        app.messagePrinted("I love the weather today")
    }
}