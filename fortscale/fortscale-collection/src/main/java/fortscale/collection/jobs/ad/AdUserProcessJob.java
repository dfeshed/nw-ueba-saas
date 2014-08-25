package fortscale.collection.jobs.ad;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.kitesdk.morphline.api.Record;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;


import fortscale.collection.morphlines.RecordToBeanItemConverter;
import fortscale.collection.tagging.service.UserTagEnum;
import fortscale.collection.tagging.service.UserTaggingService;
import fortscale.collection.usersfiltering.service.SupportedUsersService;
import fortscale.collection.usersfiltering.service.impl.UsersFilterEnum;
import fortscale.domain.ad.AdGroup;
import fortscale.domain.ad.AdUser;
import fortscale.domain.ad.dao.AdGroupRepository;
import fortscale.domain.ad.dao.AdUserRepository;
import fortscale.services.UserServiceFacade;
import fortscale.services.impl.UsernameService;

public class AdUserProcessJob extends AdProcessJob {

	
	@Autowired
	private AdUserRepository adUserRepository;
	
	@Autowired
	private SupportedUsersService supportedUsersService;
	
	@Autowired
	private AdGroupRepository adgroupRepository;
	
	@Autowired
	private UserServiceFacade userServiceFacade;
	
	@Autowired
	private UsernameService usernameService;
	
	@Autowired
	private UserTaggingService userTaggingService;
	
	@Value("${users.filter.prioritylist:}")
    private String ouUsersFilter;
	
	@Value("${users.filter.limit:0}")
	private int usersNumberLimit;
	
	@Value("${users.filter.add_new_users:false}")
	
	private boolean addUsers;
	
	private RecordToBeanItemConverter<AdUser> converter;
	
	private boolean jobFirstRun;
	
	private static Logger logger = LoggerFactory.getLogger(AdUserProcessJob.class);
	
	@Override
	protected void init(JobExecutionContext jobExecutionContext) throws JobExecutionException{
		super.init(jobExecutionContext);
		
		converter = new RecordToBeanItemConverter<>(getOutputFields());
		if(!StringUtils.isEmpty(ouUsersFilter) && supportedUsersService.getSupportedUsersNumber() == 0){
			jobFirstRun = true;
		}else{
			jobFirstRun = false;
		}
	}
	
	@Override
	protected int getTotalNumOfSteps() {
		return 5;
	}

	@Override
	protected boolean isTimestampAlreadyProcessed(Date runtime) {
		return adUserRepository.countByTimestampepoch(runtime.getTime()) > 0 ? true : false;
	}

	@Override
	protected boolean updateDb(Record record) throws Exception {
		AdUser adUser = new AdUser();
		converter.convert(record, adUser);
		if(StringUtils.isEmpty(adUser.getDistinguishedName()) || StringUtils.isEmpty(adUser.getObjectGUID())){
			return false;
		}
		adUser.setLastModified(new Date());
		if(StringUtils.isEmpty(ouUsersFilter)){
			adUserRepository.save(adUser);
			userServiceFacade.updateUserWithADInfo(adUser);
		}else{
			if(addUsers == false && jobFirstRun == false){
				if(supportedUsersService.isSupportedUser(adUser.getObjectGUID())){
					adUserRepository.save(adUser);
					userServiceFacade.updateUserWithADInfo(adUser);
				}
				return true;
			}
			adUserRepository.save(adUser);	
			return true;
		}
		return true;
	}
	
	protected void updateUsersWhoBelongtoOUOrGroup() {

		ArrayList<Pair<String, UsersFilterEnum>> filtersPriorityList = getFiltersPriorityList();
		for (Pair<String, UsersFilterEnum> filter : filtersPriorityList) {
			if (filter.getRight() == UsersFilterEnum.OU) { // OU filter
				List<AdUser> users = adUserRepository.findAdUsersBelongtoOU(filter.getLeft());
				updateSupportedUsers(users);
			}
			else { // Group filter
				AdGroup adGroup = adgroupRepository.findByDistinguishedName(filter.getLeft());
				if(adGroup == null){
					logger.error("Users group filter does not exist : {}",filter.getLeft());
					continue;
				}
				String members = adGroup.getMember();
				List<String> membersList = Arrays.asList(members.split("\\s*;\\s*"));
				List<AdUser> users = adUserRepository.findByDnUsersIn(membersList);
				updateSupportedUsers(users);
				}
			}
		}
	
	protected void updateSupportedUsers(List<AdUser> users){
		for (AdUser aduser : users) {
			if (supportedUsersService.isSupportedUser(aduser.getObjectGUID())) {
				userServiceFacade.updateUserWithADInfo(aduser);
			}
			else {
				if (supportedUsersService.getSupportedUsersNumber() < usersNumberLimit) {
					userServiceFacade.updateUserWithADInfo(aduser);
					supportedUsersService.addSupportedUser(aduser.getObjectGUID());
				}
			}
		}
	}
	
	protected ArrayList<Pair<String, UsersFilterEnum>> getFiltersPriorityList(){
		if(!ouUsersFilter.startsWith("[") || !ouUsersFilter.endsWith("]")){
			throw new IllegalArgumentException("Users filter priority list must be enclosed with []");
		}
		String filtersStr = ouUsersFilter.substring(1, ouUsersFilter.length()-1);
		String[] filtersList = filtersStr.split("\\s*;\\s*");
		ArrayList<Pair<String, UsersFilterEnum>> filtersPriorityList = new ArrayList<Pair<String, UsersFilterEnum>> ();
		for(String filter : filtersList){
			String regex = "\\s*\"(.*)\"\\s*:\\s*\"(.*)\"\\s*";
			Pattern pattern = Pattern.compile(regex);
			Matcher m = pattern.matcher(filter);
			if(m.matches() == false){
				throw new IllegalArgumentException("Bad Users filter format");
			}
			String filterName = m.group(1);
			UsersFilterEnum filterKind;
			if(m.group(2).equals("group")){
				filterKind = UsersFilterEnum.GROUP;
			}else if(m.group(2).equals("ou")){
				filterKind = UsersFilterEnum.OU;
			}else{
				throw new IllegalArgumentException("Filter should be group/ou");
			}
			filtersPriorityList.add(new ImmutablePair<String, UsersFilterEnum> (filterName, filterKind));
		}
		return filtersPriorityList;
	}
	
	@Override
	protected String getDataRecievedType() {
		return "Users";
	}
	
	protected void runFinalStep() throws Exception{
		startNewStep("update username set");
		usernameService.update();
		// Update admin tag
		userTaggingService.update(UserTagEnum.executive.getId());
		userTaggingService.update(UserTagEnum.admin.getId());
		if(!StringUtils.isEmpty(ouUsersFilter)){
			if(addUsers == true){
				updateUsersWhoBelongtoOUOrGroup();
			}
			if(addUsers == false && jobFirstRun){
				updateUsersWhoBelongtoOUOrGroup();
				adUserRepository.deleteAll();
			}
		}
		finishStep();
	}
}
