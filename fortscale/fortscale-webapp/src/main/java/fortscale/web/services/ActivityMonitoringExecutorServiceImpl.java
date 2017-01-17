package fortscale.web.services;

import fortscale.utils.logging.Logger;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ActivityMonitoringExecutorServiceImpl<T extends Runnable> implements ActivityMonitoringExecutorService<T> {

    private static final Logger logger = Logger.getLogger(ActivityMonitoringExecutorServiceImpl.class);

    private final AtomicBoolean inProgress = new AtomicBoolean(false);

    private final ExecutorService executorService;
    private final Set<T> activeTasks;

    public ActivityMonitoringExecutorServiceImpl(ExecutorService executorService, int activeTasksSetSize) {
        this.executorService = executorService;
        this.activeTasks = ConcurrentHashMap.newKeySet(activeTasksSetSize);
    }

    @Override
    public void shutdown() {
        executorService.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        synchronized (this) {
            activeTasks.clear();
            return executorService.shutdownNow();
        }
    }

    @Override
    public boolean isShutdown() {
        return executorService.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        throw new UnsupportedOperationException("not yet supported");
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return executorService.awaitTermination(timeout, unit);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        throw new UnsupportedOperationException("not yet supported");
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        throw new UnsupportedOperationException("not yet supported");
    }

    @Override
    public Future<?> submit(Runnable task) {
        throw new UnsupportedOperationException("not yet supported");

    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        throw new UnsupportedOperationException("not yet supported");

    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        throw new UnsupportedOperationException("not yet supported");

    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        throw new UnsupportedOperationException("not yet supported");
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return executorService.invokeAny(tasks, timeout, unit);
    }

    @Override
    public void execute(Runnable command) {
        executorService.execute(command);
    }

    @Override
    public boolean isHasActiveTasks() {
        return !activeTasks.isEmpty();
    }


    /**
     * returns an unmodifiable COPY of the active tasks set
     * @return an unmodifiable COPY of the active tasks set
     */
    @Override
    public Set<T> getActiveTasks() {
        return Collections.unmodifiableSet(activeTasks); // defensive copy
    }

    @Override
    public synchronized boolean markTaskActive(T task) {
        return activeTasks.add(task);
    }

    @Override
    public synchronized boolean markTaskInactive(T task) {
        return activeTasks.remove(task);
    }

    public void executeTasks(List<T> tasksToExecute) {
        logger.trace("Executing tasks {}", tasksToExecute);
        for (T task : tasksToExecute) {
            executeTask(task);
        }
    }

    @Override
    public boolean isExecutionInProgress() {
        return inProgress.get();
    }

    @Override
    public boolean tryExecute() {
        return !isHasActiveTasks() && inProgress.compareAndSet(false, true);
    }

    @Override
    public void markEndExecution() {
        inProgress.set(false);
    }

    private void executeTask(T taskToExecute) {
        executorService.execute(taskToExecute);
    }

}
