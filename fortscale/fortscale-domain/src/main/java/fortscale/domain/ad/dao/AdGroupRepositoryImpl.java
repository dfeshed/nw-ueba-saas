package fortscale.domain.ad.dao;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import fortscale.domain.ad.AdGroup;


public class AdGroupRepositoryImpl extends AdObjectRepositoryImpl implements AdGroupRepositoryCustom {
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Override
	public AdGroup findByDistinguishedNameInLastSnapshot(String dn) {
		Query query = new Query(where(AdGroup.dnField).is(dn));
		query.with(new Sort(Direction.DESC, AdGroup.lastModifiedField));
		query.limit(1);
		AdGroup latest = mongoTemplate.findOne(query, AdGroup.class);
		return latest!=null ? latest : null;
	}

	

}
