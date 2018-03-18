package fortscale.services.analyst.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;




import fortscale.domain.analyst.Analyst;
import fortscale.domain.analyst.AnalystAuth;
import fortscale.domain.analyst.dao.AnalystRepository;
import fortscale.domain.core.User;
import fortscale.services.analyst.AnalystService;
import fortscale.utils.logging.Logger;



@Service("analystService")
public class AnalystServiceImpl implements AnalystService{
	private static Logger logger = Logger.getLogger(AnalystServiceImpl.class);
	
	@Autowired
	private AnalystRepository analystRepository;

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
