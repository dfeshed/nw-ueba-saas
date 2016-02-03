package fortscale.streaming.common;

import org.apache.samza.config.Config;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Resource of a samza task are per instance but Samza Container may hold more than one instance.
 * This means that on the same process few task instances run and there a singelton service should know which resources it should
 * use. This comes to enable us using a singelton services and not per instance.
 *
 *
 * Containers and resource allocation

 Although the number of task instances is fixed — determined by the number of input partitions — you can configure how many containers you want to use for your job. If you are using YARN, the number of containers determines what CPU and memory resources are allocated to your job.

 If the data volume on your input streams is small, it might be sufficient to use just one SamzaContainer. In that case, Samza still creates one task instance per input partition, but all those tasks run within the same container. At the other extreme, you can create as many containers as you have partitions, and Samza will assign one task instance to each container.

 Each SamzaContainer is designed to use one CPU core, so it uses a single-threaded event loop for execution. It’s not advisable to create your own threads within a SamzaContainer. If you need more parallelism, please configure your job to use more containers.

 Any state in your job belongs to a task instance, not to a container. This is a key design decision for Samza’s scalability: as your job’s resource requirements grow and shrink, you can simply increase or decrease the number of containers, but the number of task instances remains unchanged. As you scale up or down, the same state remains attached to each task instance. Task instances may be moved from one container to another, and any persistent state managed by Samza will be moved with it. This allows the job’s processing semantics to remain unchanged, even as you change the job’s parallelism.
 */
@Component
public class SamzaContainerService {

    private Config config;
    private TaskContext context;

    private MessageCollector collector;
    private TaskCoordinator coordinator;

    List<SamzaContainerInitializedListener> samzaContainerInitializedListeners = new ArrayList<>();

    public void registerSamzaContainerInitializedListener(SamzaContainerInitializedListener samzaContainerInitializedListener){
        samzaContainerInitializedListeners.add(samzaContainerInitializedListener);
    }

    public void init(Config config, TaskContext context){
        this.config = config;
        this.context = context;
        for(SamzaContainerInitializedListener samzaContainerInitializedListener: samzaContainerInitializedListeners){
            samzaContainerInitializedListener.afterSamzaContainerInitialized();
        }
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public TaskContext getContext() {
        return context;
    }

    public Object getStore(String storeName){
        return context.getStore(storeName);
    }

    public void setContext(TaskContext context) {
        this.context = context;
    }


    public MessageCollector getCollector() {
        return collector;
    }

    public void setCollector(MessageCollector collector) {
        this.collector = collector;
    }

    public TaskCoordinator getCoordinator() {
        return coordinator;
    }

    public void setCoordinator(TaskCoordinator coordinator) {
        this.coordinator = coordinator;
    }
}
