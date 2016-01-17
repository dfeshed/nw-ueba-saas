package fortscale.streaming.task;

import fortscale.utils.logging.Logger;
import org.apache.samza.config.Config;
import org.apache.samza.container.TaskName;
import org.apache.samza.task.TaskContext;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

/**
 * Created by rans on 17/01/16.
 */
public interface TestTask {

    public static int TOTAL_TASK_NAMES = 1;
    static Logger logger = Logger.getLogger(TestTask.class);

    Map tasks = new HashMap<TaskName, TestTask>();
    CountDownLatch allTasksRegistered = new CountDownLatch(TOTAL_TASK_NAMES);
    CountDownLatch initFinished = new CountDownLatch(1);
    CountDownLatch gotMessage = new CountDownLatch(1);

    public default void initTest(Config config, TaskContext context) {
        register(context.getTaskName(), this);
        initFinished.countDown();

    }

    public default void processTest(){
        gotMessage.countDown();
    }

    static public Map<TaskName, TestTask> getTasks(){
        return tasks;
    }

    /**
     * Static method that tasks can use to register themselves with. Useful so
     * we don't have to sneak into the ThreadJob/SamzaContainer to get our test
     * tasks.
     */
    default public void register(TaskName taskName, TestTask task) {
        tasks.put(taskName, task);
        allTasksRegistered.countDown();
    }

    static void awaitTaskRegistered() throws InterruptedException {
        allTasksRegistered.await(120, TimeUnit.SECONDS);
        assertEquals(0, allTasksRegistered.getCount());
        assertEquals(TOTAL_TASK_NAMES, tasks.size());
    }
}
