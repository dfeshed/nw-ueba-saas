package fortscale.services.impl;

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
			userRepository.save(user);
		}
		
	}
}
