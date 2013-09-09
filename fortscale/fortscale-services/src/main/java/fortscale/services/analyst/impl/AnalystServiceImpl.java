package fortscale.services.analyst.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import fortscale.domain.analyst.Analyst;
import fortscale.domain.analyst.dao.AnalystRepository;
import fortscale.domain.core.EmailAddress;
import fortscale.services.analyst.AnalystService;



@Service("analystService")
public class AnalystServiceImpl implements AnalystService{
	
	@Autowired
	private AnalystRepository analystRepository;
	

	@Override
	public void create(String userName, String password,
			String emailAddress, String firstName, String lastName) {
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
		Analyst analyst = new Analyst(userName, password, new EmailAddress(emailAddress), firstName, lastName, authorities);
		analystRepository.save(analyst);
	}

}
