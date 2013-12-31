package fortscale.domain.ad;

import java.util.Iterator;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fortscale.domain.AbstractTest;
import fortscale.domain.ad.dao.AdUserRepository;

@Ignore
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
		Iterator<AdUser> iterator = repository.findAll().iterator();
		AdUser user = iterator.next();
		//TODO: remove the following while statement after omri fixes the problem: users like systemmailbox{e0dc1c29-89c3-4034-b678-e6c29d823ed9}@fortscale.dom
		while(user.getDistinguishedName().contains("{")) {
			user = iterator.next();
		}
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
