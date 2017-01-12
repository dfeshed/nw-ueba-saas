package fortscale.domain.ad.dao;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.ArrayList;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;


import fortscale.domain.ad.AdUser;
import fortscale.domain.core.dao.MongoDbRepositoryUtil;



class AdUserRepositoryImpl extends AdObjectRepository implements AdUserRepositoryCustom{
	private static final int inOperatorSizeLimit = 2000; 
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Autowired
	private MongoDbRepositoryUtil mongoDbRepositoryUtil;

	public String getAdUsersLastSnapshotRuntime(){
		return  mongoDbRepositoryUtil.getLatestTimeStampString(AdUser.runTimeField, AdUser.COLLECTION_NAME);
	}
	public Page<AdUser> findAdUsersBelongtoOUInSnapshot(String ou, Pageable pageable, String runtime){
		Query query = new Query(where(AdUser.dnField).regex(".*"+ou).andOperator(where(AdUser.runTimeField).is(runtime)));
		return mongoDbRepositoryUtil.getPage(query, pageable, AdUser.class, false);
	}
	
	public List<AdUser> findByDnUsersIn(List<String> usersDn) {
		String latestRuntime = mongoDbRepositoryUtil.getLatestTimeStampString(AdUser.runTimeField, AdUser.COLLECTION_NAME);
		int chunksNumber = (int) Math.ceil((float) usersDn.size() / inOperatorSizeLimit);
		List<AdUser> users = new ArrayList<AdUser>();
		for (int i = 0; i < chunksNumber - 1; i++) {
			users.addAll(findByDnAdUsersInChunk((usersDn.subList(inOperatorSizeLimit * i, inOperatorSizeLimit * (i + 1))), latestRuntime));

		}
		users.addAll(findByDnAdUsersInChunk(usersDn.subList(inOperatorSizeLimit * (chunksNumber - 1), usersDn.size()), latestRuntime));
		return users;
	}
	
	private List<AdUser> findByDnAdUsersInChunk(List<String> usersDn, String time){
		Query query = new Query(where(AdUser.dnField).in(usersDn).andOperator(where(AdUser.runTimeField).is(time)));
		return mongoTemplate.find(query, AdUser.class, AdUser.COLLECTION_NAME);
	}

	@Override
	public String getCollectionName() {
		return AdUser.COLLECTION_NAME;
	}
}
