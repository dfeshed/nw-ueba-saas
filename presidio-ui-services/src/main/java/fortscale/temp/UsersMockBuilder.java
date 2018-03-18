package fortscale.temp;

import fortscale.domain.core.Severity;
import fortscale.domain.core.User;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by shays on 05/07/2017.
 */
public class UsersMockBuilder {


    private String id;
    private String usernmae;
    private String displayName;
    private Set<String> tags;
    private boolean watched;
    private int score;
    private Severity severity;
    private Integer alertsCounts;

    public UsersMockBuilder(int serial){
        this.id=id+1;
        this.usernmae="user"+serial+ HardCodedMocks.BIGCONPANY_COM;
        this.displayName= HardCodedMocks.USER_BASE +serial;
        this.tags = new HashSet<>(Arrays.asList("admin"));
        this.watched = false;
        this.score = HardCodedMocks.DEFAULT_SCORE;
        this.severity = Severity.Medium;
        this.alertsCounts = 5;


    }

    public UsersMockBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public UsersMockBuilder setUsernmae(String usernmae) {
        this.usernmae = usernmae;
        return this;
    }

    public UsersMockBuilder setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public UsersMockBuilder setTags(Set<String> tags) {
        this.tags = tags;
        return this;
    }

    public UsersMockBuilder setWatched(boolean watched) {
        this.watched = watched;
        return this;
    }

    public UsersMockBuilder setScore(int score) {
        this.score = score;

        if (score<50){
            this.severity = Severity.Low;
        } else if (score<75){
            this.severity = Severity.Medium;
        } else if (score<90){
            this.severity = Severity.High;
        } else {
            this.severity = Severity.Critical;
        }
        return this;
    }

    public User createInstance(){
        User user = new User();
        user.setTags(this.tags);
        user.setMockId(this.id);
        user.setUsername(this.usernmae);
        user.setDisplayName(this.displayName);
        user.setScore(this.score);
        user.setScoreSeverity(this.severity);
        user.setAlertsCount(alertsCounts);

        return user;
    }
}
