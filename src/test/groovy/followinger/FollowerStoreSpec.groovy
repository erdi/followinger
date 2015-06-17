package followinger

import spock.lang.Specification

class FollowerStoreSpec extends Specification {
    
    def followerStore = new FollowerStore()
    
    def "followers data can be stored"() {
        when:
        followerStore.addFollower("Charlie", "Alice")
        followerStore.addFollower("Charlie", "Bob")
        followerStore.addFollower("Bob", "Alice")
        
        then:
        followerStore.followedBy("Charlie") == ["Alice", "Bob"] as Set
        followerStore.followedBy("Bob") == ["Alice"] as Set
        followerStore.followedBy("Alice") == [] as Set
    }
}
