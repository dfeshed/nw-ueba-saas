package fortscale.services.impl;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static org.springframework.data.mongodb.core.query.Update.update;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Service;

import fortscale.domain.ad.AdComputer;
import fortscale.domain.ad.AdGroup;
import fortscale.domain.ad.AdOU;
import fortscale.domain.ad.AdUser;
import fortscale.domain.ad.dao.AdUserRepository;
import fortscale.domain.fe.AdUserFeaturesExtraction;
import fortscale.services.AdService;




@Service("adService")
public class AdServiceImpl implements AdService {
	
	@Autowired
	private MongoOperations mongoTemplate;
	
	@Autowired
	private AdUserRepository adUserRepository;
	
	@Override
	public void addLastModifiedFieldToAllCollections() {
		insertLastModified(AdUser.lastModifiedField, AdUser.class);
		
		insertLastModified(AdGroup.lastModifiedField, AdGroup.class);

		insertLastModified(AdComputer.lastModifiedField, AdComputer.class);

		insertLastModified(AdOU.lastModifiedField, AdOU.class);

		insertLastModified(AdUserFeaturesExtraction.lastModifiedField, AdUserFeaturesExtraction.class);
	}
	
	private void insertLastModified(String fieldName, Class<?> entityClass){
		Date date = new Date();
		mongoTemplate.updateMulti(query(where(fieldName).exists(false)), update(fieldName, date), entityClass);
	}

	@Override
	public void removeThumbnails() {
		Long timestampepoch = adUserRepository.getLatestTimeStampepoch();
		if(timestampepoch != null){
			mongoTemplate.updateMulti(query(where(AdUser.timestampepochField).ne(timestampepoch).and(AdUser.thumbnailPhotoField).exists(true)), update(AdUser.thumbnailPhotoField, null), AdUser.class);
		}
	}

}
