package fortscale.collection.jobs.ad;

import fortscale.collection.morphlines.RecordToBeanItemConverter;
import fortscale.domain.ad.AdGroup;
import fortscale.domain.ad.AdUser;
import fortscale.domain.ad.dao.AdGroupRepository;
import fortscale.domain.ad.dao.AdUserRepository;
import fortscale.services.UserServiceFacade;
import fortscale.domain.core.UserTagEnum;
import fortscale.services.UserTaggingService;
import fortscale.services.impl.ParsingUsersMachinesFiltering;
import fortscale.services.impl.UsernameService;
import fortscale.services.impl.UsersMachinesFilterEnum;
import fortscale.services.users.SupportedUsersService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.kitesdk.morphline.api.Record;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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

		//in case that we don't filter by ou or we have white list and the current user appear at the white list
		if(StringUtils.isEmpty(ouUsersFilter) || supportedUsersService.isSupportedUsername(adUser.getUserPrincipalName()) ||
				supportedUsersService.isSupportedUsername(adUser.getsAMAccountName())){
			adUserRepository.save(adUser);
			userServiceFacade.updateUserWithADInfo(adUser);
		}
		else{
			// in case that add new user flag is false and this is not the first run
			if(addUsers == false && jobFirstRun == false){
				if(supportedUsersService.isSupportedUser(adUser.getObjectGUID())){
					adUserRepository.save(adUser);
					userServiceFacade.updateUserWithADInfo(adUser);
				}
			}
			else{
				adUserRepository.save(adUser);	
			}
		}
		return true;
	}
	
	protected void updateUsersWhoBelongtoOUOrGroup() {
		ArrayList<Pair<String, UsersMachinesFilterEnum>> filtersPriorityList = ParsingUsersMachinesFiltering.getFiltersList(ouUsersFilter);
		for (Pair<String, UsersMachinesFilterEnum> filter : filtersPriorityList) {
			if (filter.getRight() == UsersMachinesFilterEnum.OU) { // OU filter
				Pageable pageable = new PageRequest(0, 1000);
				String runtime = adUserRepository.getAdUsersLastSnapshotRuntime();
				Page<AdUser> users = adUserRepository.findAdUsersBelongtoOUInSnapshot(filter.getLeft(), pageable, runtime);
				while(users!= null && users.hasContent()){
					updateSupportedUsers(users.getContent());
					pageable = pageable.next();
					users = adUserRepository.findAdUsersBelongtoOUInSnapshot(filter.getLeft(), pageable, runtime);
				}
			}
			else { // Group filter
				AdGroup adGroup = adgroupRepository.findByDistinguishedNameInLastSnapshot(filter.getLeft());
				if(adGroup == null){
					logger.error("Users group filter does not exist : {}",filter.getLeft());
					continue;
				}
				String members = adGroup.getMember();
                if (members!=null) {
                    List<String> membersList = Arrays.asList(members.split("\\s*;\\s*"));
                    List<AdUser> users = adUserRepository.findByDnUsersIn(membersList);
                    updateSupportedUsers(users);
                }
			}
		}


	}
	
	protected void updateSupportedUsers(List<AdUser> users){
		for (AdUser aduser : users) {
			if (supportedUsersService.isSupportedUser(aduser.getObjectGUID())) {
				userServiceFacade.updateUserWithADInfo(aduser);
			}
			else {
				if (usersNumberLimit == 0 || supportedUsersService.getSupportedUsersNumber() < usersNumberLimit) {
					userServiceFacade.updateUserWithADInfo(aduser);
					supportedUsersService.addSupportedUser(aduser.getObjectGUID());
				}
			}
		}
	}
	
	@Override
	protected String getDataRecievedType() {
		return "Users";
	}
	
	protected void runFinalStep() throws Exception{
		startNewStep("update username set");

		// Update admin tag
		userTaggingService.update(UserTagEnum.executive.getId());
		userTaggingService.update(UserTagEnum.admin.getId());
		userTaggingService.update(UserTagEnum.service.getId());
		userTaggingService.update(UserTagEnum.LR.getId());
		userTaggingService.update(UserTagEnum.custom.getId());
		if(!StringUtils.isEmpty(ouUsersFilter)){
			if(addUsers == true){
				updateUsersWhoBelongtoOUOrGroup();
			}
			if(addUsers == false && jobFirstRun){
				updateUsersWhoBelongtoOUOrGroup();
				adUserRepository.deleteAll();
			}
		}

		//update the username serivice cahce with the user that was updated in the User collection at the mongo
		usernameService.updateUsernameCaches();
		finishStep();
	}
}
