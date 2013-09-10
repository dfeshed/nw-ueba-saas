package fortscale.services.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fortscale.domain.ad.AdGroup;
import fortscale.domain.ad.AdUser;
import fortscale.domain.ad.AdUserGroup;
import fortscale.domain.ad.UserMachine;
import fortscale.domain.ad.dao.AdGroupRepository;
import fortscale.domain.ad.dao.AdUserRepository;
import fortscale.domain.ad.dao.UserMachineDAO;
import fortscale.domain.core.ClassifierScore;
import fortscale.domain.core.EmailAddress;
import fortscale.domain.core.ScoreInfo;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.UserRepository;
import fortscale.domain.fe.AdUserFeaturesExtraction;
import fortscale.domain.fe.IFeature;
import fortscale.domain.fe.dao.AdUsersFeaturesExtractionRepository;
import fortscale.services.IUserScore;
import fortscale.services.IUserScoreHistoryElement;
import fortscale.services.UserService;
import fortscale.services.fe.ClassifierService;
import fortscale.utils.actdir.ADUserParser;

@Service("userService")
public class UserServiceImpl implements UserService{
	
	private static final String SEARCH_FIELD_PREFIX = "##";
	
	@Autowired
	private AdUserRepository adUserRepository;
	
	@Autowired
	private AdGroupRepository adGroupRepository;
		
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private AdUsersFeaturesExtractionRepository adUsersFeaturesExtractionRepository;
	
	@Autowired
	private ClassifierService classifierService;
	
	@Autowired
	private UserMachineDAO userMachineDAO;

	@Override
	public User getUserById(String uid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateUserWithCurrentADInfo() {
		updateUserWithADInfo(adUserRepository.getLatestTimeStamp());
	}
	
	@Override
	public void updateUserWithADInfo(String timestamp) {
		updateUserWithADInfo(adUserRepository.findByTimestamp(timestamp));
	}
	
	private void updateUserWithADInfo(Iterable<AdUser> adUsers) {
		for(AdUser adUser: adUsers){
			User user = userRepository.findByAdDn(adUser.getDistinguishedName());
			if(user == null){
				user = new User(adUser.getDistinguishedName());
			}
			user.setFirstname(adUser.getFirstname());
			user.setLastname(adUser.getLastname());
			if(adUser.getEmailAddress() != null && adUser.getEmailAddress().length() > 0){
				user.setEmailAddress(new EmailAddress(adUser.getEmailAddress()));
			}
			user.setAdUserPrincipalName(adUser.getUserPrincipalName());
			user.setEmployeeID(adUser.getEmployeeID());
			user.setManagerDN(adUser.getManager());
			user.setMobile(adUser.getMobile());
			user.setTelephoneNumber(adUser.getTelephoneNumber());
			user.setSearchField(createSearchField(user));
			user.setDepartment(adUser.getDepartment());
			user.setPosition(adUser.getTitle());
			ADUserParser adUserParser = new ADUserParser();
			String[] groups = adUserParser.getUserGroups(adUser.getMemberOf());
			if(groups != null){
				for(String groupDN: groups){
					AdGroup adGroup = adGroupRepository.findByDistinguishedName(groupDN);
					if(adGroup != null){
						user.addGroup(new AdUserGroup(groupDN, adGroup.getName()));
					}else{
						//TODO: LOG WARNING.
					}
				}
			}
			userRepository.save(user);
		}
		
	}
	
	private String createSearchField(User user){
		StringBuilder sb = new StringBuilder();
		if(user.getFirstname() != null && user.getFirstname().length() > 0){
			if(user.getLastname() != null && user.getLastname().length() > 0){
				sb.append(SEARCH_FIELD_PREFIX).append(user.getFirstname().toLowerCase()).append(" ").append(user.getLastname().toLowerCase());
				sb.append(SEARCH_FIELD_PREFIX).append(user.getLastname().toLowerCase()).append(" ").append(user.getFirstname().toLowerCase());
			} else{
				sb.append(SEARCH_FIELD_PREFIX).append(user.getFirstname().toLowerCase());
			}
		}else{
			if(user.getLastname() != null && user.getLastname().length() > 0){
				sb.append(SEARCH_FIELD_PREFIX).append(SEARCH_FIELD_PREFIX).append(user.getLastname().toLowerCase());
			}
		}
		
		if(sb.length() > 0 && user.getAdUserPrincipalName() != null && user.getAdUserPrincipalName().length() > 0){
			sb.append(SEARCH_FIELD_PREFIX).append(user.getAdUserPrincipalName().toLowerCase());
		}
		return sb.toString();
	}

	@Override
	public List<User> findBySearchFieldContaining(String prefix) {
		
		return userRepository.findBySearchFieldContaining(SEARCH_FIELD_PREFIX+prefix.toLowerCase());
	}

	@Override
	public List<IUserScore> getUserScores(String uid) {
		User user = userRepository.findOne(uid);
		if(user == null){
			return Collections.emptyList();
		}
		
		List<IUserScore> ret = new ArrayList<IUserScore>();
		for(ClassifierScore classifierScore: user.getScores().values()){
			UserScore score = new UserScore(classifierScore.getClassifierId(), classifierService.getClassifier(classifierScore.getClassifierId()).getDisplayName(),
					(int)classifierScore.getScore(), (int)classifierScore.getAvgScore());
			ret.add(score);
		}
		
//		Pageable pageable = new PageRequest(0, 1, Direction.DESC, AdUserFeaturesExtraction.timestampField);
//		List<AdUserFeaturesExtraction> ufeList = adUsersFeaturesExtractionRepository.findByUserId(user.getAdDn(), pageable);
//		if(ufeList == null || ufeList.size() == 0){
//			return Collections.emptyList();
//		}
//		AdUserFeaturesExtraction ufe = ufeList.get(0);
//		Double avgScore = adUsersFeaturesExtractionRepository.calculateAvgScore(Classifier.getAdClassifierUniqueName(), ufe.getTimestamp());
//		List<IUserScore> ret = new ArrayList<IUserScore>();
//		UserScore score = new UserScore("overall", "User Profile", ufe.getScore(), avgScore);
//		ret.add(score);
		return ret;
	}
	
	public List<IUserScoreHistoryElement> getUserScoresHistory(String uid, String classifierId){
		User user = userRepository.findOne(uid);
		List<IUserScoreHistoryElement> ret = new ArrayList<IUserScoreHistoryElement>();
		ClassifierScore classifierScore = user.getScore(classifierId);
		if(classifierScore != null){
			UserScoreHistoryElement userScoreHistoryElement = new UserScoreHistoryElement(classifierScore.getTimestamp(), classifierScore.getScore(), classifierScore.getAvgScore());
			ret.add(userScoreHistoryElement);
			if(classifierScore.getPrevScores() != null){
				for(ScoreInfo scoreInfo: classifierScore.getPrevScores()){
					userScoreHistoryElement = new UserScoreHistoryElement(scoreInfo.getTimestamp(), scoreInfo.getScore(), scoreInfo.getAvgScore());
					ret.add(userScoreHistoryElement);
				}
			}
		}
		
//		Pageable pageable = new PageRequest(0, 14, Direction.DESC, AdUserFeaturesExtraction.timestampField);
//		List<AdUserFeaturesExtraction> ufeList = adUsersFeaturesExtractionRepository.findByUserIdAndClassifierId(uid, classifierId, pageable);
//		if(ufeList == null || ufeList.size() == 0){
//			return Collections.emptyList();
//		}
//		
//		for(AdUserFeaturesExtraction ufe: ufeList){
//			Double avgScore = adUsersFeaturesExtractionRepository.calculateUsersDailyMaxScores(classifierId, uid);
//			UserScoreHistoryElement userScoreHistoryElement = new UserScoreHistoryElement(ufe.getTimestamp(), ufe.getScore(), avgScore);
//			ret.add(userScoreHistoryElement);
//		}
		return ret;
	}

	@Override
	public List<IFeature> getUserAttributesScores(String uid, String classifierId, Date timestamp) {
		AdUserFeaturesExtraction ufe = adUsersFeaturesExtractionRepository.findByUserIdAndClassifierIdAndTimestamp(uid, classifierId, timestamp);
		if(ufe == null || ufe.getAttributes() == null){
			return Collections.emptyList();
		}
		return ufe.getAttributes();
	}

	@Override
	public List<UserMachine> getUserMachines(String uid) {
		User user = userRepository.findOne(uid);
		String userName = user.getAdUserPrincipalName().split("@")[0];
		return userMachineDAO.findByUsername(userName);
	}

	
}
