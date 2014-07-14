package fortscale.services.computer.classifier;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import fortscale.domain.ad.UserMachine;
import fortscale.domain.ad.dao.UserMachineDAO;
import fortscale.domain.core.Computer;
import fortscale.domain.core.ComputerUsageClassifier;
import fortscale.domain.core.ComputerUsageType;

public class LoginClassifierTest {

	@Mock
	private UserMachineDAO dao;
	@InjectMocks
	private LoginClassifier classifier;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		classifier.setDesktopStaleThreshold(3);
		classifier.setUnknownStaleThreshold(1);
		classifier.setLoginPeriod(30);
	}
	

	@Test
	public void classifier_should_do_nothing_when_there_is_no_hostname() {
		Computer computer = new Computer();
		classifier.classify(computer);
		
		// verify no interactions are made with the dao
		verify(dao, times(0)).findByHostname(anyString(), anyInt());
		verify(dao, times(0)).findByUsername(anyString());
		
		assertTrue(computer.getUsageClassifiers().size()==0);
	}
	
	@Test
	public void classifier_should_do_nothing_when_other_classification_exists() {
		List<UserMachine> logins = new LinkedList<UserMachine>();
		logins.add(new UserMachine("me", "my-pc", 20, 0L));
		when(dao.findByHostname("my-pc", 30)).thenReturn(logins);
		when(dao.findByUsername("me")).thenReturn(logins);
		
		Computer computer = new Computer();
		computer.setName("my-pc");
		computer.putUsageClassifier(new ComputerUsageClassifier("TestClassifier", ComputerUsageType.Desktop, new Date()));
		
		classifier.classify(computer);
		
		// verify no interactions are made with the dao
		verify(dao, times(0)).findByHostname(anyString(), anyInt());
		verify(dao, times(0)).findByUsername(anyString());
		assertTrue(computer.getUsageClassifiers().size()==1);
	}
	
	@Test
	public void classifier_should_compute_when_other_classification_set_unknown() {
		List<UserMachine> logins = new LinkedList<UserMachine>();
		logins.add(new UserMachine("me", "my-pc", 20, 0L));
		when(dao.findByHostname("my-pc", 30)).thenReturn(logins);
		when(dao.findByUsername("me")).thenReturn(logins);
		
		Computer computer = new Computer();
		computer.setName("my-pc");
		computer.putUsageClassifier(new ComputerUsageClassifier("TestClassifier", ComputerUsageType.Unknown, new Date()));
		
		classifier.classify(computer);
		
		ComputerUsageClassifier classification = computer.getUsageClassifier(LoginClassifier.CLASSIFIER_NAME);
		assertNotNull(classification);
		assertTrue(computer.getUsageClassifiers().size()==2);
	}
	
	@Test
	public void classifier_should_not_compute_when_previous_dekstop_classification_is_not_stale() {
		Computer computer = new Computer();
		Date whenComputed = new Date();
		computer.putUsageClassifier(new ComputerUsageClassifier(LoginClassifier.CLASSIFIER_NAME, ComputerUsageType.Desktop, whenComputed));
		
		classifier.classify(computer);
		
		// verify no interactions are made with the dao
		verify(dao, times(0)).findByHostname(anyString(), anyInt());
		verify(dao, times(0)).findByUsername(anyString());
		
		assertTrue(computer.getUsageClassifier(LoginClassifier.CLASSIFIER_NAME).getWhenComputed().equals(whenComputed));
	}
	
	@Test
	public void classifier_should_not_compute_when_previous_unknown_classification_is_not_stale() {
		Computer computer = new Computer();
		Date whenComputed = new Date();
		computer.putUsageClassifier(new ComputerUsageClassifier(LoginClassifier.CLASSIFIER_NAME, ComputerUsageType.Unknown, whenComputed));
		
		classifier.classify(computer);
		
		// verify no interactions are made with the dao
		verify(dao, times(0)).findByHostname(anyString(), anyInt());
		verify(dao, times(0)).findByUsername(anyString());
		
		assertTrue(computer.getUsageClassifier(LoginClassifier.CLASSIFIER_NAME).getWhenComputed().equals(whenComputed));
	}
	
	@Test
	public void classifier_should_classifiy_as_unknown_if_a_user_did_not_login_more_than_10_times_per_30_days() {
		List<UserMachine> logins = new LinkedList<UserMachine>();
		logins.add(new UserMachine("me", "my-pc", 4, 0L));
		when(dao.findByHostname("my-pc", 30)).thenReturn(logins);
		when(dao.findByUsername("me")).thenReturn(logins);
		
		Computer computer = new Computer();
		computer.setName("my-pc");
		
		classifier.classify(computer);
		
		ComputerUsageClassifier classification = computer.getUsageClassifier(LoginClassifier.CLASSIFIER_NAME);
		assertNotNull(classification);
		assertEquals(ComputerUsageType.Unknown, classification.getUsageType());
	}
	
	@Test
	public void classifier_should_classify_as_desktop_if_a_user_login_more_than_10_times_per_30_days() {
		List<UserMachine> logins = new LinkedList<UserMachine>();
		logins.add(new UserMachine("me", "my-pc", 20, 0L));
		when(dao.findByHostname("my-pc", 30)).thenReturn(logins);
		when(dao.findByUsername("me")).thenReturn(logins);
		
		Computer computer = new Computer();
		computer.setName("my-pc");
		
		classifier.classify(computer);
		
		ComputerUsageClassifier classification = computer.getUsageClassifier(LoginClassifier.CLASSIFIER_NAME);
		assertNotNull(classification);
		assertEquals(ComputerUsageType.Desktop, classification.getUsageType());
	}
	
	@Test
	public void classifier_should_classify_as_unknow_if_there_are_more_than_one_users_that_login() {
		List<UserMachine> logins = new LinkedList<UserMachine>();
		logins.add(new UserMachine("me", "my-pc", 15, 0L));
		logins.add(new UserMachine("him", "my-pc", 7, 0L));
		when(dao.findByHostname("my-pc", 30)).thenReturn(logins);
		when(dao.findByUsername("me")).thenReturn(logins);
		
		Computer computer = new Computer();
		computer.setName("my-pc");
		
		classifier.classify(computer);
		
		ComputerUsageClassifier classification = computer.getUsageClassifier(LoginClassifier.CLASSIFIER_NAME);
		assertNotNull(classification);
		assertEquals(ComputerUsageType.Unknown, classification.getUsageType());
	}
}
