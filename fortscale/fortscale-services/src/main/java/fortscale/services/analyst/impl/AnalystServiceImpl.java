package fortscale.services.analyst.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import fortscale.domain.analyst.Analyst;
import fortscale.domain.analyst.dao.AnalystRepository;
import fortscale.domain.core.EmailAddress;
import fortscale.services.analyst.AnalystService;



@Service("analystService")
public class AnalystServiceImpl implements AnalystService{
	
	@Autowired
	private AnalystRepository analystRepository;
	
	

	@Override
	public void replaceEmailAddress(String username, String emailAddress) {
		Assert.hasText(username);
		Assert.notNull(emailAddress);
		
		
		Analyst analyst = analystRepository.findByUserName(username);
		if(analyst == null) {
			throw new UsernameNotFoundException(username);
		}
		
		if(!analyst.getEmailAddress().equals(new EmailAddress(emailAddress))) {
			analystRepository.save(analyst);
		}
	}



	@Override
	public Analyst findByUsername(String username) {
		return analystRepository.findByUserName(username);
	}



	@Override
	public List<Analyst> findAll() {
		return analystRepository.findAll();
	}



	@Override
	public List<Analyst> findAllNonDisabledUsers() {
		return analystRepository.findByIsDisabled(false);
	}

}
