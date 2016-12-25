package fortscale.streaming.task;

import fortscale.streaming.task.message.ProcessMessageContext;
import fortscale.utils.ResettableCountDownLatch;
import fortscale.utils.logging.Logger;
import org.apache.samza.config.Config;
import org.apache.samza.container.TaskName;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by rans on 17/01/16.
 */
public interface TestTask {

    public static int TOTAL_TASK_NAMES = 1;
    static Logger logger = Logger.getLogger(TestTask.class);

    Map tasks = new HashMap<TaskName, TestTask>();
    ResettableCountDownLatch allTasksRegistered = new ResettableCountDownLatch(TOTAL_TASK_NAMES);
    List<String> received = new ArrayList<String>();
    ResettableCountDownLatch initFinished = new ResettableCountDownLatch(1);
    ResettableCountDownLatch gotMessage = new ResettableCountDownLatch(1);

    public default void initTest(Config config, TaskContext context) {
        register(context.getTaskName(), this);
        initFinished.countDown();

    }

    public default void processTest(ProcessMessageContext message, MessageCollector collector,
                                    TaskCoordinator coordinator) {
        String messageText = message.getMessageAsString();
        received.add(messageText);
        gotMessage.countDown();
        coordinator.commit(TaskCoordinator.RequestScope.ALL_TASKS_IN_CONTAINER);
    }

    public default void awaitMessage() throws InterruptedException {
        assertTrue("Timed out of waiting for message rather than received one.", gotMessage.await(60, TimeUnit.SECONDS));
        assertEquals(0, gotMessage.getCount());
        gotMessage.reset();
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
        // Reset the registered latch, so we can use it again every time we start a new job.
        TestTask.allTasksRegistered.reset();
    }
}
