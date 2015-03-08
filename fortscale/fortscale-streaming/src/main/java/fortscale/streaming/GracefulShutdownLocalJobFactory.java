package fortscale.streaming;

import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.samza.Partition;
import org.apache.samza.config.Config;
import org.apache.samza.container.SamzaContainer;
import org.apache.samza.job.StreamJob;
import org.apache.samza.job.local.LocalJobFactory;
import org.apache.samza.job.local.ThreadJob;
import org.apache.samza.system.SystemAdmin;
import org.apache.samza.system.SystemFactory;
import org.apache.samza.system.SystemStreamMetadata;
import org.apache.samza.system.SystemStreamPartition;
import org.apache.samza.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.collection.immutable.Set;
import com.google.common.collect.Sets;

/**
 * Samza job factory that supports graceful shutdown.
 * In addition the local job factory enables the restriction of which partitions to include in each input topic.
 * To control which partitions are handles, and java opts parameter should be passed to the JVM instance named "topicPartitions".
 * The value of the java opts parameter "topicPartitions" should be in the format of <topicName>.<partitionId>,<topicName>.<partitionId>,....
 * Only partitions that actualy exists in the topic will be processed. Several partitions or topics of the same kind can
 * appear in the topicPartitions parameter value.
 *
 * To set the JVM topicPartitions in linux ssh terminal run the following command:
 * export JAVA_OPTS="-DtopicPartitions=...."
 */
public class GracefulShutdownLocalJobFactory extends LocalJobFactory {

	private static final Logger logger = LoggerFactory.getLogger(GracefulShutdownLocalJobFactory.class);
	
	private List<ThreadJob> jobs = new LinkedList<ThreadJob>();
	
	public GracefulShutdownLocalJobFactory() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				logger.info("Recieved JVM shutdown signal, shutting down all streaming jobs");
				
				// go over jobs and kill them all
				for (ThreadJob job : jobs) { 
					job.kill();
					job.waitForFinish(10000);
				}
			}
		});
	}
	
	@Override
	public StreamJob getJob(Config config) {
		// get the task name from configuration file
		String taskName = config.get("job.name");

		// create SSP from task.inputs configuration property, if we received an
		// environment variable to determine what partitions are to be processed by each topic.
		// if the environment variable is missing, revert to process all partitions in the input topics
		// as the default action
		Set<SystemStreamPartition> partitions = getPartitions(config);

		// create the thread job and add it to the list of jobs to shutdown on jvm shutdown hook notification
		ThreadJob job = new ThreadJob(SamzaContainer.apply(taskName, partitions, config));
		jobs.add(job);

		return job;
	}

	/**
	 * Get a set of input partitions definitions to be handled by the local task.
	 * If an environment variable is set with a restrictions as to which partitions to process for
	 * each topic only those partitions will be handled. Topics that does not appear in the environment
	 * variable are not affected by this and all partitions for them will be processed.
	 */
	private Set<SystemStreamPartition> getPartitions(Config config) {
		// get the environment variable for input topic restrictions
		Map<String, List<Integer>> partitionsToInclude = getPartitionsToRestrict();

		// load all partitions of the input topics in case the environment variable is missing
		if (partitionsToInclude.isEmpty())
			return Util.getInputStreamPartitions(config);



		scala.collection.mutable.Set<SystemStreamPartition> partitions = new scala.collection.mutable.HashSet<>();

		// get all topics from the task configuration file and for each one create a SSP
		// only for the restricted partitions in the topic
		List<String> configTopics = config.getList("task.inputs");
		Map<String, SystemAdmin> systemAdmins = new HashMap<>();

		for (String configTopic : configTopics) {
			String[] configParts = configTopic.split("\\.");
			String system = configParts[0];
			String topic = configParts[1];

			// ensure we have a system factory for the system topic
			if (!systemAdmins.containsKey(system)) {
				// get the system factory name from configuration
				try {
					String className = config.get(String.format("systems.%s.samza.factory", system));
					SystemFactory factory = (SystemFactory) Class.forName(className).newInstance();
					SystemAdmin admin = factory.getAdmin(system, config);
					systemAdmins.put(system, admin);
				} catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
					throw new IllegalStateException("cannot create instance of system factory for system " + system);
				}
			}
			SystemAdmin admin = systemAdmins.get(system);


			// get all topic partitions and create a SSP for each one
			SystemStreamMetadata metadata = admin.getSystemStreamMetadata(Sets.newHashSet(topic)).get(topic);
			for (Partition partition : metadata.getSystemStreamPartitionMetadata().keySet()) {
				// check if we have the topic and partition in the restriction map
				if (!partitionsToInclude.containsKey(topic) || partitionsToInclude.get(topic).contains(partition.getPartitionId()))
					partitions.add(new SystemStreamPartition(system, topic, partition));
			}
		}

		return partitions.toSet();
	}

	private Map<String, List<Integer>> getPartitionsToRestrict() {
		String envTopicPartitions = System.getProperty("topicPartitions");
		if (StringUtils.isEmpty(envTopicPartitions))
			return new HashMap<>();

		// get the list of partitions topic from the environment variable, assuming the environment
		// variable name is in the following format: <topic>.<partition>,<topic>.<partitions>
		// build from the environment variable a map between topic names and list of partitions to include
		Map<String,List<Integer>> partitionsToInclude = new HashMap<>();
		for (String topicEntry : envTopicPartitions.split(",")) {
			String[] topicEntryArr = topicEntry.split("\\.");
			String topicName = topicEntryArr[0];
			int partition = Integer.parseInt(topicEntryArr[1]);

			if (!partitionsToInclude.containsKey(topicName))
				partitionsToInclude.put(topicName, new LinkedList<Integer>());
			partitionsToInclude.get(topicName).add(partition);
		}
		return partitionsToInclude;
	}

}
