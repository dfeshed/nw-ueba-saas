package fortscale.domain.fe;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;

import fortscale.domain.AbstractTest;
import fortscale.domain.fe.dao.AdUsersFeaturesExtractionRepository;
import fortscale.domain.fe.dao.Threshold;



@Ignore
public class AdUsersFeaturesExtractionRepositoryTest extends AbstractTest{

	@Autowired
	private AdUsersFeaturesExtractionRepository repository;
	
	@Test
	public void testCount(){
		repository.count();
	}
	
	@Test
	public void testFindAll(){
		repository.findAll();
	}
	
	@Test
	public void testFindOne(){
		AdUserFeaturesExtraction adUserFeaturesExtraction = repository.findAll().iterator().next();
		AdUserFeaturesExtraction adUserFeaturesExtraction2 = repository.findOne(adUserFeaturesExtraction.getId());
		Assert.assertEquals(adUserFeaturesExtraction.getId(), adUserFeaturesExtraction2.getId());
	}
	
//	@Test
//	public void testFindClassifierIdAndByUserIdAndTimestamp() {
//		AdUserFeaturesExtraction adUserFeaturesExtraction = repository.findAll().iterator().next();
//		AdUserFeaturesExtraction adUserFeaturesExtraction2 = repository.findClassifierIdAndByUserIdAndTimestamp(adUserFeaturesExtraction.getClassifierId(), adUserFeaturesExtraction.getUserId(), adUserFeaturesExtraction.getTimestamp());
//		Assert.assertEquals(adUserFeaturesExtraction.getId(), adUserFeaturesExtraction2.getId());
//	}
	
	@Test
	public void testCalculateAvgScore() {
		AdUserFeaturesExtraction adUserFeaturesExtraction = repository.findAll().iterator().next();
		repository.calculateAvgScore(adUserFeaturesExtraction.getClassifierId(), adUserFeaturesExtraction.getTimestamp());
	}
	
	@Test
	public void testCalculateNumOfUsersWithScoresGTThresholdForLastRun() {
		AdUserFeaturesExtraction adUserFeaturesExtraction = repository.findAll().iterator().next();
		List<Threshold> thresholds = new ArrayList<>();
		thresholds.add(new Threshold("All", 0));
		thresholds.add(new Threshold("Low", 10));
		thresholds.add(new Threshold("Medium", 60));
		thresholds.add(new Threshold("Critical", 85));
		repository.calculateNumOfUsersWithScoresGTThresholdForLastRun(adUserFeaturesExtraction.getClassifierId(), thresholds);
	}
	
	@Test
	public void testFindByClassifierId() {
		AdUserFeaturesExtraction adUserFeaturesExtraction = repository.findAll().iterator().next();
		Pageable pageable = new PageRequest(0, 1, Direction.DESC, AdUserFeaturesExtraction.timestampField);
		List<AdUserFeaturesExtraction> adUserFeaturesExtractions = repository.findByClassifierId(adUserFeaturesExtraction.getClassifierId(), pageable);
		Assert.assertTrue(adUserFeaturesExtractions.size() > 0);
	}
	
	@Test
	public void testFindByClassifierIdAndTimestampAndScoreBetween() {
		AdUserFeaturesExtraction adUserFeaturesExtraction = repository.findAll(new PageRequest(0, 1)).iterator().next();
		Pageable pageable = new PageRequest(0, 10, Direction.DESC, AdUserFeaturesExtraction.scoreField);
		int score = adUserFeaturesExtraction.getScore().intValue();
		List<AdUserFeaturesExtraction> adUserFeaturesExtractions = repository.findByClassifierIdAndTimestampAndScoreBetween(adUserFeaturesExtraction.getClassifierId(), adUserFeaturesExtraction.getTimestamp(), score, score + 1, pageable);
		Assert.assertTrue(adUserFeaturesExtractions.size() > 0);
	}
	
	
}
