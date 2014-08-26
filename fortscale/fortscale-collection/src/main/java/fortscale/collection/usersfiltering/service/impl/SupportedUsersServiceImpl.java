package fortscale.collection.usersfiltering.service.impl;

import java.util.HashSet;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import fortscale.collection.usersfiltering.service.SupportedUsersService;
import fortscale.domain.core.dao.UserRepository;

@Service("supportedUsersService")
public class SupportedUsersServiceImpl implements SupportedUsersService, InitializingBean{
	private HashSet<String> supportedUsersGUID;
	@Autowired
	private UserRepository userRepository;
	@Value("${users.filter.prioritylist:}")
    private String ouUsersFilter;
	
	@Override
	public void afterPropertiesSet()
		throws Exception {

		if (!StringUtils.isEmpty(ouUsersFilter)) {
			if (userRepository.count() != 0) {
				supportedUsersGUID = userRepository.getUsersGUID();
			}
			else {
				supportedUsersGUID = new HashSet<String>();
			}
		}
	}
	public boolean isSupportedUser(String userGUID){
		return supportedUsersGUID.contains(userGUID);
	}
	public int getSupportedUsersNumber(){
		return supportedUsersGUID.size();
	}
	public void addSupportedUser(String userGUID){
		supportedUsersGUID.add(userGUID);
	}
}
