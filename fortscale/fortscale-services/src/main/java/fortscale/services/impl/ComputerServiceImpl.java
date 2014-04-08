package fortscale.services.impl;

import static org.python.google.common.base.Preconditions.checkNotNull;

import java.text.ParseException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fortscale.domain.ad.AdComputer;
import fortscale.domain.core.Computer;
import fortscale.domain.core.ComputerUsageType;
import fortscale.domain.core.dao.ComputerRepository;
import fortscale.services.ComputerService;
import fortscale.services.computer.EndpointDetectionService;
import fortscale.utils.actdir.ADParser;


@Service
public class ComputerServiceImpl implements ComputerService {

	private static Logger logger = LoggerFactory.getLogger(ComputerServiceImpl.class);
	
	@Autowired
	private ComputerRepository repository;
	
	@Autowired
	private EndpointDetectionService endpointDetectionService;
	
	private ADParser parser = new ADParser();
	
	public void updateComputerWithADInfo(AdComputer computer) {
		checkNotNull(computer);
		
		// find out if the AD computer record is newer than what we have, 
		// if not skip the whole process. Otherwise, look for the existing
		// document in mongo, create one if not exist or update the existing 
		
		Date latestWhenChanged = repository.getLatestWhenChanged();
		
		Date whenChanged = null;
		try {
			whenChanged = parser.parseDate(computer.getWhenChanged());
		} catch (ParseException e) {
			logger.error("computer whenChanged field value '{}' not match expected format for computer {}", computer.getWhenChanged(), computer.getCn());
			return;
		}
		
		if (latestWhenChanged!=null && latestWhenChanged.after(whenChanged)) {
			// skip this record as we already have a newer snapshot in place
			return;
		}
		
		// check if the repository already contains such a computer
		Computer saved = repository.findByName(computer.getCn());
		if (saved==null)
			saved = new Computer();
		
		// merge new computer info into the saved computer
		mergeComputerInfo(saved, computer);
		
		// re-calculate the computer classification for new or updated computer info
		endpointDetectionService.classifyComputer(saved);
		
		// TODO: consider to replace with update
		repository.save(saved);
	}
	
	public ComputerUsageType getComputerUsageType(String hostname) {
		checkNotNull(hostname);

		// get the computer from the repository, use upper case for host name
		// as we case insensitive search
		Computer computer = repository.findByName(hostname.toUpperCase());
		boolean created = false;
		if (computer==null) {
			// create a new computer instance type for the discovered host
			computer = new Computer();
			computer.setName(hostname.toUpperCase());
			computer.setTimestamp(new Date());
			
			created = true;
		} 
		// check if classification update are needed, if so update it in the repository
		// and return the usage type for the computer
		boolean changed = endpointDetectionService.classifyComputer(computer);
		if (created || changed)
			repository.save(computer);
		return computer.getUsageType();
	}
	
	private void mergeComputerInfo(Computer computer, AdComputer adComputer) {
		computer.setTimestamp(new Date());
		computer.setName(adComputer.getCn());
		computer.setDistinguishedName(adComputer.getDistinguishedName());
		computer.setOperatingSystem(adComputer.getOperatingSystem());
		computer.setOperatingSystemServicePack(adComputer.getOperatingSystemServicePack());
		computer.setOperatingSystemVersion(adComputer.getOperatingSystemVersion());
		try {
			computer.setWhenChanged(parser.parseDate(adComputer.getWhenChanged()));
		} catch (ParseException e) {
			logger.error("computer whenChanged field value '{}' not match expected format for computer {}", adComputer.getWhenChanged(), adComputer.getCn());
		}
		try {
			computer.setWhenCreated(parser.parseDate(adComputer.getWhenCreated()));
		} catch (ParseException e) {
			logger.error("computer whenCreated field value '{}' not match expected format for computer {}", adComputer.getWhenCreated(), adComputer.getCn());
		}	
	}

	
}
