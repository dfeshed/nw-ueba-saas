package fortscale.services.impl;

import java.util.ArrayList;
import java.util.Calendar;
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
import fortscale.domain.fe.AuthScore;
import fortscale.domain.fe.IFeature;
import fortscale.domain.fe.VpnScore;
import fortscale.domain.fe.dao.AdUsersFeaturesExtractionRepository;
import fortscale.domain.fe.dao.AuthDAO;
import fortscale.domain.fe.dao.VpnDAO;
import fortscale.services.IUserScore;
import fortscale.services.IUserScoreHistoryElement;
import fortscale.services.UserService;
import fortscale.services.fe.Classifier;
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
	
	@Autowired
	private AuthDAO authDAO;
	
	@Autowired
	private VpnDAO vpnDAO;

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
			user.setAdUserPrincipalName(adUser.getUserPrincipalName().toLowerCase());
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
					(int)Math.round(classifierScore.getScore()), (int)Math.round(classifierScore.getAvgScore()));
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
			
			if(classifierScore.getPrevScores() != null){
				ScoreInfo scoreInfo = null;
				for(int i = classifierScore.getPrevScores().size() -1; i >= 0; i--){
					scoreInfo = classifierScore.getPrevScores().get(i);
					UserScoreHistoryElement userScoreHistoryElement = new UserScoreHistoryElement(scoreInfo.getTimestamp(), scoreInfo.getScore(), scoreInfo.getAvgScore());
					ret.add(userScoreHistoryElement);
				}
				if(isOnSameDay(classifierScore.getTimestamp(), scoreInfo.getTimestamp())){
					if(classifierScore.getScore() >= scoreInfo.getScore()){
						UserScoreHistoryElement userScoreHistoryElement = new UserScoreHistoryElement(classifierScore.getTimestamp(), classifierScore.getScore(), classifierScore.getAvgScore());
						ret.set(classifierScore.getPrevScores().size() -1, userScoreHistoryElement);
					}
				} else{
					UserScoreHistoryElement userScoreHistoryElement = new UserScoreHistoryElement(classifierScore.getTimestamp(), classifierScore.getScore(), classifierScore.getAvgScore());
					ret.add(userScoreHistoryElement);
				}
			} else{
				UserScoreHistoryElement userScoreHistoryElement = new UserScoreHistoryElement(classifierScore.getTimestamp(), classifierScore.getScore(), classifierScore.getAvgScore());
				ret.add(userScoreHistoryElement);
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
		Collections.sort(ufe.getAttributes(), new IFeature.OrderByScoreDesc());
		return ufe.getAttributes();
	}
	
	

	@Override
	public List<UserMachine> getUserMachines(String uid) {
		User user = userRepository.findOne(uid);
		String userName = user.getAdUserPrincipalName().split("@")[0];
		return userMachineDAO.findByUsername(userName);
	}

	@Override
	public void updateUserWithAuthScore() {
		Date lastRun = authDAO.getLastRunDate();
		double avg = authDAO.calculateAvgScoreOfGlobalScore(lastRun);
		for(AuthScore authScore: authDAO.findGlobalScoreByTimestamp(lastRun)){
			User user = userRepository.findByAdUserPrincipalName(authScore.getUserName().toLowerCase());
			if(user == null){
				//TODO:	error log message
				continue;
			}
			updateUserScore(user, lastRun, Classifier.auth.getId(), authScore.getGlobalScore(), avg);
		}
		
	}
	
	@Override
	public void updateUserWithVpnScore() {
		Date lastRun = vpnDAO.getLastRunDate();
		double avg = vpnDAO.calculateAvgScoreOfGlobalScore(lastRun);
		for(VpnScore vpnScore: vpnDAO.findGlobalScoreByTimestamp(lastRun)){
			User user = userRepository.findByAdUserPrincipalName(vpnScore.getUserName().toLowerCase());
			if(user == null){
				//TODO:	error log message
				continue;
			}
			updateUserScore(user, lastRun, Classifier.vpn.getId(), vpnScore.getGlobalScore(), avg);
		}
		
	}
	
	@Override
	public void updateUserScore(User user, Date timestamp, String classifierId, double value, double avgScore){
		ClassifierScore cScore = user.getScore(classifierId);
		if(cScore == null){
			cScore = new ClassifierScore();
			cScore.setClassifierId(classifierId);
		}else{
			ScoreInfo scoreInfo = new ScoreInfo();
			scoreInfo.setScore(cScore.getScore());
			scoreInfo.setAvgScore(cScore.getAvgScore());
			scoreInfo.setTimestamp(cScore.getTimestamp());
			List<ScoreInfo> prevScores = cScore.getPrevScores();
			if(prevScores.isEmpty()){
				prevScores = new ArrayList<ScoreInfo>();
				prevScores.add(scoreInfo);
			} else{
				if(isOnSameDay(prevScores.get(0).getTimestamp(), scoreInfo.getTimestamp())){
					if(prevScores.get(0).getScore() < scoreInfo.getScore()){
						prevScores.set(0, scoreInfo);
					}
				} else{
					prevScores.add(0, scoreInfo);
				}
			}
			cScore.setPrevScores(prevScores);
		}
		cScore.setScore(value);
		cScore.setAvgScore(avgScore);
		cScore.setTimestamp(timestamp);
		user.putClassifierScore(cScore);
		userRepository.save(user);
	}
	
	private boolean isOnSameDay(Date date1, Date date2){
		Calendar tmp = Calendar.getInstance();
		tmp.setTime(date1);
		Calendar tmp1 = Calendar.getInstance();
		tmp1.setTime(date2);
		int day1 = tmp.get(Calendar.DAY_OF_YEAR);
		int day2 = tmp1.get(Calendar.DAY_OF_YEAR);
		
		return (day1 == day2);
	}

	
}
