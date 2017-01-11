package fortscale.web;


import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;

public interface ActivityMonitoringExecutorService<T> extends ExecutorService {

    boolean isHasActiveTasks();

    Set<T> getActiveTasks();

    boolean markTaskActive(T task);

    boolean markTaskInactive(T task);

    void executeTasks(List<T> followingTasks);

    boolean isAdExecutionInProgress();

    boolean tryExecute();

    void markEndExecution();
}
