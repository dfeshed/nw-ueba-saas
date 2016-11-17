package fortscale.domain.ad.dao;

import fortscale.domain.ad.AdGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

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


	public List<AdGroup> getActiveDirectoryGroups(int maxNumberOfReturnElements) {
		Query query = new Query(where(AdGroup.memberField).exists(true));
		query.fields().include(AdGroup.memberField);
		query.fields().include(AdGroup.dnField);
		query.limit(maxNumberOfReturnElements);

		return mongoTemplate.find(query, AdGroup.class);
	}

	@Override
	public List<AdGroup> getActiveDirectoryGroupsNameStartsWith(String startsWith) {
		Query query = new Query(where(AdGroup.memberField).exists(true));
		query.fields().include(AdGroup.memberField);
		query.fields().include(AdGroup.dnField);

		String startsWithRegex = "^" + startsWith;
		query.addCriteria(new Criteria(AdGroup.nameField).regex(startsWithRegex, "i"));

		return mongoTemplate.find(query, AdGroup.class);
	}

}
