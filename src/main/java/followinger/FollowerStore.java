package followinger;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.Map;
import java.util.Set;

public class FollowerStore {
    
    private final Map<String, Set<String>> followedBy = Maps.newHashMap(); 
    
    public void addFollower(String follower, String followed) {
        mutableFollowedBy(follower).add(followed);
    }

    private Set<String> mutableFollowedBy(String follower) {
        return followedBy.computeIfAbsent(follower, key -> Sets.newHashSet());
    }

    public Set<String> followedBy(String follower) {
        return ImmutableSet.copyOf(mutableFollowedBy(follower));
    }
}
