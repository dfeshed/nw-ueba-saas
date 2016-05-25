package fortscale.collection.activity;

import fortscale.collection.jobs.activity.UserActivityJob;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author gils
 *         24/05/2016
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:META-INF/spring/collection-context-user-activity-test.xml" })
public class UserActivityTest {

    @Autowired
    private UserActivityJob userActivityJob;

    @Test
    public void testUserActivity() {
        try {
            userActivityJob.runSteps();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
