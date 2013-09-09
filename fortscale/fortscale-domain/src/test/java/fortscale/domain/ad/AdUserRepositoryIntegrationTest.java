package fortscale.domain.ad;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fortscale.domain.ad.dao.AdUserRepository;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.MongoDbRepositoryUtil;
import fortscale.domain.core.dao.UserRepository;
import fortscale.domain.fe.ADFeature;
import fortscale.domain.fe.AdUserFeaturesExtraction;
import fortscale.domain.fe.IFeature;
import fortscale.domain.fe.dao.AdUsersFeaturesExtractionRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:META-INF/spring/fortscale-domain-context-test.xml" })
public class AdUserRepositoryIntegrationTest{

	@Autowired
	AdUserRepository repository;
	
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private AdUsersFeaturesExtractionRepository adUsersFeaturesExtractionRepository;
	
	@Autowired
	private MongoDbRepositoryUtil mongoDbRepositoryUtil;

	@Test
	public void findAllTest(){
//		Pageable pageable = new PageRequest(0, 10, Direction.DESC, "dn");
//		AdUser adUser = repository.findByEmailAddress("ramir@fortscale.dom");
//		System.out.println(adUser.getDistinguishedName() + ", " + adUser.getLastname()  + ", " + adUser.getEmailAddress());
//		for(AdUser adUser1: repository.findAll(pageable)){
//			System.out.println(adUser1.getDistinguishedName() + ", " + adUser1.getLastname() + ", " + adUser1.getEmailAddress());
//		}
//		System.out.println();
	}
	
//	@Test
	public void getAdUsersAttrValsTest(){
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
		
		Map<String, Map<String, String>> ret = new HashMap<String, Map<String,String>>();
		
		List<AdUser> adUsersAttrValsList = repository.findAdUsersAttrVals();
		for(AdUser adUser: adUsersAttrValsList){
			User user = userRepository.findByAdDn(adUser.getDistinguishedName());
			if(user == null){
				//TODO: WARNING
				continue;
			}
			ret.put(user.getId().toString(), adUser.getAttrVals());
			AdUserFeaturesExtraction adUsersFeaturesExtraction = new AdUserFeaturesExtraction("ad",user.getId().toString(),user.getId().toString());
			List<IFeature> features = new ArrayList<IFeature>();
			for(Entry<String, String> ent: adUser.getAttrVals().entrySet()){
				if(ent.getKey().equalsIgnoreCase("_id")){
					continue;
				}
				IFeature adFeature = new ADFeature(ent.getKey(), ent.getKey().toString(), 1.0, 2.0);
				features.add(adFeature);
			}
			adUsersFeaturesExtraction.setAttrVals(features);
			adUsersFeaturesExtractionRepository.saveMap(adUsersFeaturesExtraction);
//			mongoDbRepositoryUtil.saveMap(AdUserFeaturesExtraction.collectionName, adUser.getAttrVals());
			System.out.println(user.getId().toString());
			System.out.println();
			System.out.println(adUser);
		}
		System.out.println();
	}
}
