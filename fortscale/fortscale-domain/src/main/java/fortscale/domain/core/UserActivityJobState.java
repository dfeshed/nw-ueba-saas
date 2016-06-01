package fortscale.domain.core;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.TreeSet;

/**
 * @author gils
 * 01/06/2016
 */
@Document(collection = UserActivityJobState.COLLECTION_NAME)
public class UserActivityJobState extends AbstractDocument{
    public static final String COLLECTION_NAME = "user_activity_job_state";

    public static final String LAST_RUN_FIELD = "lastRun";
    public static final String COMPLETED_EXECUTION_DAYS_FIELD = "completedExecutionDays";

    @Field(LAST_RUN_FIELD)
    private Long lastRun;

    @Field(COMPLETED_EXECUTION_DAYS_FIELD)
    private TreeSet<Long> completedExecutionDays = new TreeSet<Long>() {
    };

    public TreeSet<Long> getCompletedExecutionDays() {
        return completedExecutionDays;
    }

    public Long getLastRun() {
        return lastRun;
    }

    public void setLastRun(Long lastRun) {
        this.lastRun = lastRun;
    }
}
