package fortscale.collection.jobs.tagging;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by alexp on 23/02/2017.
 */
public class UserTaggingJobTest {

    @Test
    public void testFindDelta_bothListsNull(){
        Map<String, Long> before = null;
        Map<String, Long> after = null;
        Map<String, Long> result = UserTaggingJob.findDelta(before, after);

        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testFindDelta_TwoEmptyList(){
        Map<String, Long> before = new HashMap<>();
        Map<String, Long> after = new HashMap<>();
        Map<String, Long> result = UserTaggingJob.findDelta(before, after);

        Assert.assertEquals(0, result.size());
    }

    @Test
    public void testFindDelta_OneEmptyList(){
        String tag = "tag";
        Long usersCount = 20l;
        Map<String, Long> before = new HashMap<>();
        Map<String, Long> after = new HashMap<>();
        after.put(tag, usersCount);
        Map<String, Long> result = UserTaggingJob.findDelta(before, after);

        Assert.assertEquals(1, result.size());
        Assert.assertTrue(result.keySet().contains(tag));
        Assert.assertEquals(usersCount, result.get(tag));
    }

    @Test
    public void testFindDelta_PositiveDelta(){
        String tag = "tag";
        Long usersCountBefore = 20l;
        Long usersCountAfter = 30l;
        Map<String, Long> before = new HashMap<>();
        before.put(tag, usersCountBefore);
        Map<String, Long> after = new HashMap<>();
        after.put(tag, usersCountAfter);
        Map<String, Long> result = UserTaggingJob.findDelta(before, after);

        Assert.assertEquals(1, result.size());
        Assert.assertTrue(result.keySet().contains(tag));
        Assert.assertEquals(Long.valueOf(usersCountAfter - usersCountBefore), result.get(tag));
    }


    @Test
    public void testFindDelta_NegativeDelta(){
        String tag = "tag";
        Long usersCountAfter = 20l;
        Long usersCountBefore = 30l;
        Map<String, Long> before = new HashMap<>();
        before.put(tag, usersCountBefore);
        Map<String, Long> after = new HashMap<>();
        after.put(tag, usersCountAfter);
        Map<String, Long> result = UserTaggingJob.findDelta(before, after);

        Assert.assertEquals(1, result.size());
        Assert.assertTrue(result.keySet().contains(tag));
        Assert.assertEquals(Long.valueOf((usersCountAfter - usersCountBefore) * -1), result.get(tag));
    }
}
