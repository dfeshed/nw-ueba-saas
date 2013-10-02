package fortscale.domain.ad;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fortscale.domain.AbstractTest;
import fortscale.domain.ad.dao.AdUserRepository;

public class AdUserRepositoryTest extends AbstractTest{

	@Autowired
	AdUserRepository repository;
		
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
		AdUser user = repository.findAll().iterator().next();
		repository.findOne(user.getId());
	}
	
	@Test
	public void testFindByDistinguishedName(){
		AdUser user = repository.findAll().iterator().next();
		repository.findByDistinguishedNameIgnoreCaseContaining(user.getDistinguishedName());
	}
	
	@Test
	public void testFindAdUsersAttrVals(){
		repository.findAdUsersAttrVals();
	}
	
	@Test
	public void testFindByEmailAddress(){
		AdUser user = repository.findAll().iterator().next();
		repository.findByEmailAddress(user.getEmailAddress());
	}
	
	@Test
	public void testGetLatestTimeStamp(){
		repository.getLatestTimeStamp();
	}
	
	@Test
	public void testFindByTimestamp(){
		repository.findByTimestamp(repository.getLatestTimeStamp());
	}
	
	
	

//	@Test
//	public void findAllTest(){
//		Pageable pageable = new PageRequest(0, 10, Direction.DESC, "dn");
//		AdUser adUser = repository.findByEmailAddress("ramir@fortscale.dom");
//		System.out.println(adUser.getDistinguishedName() + ", " + adUser.getLastname()  + ", " + adUser.getEmailAddress());
//		for(AdUser adUser1: repository.findAll(pageable)){
//			System.out.println(adUser1.getDistinguishedName() + ", " + adUser1.getLastname() + ", " + adUser1.getEmailAddress());
//		}
//		System.out.println();
//	}
	
//	@Test
//	public void getAdUsersAttrValsTest(){
//		for(AdUser adUser: repository.findAll()){
//			User user = userRepository.findByAdDn(adUser.getDistinguishedName());
//			if(user == null){
//				user = new User(adUser.getDistinguishedName(), adUser.getDistinguishedName());
//			}
//			user.setFirstname(adUser.getFirstname());
//			user.setLastname(adUser.getLastname());
//			user.setEmailAddress(new EmailAddress(adUser.getEmailAddress()));
//			userRepository.save(user);
//		}
		
//		Map<String, Map<String, String>> ret = new HashMap<String, Map<String,String>>();
//		
//		List<AdUser> adUsersAttrValsList = repository.findAdUsersAttrVals();
//		for(AdUser adUser: adUsersAttrValsList){
//			User user = userRepository.findByAdDn(adUser.getDistinguishedName());
//			if(user == null){
//				//TODO: WARNING
//				continue;
//			}
//			ret.put(user.getId().toString(), adUser.getAttrVals());
//			AdUserFeaturesExtraction adUsersFeaturesExtraction = new AdUserFeaturesExtraction("ad",user.getId().toString(),user.getId().toString());
//			List<IFeature> features = new ArrayList<IFeature>();
//			for(Entry<String, String> ent: adUser.getAttrVals().entrySet()){
//				if(ent.getKey().equalsIgnoreCase("_id")){
//					continue;
//				}
//				IFeature adFeature = new ADFeature(ent.getKey(), ent.getKey().toString(), 1.0, 2.0);
//				features.add(adFeature);
//			}
//			adUsersFeaturesExtraction.setAttrVals(features);
//			adUsersFeaturesExtractionRepository.saveMap(adUsersFeaturesExtraction);
////			mongoDbRepositoryUtil.saveMap(AdUserFeaturesExtraction.collectionName, adUser.getAttrVals());
//			System.out.println(user.getId().toString());
//			System.out.println();
//			System.out.println(adUser);
//		}
//		System.out.println();
//	}
}
