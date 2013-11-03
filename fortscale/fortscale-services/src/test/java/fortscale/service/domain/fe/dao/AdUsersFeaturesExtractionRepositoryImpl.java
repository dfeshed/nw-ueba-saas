package fortscale.service.domain.fe.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import fortscale.domain.fe.AdUserFeaturesExtraction;
import fortscale.domain.fe.dao.AdUsersFeaturesExtractionRepository;
import fortscale.domain.fe.dao.Threshold;




@Component("adUsersFeaturesExtractionRepository")
public class AdUsersFeaturesExtractionRepositoryImpl implements AdUsersFeaturesExtractionRepository{
	
	Map<String, List<AdUserFeaturesExtraction>> classifierMap = new HashMap<String, List<AdUserFeaturesExtraction>>();

	@Override
	public Iterable<AdUserFeaturesExtraction> findAll(Sort sort) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<AdUserFeaturesExtraction> findAll(Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends AdUserFeaturesExtraction> S save(S entity) {
		List<AdUserFeaturesExtraction> adUserFeaturesExtractions = classifierMap.get(entity.getClassifierId());
		if(adUserFeaturesExtractions == null) {
			adUserFeaturesExtractions = new ArrayList<>();
			classifierMap.put(entity.getClassifierId(), adUserFeaturesExtractions);
		}
		adUserFeaturesExtractions.add(entity);
		
		return entity;
	}

	@Override
	public <S extends AdUserFeaturesExtraction> Iterable<S> save(
			Iterable<S> entities) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AdUserFeaturesExtraction findOne(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean exists(String id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterable<AdUserFeaturesExtraction> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<AdUserFeaturesExtraction> findAll(Iterable<String> ids) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long count() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void delete(String id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(AdUserFeaturesExtraction entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(Iterable<? extends AdUserFeaturesExtraction> entities) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void saveMap(AdUserFeaturesExtraction adUsersFeaturesExtraction) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Double calculateAvgScore(String classifierId, Date timestamp) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Threshold> calculateNumOfUsersWithScoresGTThresholdForLastRun(String classifierId, List<Threshold> thresholds) {
		int count = 10;
		for(int i = 1; i < thresholds.size(); i++) {
			thresholds.get(i).setCount(count);
			count = count * 2;
		}
		thresholds.get(0).setCount(count*2);
		return thresholds;
	}

	@Override
	public List<AdUserFeaturesExtraction> findByClassifierId(String classifierId, Pageable pageable) {
		return classifierMap.get(classifierId);
	}

	@Override
	public List<AdUserFeaturesExtraction> findByUserId(String userId,
			Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AdUserFeaturesExtraction> findByUserIdAndClassifierId(
			String userId, String classifierId, Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AdUserFeaturesExtraction findByUserIdAndClassifierIdAndTimestamp(
			String userId, String classifierId, Date timestamp) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AdUserFeaturesExtraction> findByClassifierIdAndTimestampAndScoreBetween(
			String classifierId, Date timestamp, int lowestVal, int upperVal,
			Pageable pageable) {
		return classifierMap.get(classifierId);
	}

	@Override
	public Date getLatestTimeStamp() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AdUserFeaturesExtraction> findByClassifierIdAndTimestamp(
			String classifierId, Date timestamp) {
		// TODO Auto-generated method stub
		return null;
	}	
}
