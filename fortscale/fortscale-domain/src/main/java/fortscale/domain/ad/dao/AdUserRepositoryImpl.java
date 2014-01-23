package fortscale.domain.ad.dao;






class AdUserRepositoryImpl extends AdObjectRepositoryImpl implements AdUserRepositoryCustom{
//	@SuppressWarnings("unchecked")
//	@Override
//	public List<AdUser> findAdUsersAttrVals() {
//		DBCollection collection = getDBCollection();
//		
//		List<AdUser> ret = new ArrayList<AdUser>();
//		Iterator<DBObject> iter = collection.find().iterator();
//		while(iter.hasNext()){
//			DBObject cur = iter.next();
//			AdUser adUser = new AdUser();
//			adUser.setDistinguishedName((String) cur.toMap().get(AdObject.dnField));
//			adUser.setAttrVals(cur.toMap());
//			ret.add(adUser);
//		}
//		
//		return ret;
//	}
	
//	private DBCollection getDBCollection(){
//		DB db = mongoDbFactory.getDb();
//		return db.getCollection("ad_user");
//	}
}
