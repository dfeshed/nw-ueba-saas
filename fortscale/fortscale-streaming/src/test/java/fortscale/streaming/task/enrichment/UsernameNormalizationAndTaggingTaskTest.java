package fortscale.streaming.task.enrichment;

import fortscale.streaming.service.UserTagsService;
import fortscale.streaming.service.usernameNormalization.UsernameNormalizationService;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.system.SystemStreamPartition;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskCoordinator;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class UsernameNormalizationAndTaggingTaskTest {


	public static final String MESSAGE_1 = "{ \"name\": \"user1\" }";
	public static final String MESSAGE_2 = "{ \"name\": \"user2\",  \"normalized_name\": \"User 2\" }";
	public static final String MESSAGE_3 = "{ \"name\": \"user3\" }";
	public static final String MESSAGE_4 = "{ \"name\": \"user4\" }";

	@Test
	public void testWrappedProcess() throws Exception {

		// Init the task to test
		UsernameNormalizationAndTaggingTask task = new UsernameNormalizationAndTaggingTask();

		// fields
		task.normalizedUsernameField = "normalized_name";
		task.usernameField = "name";

		// Mocks
		SystemStreamPartition systemStreamPartition = Mockito.mock(SystemStreamPartition.class);
		SystemStream systemStream = Mockito.mock(SystemStream.class);
		Mockito.when(systemStreamPartition.getSystemStream()).thenReturn(systemStream);

		// configuration
		task.inputTopicToConfiguration = new HashMap<>();
		UsernameNormalizationService usernameNormalizationService = Mockito.mock(SystemStreamPartition.class);
		task.inputTopicToConfiguration.put("input1" , new ImmutablePair<>("output1",usernameNormalizationService));

		// tagging
		task.tagService = Mockito.mock(UserTagsService.class);


		// User2 with normalized username

		// prepare envelope
		IncomingMessageEnvelope envelope = getIncomingMessageEnvelope(systemStreamPartition, systemStream, MESSAGE_2, "input1");
		// run the process on the envelope
		task.wrappedProcess(envelope ,Mockito.mock(MessageCollector.class), Mockito.mock(TaskCoordinator.class));
		// validate no normalization for username (we already have it
		Mockito.verify(usernameNormalizationService, never()).normalizeUsername(anyString());
		// validate tagging
		Mockito.verify(task.tagService).addTagsToEvent("User 2", MESSAGE_2);


		// User 1 without normalized name, success in normalization

		Mockito.when(usernameNormalizationService.normalizeUsername("user1")).thenReturn("User 1");
		Mockito.when(usernameNormalizationService.shouldDropRecord("user1", "User 1")).thenReturn(false);
		// prepare envelope
		envelope = getIncomingMessageEnvelope(systemStreamPartition, systemStream, MESSAGE_1, "input1");
		// run the process on the envelope
		task.wrappedProcess(envelope ,Mockito.mock(MessageCollector.class), Mockito.mock(TaskCoordinator.class));
		// validate no normalization for username (we already have it
		Mockito.verify(usernameNormalizationService).normalizeUsername("user1");
		Mockito.verify(usernameNormalizationService, never()).getUsernameAsNormalizedUsername(anyString(),anyString());
		// validate tagging
		Mockito.verify(task.tagService).addTagsToEvent("User 1", MESSAGE_1);


		// User 3 without normalized name, failure in normalization and drop of record

		Mockito.when(usernameNormalizationService.normalizeUsername("user3")).thenReturn(null);
		Mockito.when(usernameNormalizationService.shouldDropRecord("user3", null)).thenReturn(true);
		// prepare envelope
		envelope = getIncomingMessageEnvelope(systemStreamPartition, systemStream, MESSAGE_3, "input1");
		// run the process on the envelope
		task.wrappedProcess(envelope ,Mockito.mock(MessageCollector.class), Mockito.mock(TaskCoordinator.class));
		// validate no normalization for username (we already have it
		Mockito.verify(usernameNormalizationService).normalizeUsername("user3");
		Mockito.verify(usernameNormalizationService, never()).getUsernameAsNormalizedUsername(anyString(),anyString());
		// validate tagging
		Mockito.verify(task.tagService, never()).addTagsToEvent(anyString(), MESSAGE_3);


		// User 4 without normalized name, failure in normalization and no drop of record

		Mockito.when(usernameNormalizationService.normalizeUsername("user4")).thenReturn(null);
		Mockito.when(usernameNormalizationService.shouldDropRecord("user4", null)).thenReturn(false);
		Mockito.when(usernameNormalizationService.getUsernameAsNormalizedUsername("user4", MESSAGE_4)).thenReturn("User 4");
		// prepare envelope
		envelope = getIncomingMessageEnvelope(systemStreamPartition, systemStream, MESSAGE_4, "input1");
		// run the process on the envelope
		task.wrappedProcess(envelope ,Mockito.mock(MessageCollector.class), Mockito.mock(TaskCoordinator.class));
		// validate no normalization for username (we already have it
		Mockito.verify(usernameNormalizationService).normalizeUsername("user4");
		Mockito.verify(usernameNormalizationService).getUsernameAsNormalizedUsername("user4",anyString());
		// validate tagging
		Mockito.verify(task.tagService).addTagsToEvent("User 4", MESSAGE_4);


	}

	private IncomingMessageEnvelope getIncomingMessageEnvelope(SystemStreamPartition systemStreamPartition,
					SystemStream systemStream, String message, String topic) {

		IncomingMessageEnvelope envelope = Mockito.mock(IncomingMessageEnvelope.class);
		Mockito.when(envelope.getMessage()).thenReturn(message);
		Mockito.when(envelope.getSystemStreamPartition()).thenReturn(systemStreamPartition);
		Mockito.when(systemStream.getStream()).thenReturn(topic);

		return envelope;
	}
}
