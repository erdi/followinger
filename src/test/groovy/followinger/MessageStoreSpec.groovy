package followinger

import spock.lang.Specification

class MessageStoreSpec extends Specification {
    
    def messageStore = new MessageStore()
    
    def "messages are returned for their respective authors in the order in which they are posted"() {
        when:
        messageStore.addMessage("Alice", "I love the weather today")
        messageStore.addMessage("Bob", "Good game though.")
        messageStore.addMessage("Bob", "Damn! We lost!")
        
        then:
        messageStore.messagesFor("Alice") == ["I love the weather today"]
        messageStore.messagesFor("Bob") == ["Good game though.", "Damn! We lost!"]
    }
}
