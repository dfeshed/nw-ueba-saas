package fortscale.services.fe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import fortscale.domain.core.User;
import fortscale.domain.core.dao.UserRepository;
import fortscale.domain.fe.dao.AccessDAO;
import fortscale.domain.fe.dao.EventResultRepository;
import fortscale.domain.fe.dao.Threshold;
import fortscale.services.analyst.ConfigurationService;
import fortscale.services.fe.impl.ClassifierServiceImpl;
import fortscale.services.fe.impl.ScoreDistribution;
import fortscale.services.impl.SeverityElement;

public class ClassifierServiceTest{

	@Mock
	private UserRepository userRepository;
	
	@Mock
	private EventResultRepository eventResultRepository;
	
	@Mock
	private AccessDAO loginDAO;
	
	@Mock
	private AccessDAO sshDAO;
	
	@Mock
	private AccessDAO vpnDAO;
		
	@Mock
	private ConfigurationService configurationService;
		
	@InjectMocks
	private ClassifierServiceImpl classifierService;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}
	

	@Test
	public void testGetScoreDistributionForAuth() {
		testGetScoreDistribution(Classifier.auth.getId());
	}
	
	
	private void testGetScoreDistribution(String classifierId){
		int total = 17;
		Mockito.when(userRepository.countNumOfUsersAboveThreshold(classifierId, new Threshold("All", 0))).thenReturn(total);
		
		List<Threshold> thresholds = getThresholds();
		Map<String,IScoreDistribution> expectedScoreDistributions = new HashMap<>();
		
		int thresholdIndex = 0;
		int startCount = 17;
		int endCount = 9;
		addScoreDistribution(expectedScoreDistributions, classifierId, total, thresholds.get(thresholdIndex), thresholds.get(thresholdIndex+1), startCount, endCount);
		
		thresholdIndex++;
		startCount = endCount;
		endCount = 3;
		addScoreDistribution(expectedScoreDistributions, classifierId, total, thresholds.get(thresholdIndex), thresholds.get(thresholdIndex+1), startCount, endCount);
		
		thresholdIndex++;
		startCount = endCount;
		endCount = 1;
		addScoreDistribution(expectedScoreDistributions, classifierId, total, thresholds.get(thresholdIndex), thresholds.get(thresholdIndex+1), startCount, endCount);
		
		thresholdIndex++;
		startCount = endCount;
		endCount = 0;
		addScoreDistribution(expectedScoreDistributions, classifierId, total, thresholds.get(thresholdIndex), new Threshold("Last", 100), startCount, endCount);
		
		List<SeverityElement> severityElements = getSeverityElements(thresholds);
		Mockito.when(configurationService.getSeverityElements()).thenReturn(severityElements);
		
		List<IScoreDistribution> scoreDistributions = classifierService.getScoreDistribution(classifierId);
		
		Assert.assertEquals(expectedScoreDistributions.size(), scoreDistributions.size());
		
		for(IScoreDistribution scoreDistribution: scoreDistributions){
			IScoreDistribution expectedScoreDistribution = expectedScoreDistributions.get(scoreDistribution.getName());
			Assert.assertNotNull(expectedScoreDistribution);
			Assert.assertEquals(expectedScoreDistribution, scoreDistribution);
		}
		
	}
	
	private List<Threshold> getThresholds(){
		List<Threshold> ret = new ArrayList<>();
		ret.add(new Threshold("Low", 0));
		ret.add(new Threshold("Medium", 50));
		ret.add(new Threshold("High", 80));
		ret.add(new Threshold("Critical", 90));
		return ret;
	}
	
	private List<SeverityElement> getSeverityElements(List<Threshold> thresholds){
		List<SeverityElement> ret = new ArrayList<>();
		for(Threshold threshold: thresholds){
			ret.add(new SeverityElement(threshold.getName(), threshold.getValue()));
		}
		Collections.reverse(ret);
		return ret;
	}
	
	private void addScoreDistribution(Map<String,IScoreDistribution> expectedScoreDistributions, String classifierId, int total, Threshold startThreshold, Threshold endThreshold, int startCount, int endCount){
		Mockito.when(userRepository.countNumOfUsersAboveThreshold(classifierId, startThreshold)).thenReturn(startCount);
		double percent =  Math.round(100 * (startCount - endCount) / ((double) total) );
		expectedScoreDistributions.put(startThreshold.getName(), new ScoreDistribution(startThreshold.getName(), (startCount - endCount), (int)percent, startThreshold.getValue(), endThreshold.getValue()));
	}
	
	
	
	public static class TestUser extends User{
		private static final long serialVersionUID = 1L;

		public TestUser(String adDn) {
			setAdDn(adDn);
		}
		
		public void setId(String userIdString) {
			super.setId(userIdString);
		}
	}
}
