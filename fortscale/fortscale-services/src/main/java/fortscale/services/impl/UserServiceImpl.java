package fortscale.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fortscale.domain.ad.AdUser;
import fortscale.domain.ad.dao.AdUserRepository;
import fortscale.domain.core.EmailAddress;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.UserRepository;
import fortscale.services.UserService;

@Service("userService")
public class UserServiceImpl implements UserService{
	
	private static final String SEARCH_FIELD_PREFIX = "##";
	
	@Autowired
	private AdUserRepository adUserRepository;
		
	@Autowired
	private UserRepository userRepository;

	@Override
	public User getUserById(String uid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateUserWithCurrentADInfo() {
		for(AdUser adUser: adUserRepository.findAll()){
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
}
