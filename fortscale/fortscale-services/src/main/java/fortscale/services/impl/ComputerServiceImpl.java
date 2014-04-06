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
import fortscale.domain.core.dao.ComputerRepository;
import fortscale.services.ComputerService;
import fortscale.utils.actdir.ADParser;


@Service
public class ComputerServiceImpl implements ComputerService {

	private static Logger logger = LoggerFactory.getLogger(ComputerServiceImpl.class);
	
	@Autowired
	private ComputerRepository repository;
	
	private ADParser parser = new ADParser();
	
	public void updateComputerWithADInfo(AdComputer computer) {
		checkNotNull(computer);
		
		// find out if the AD computer record is newer than what we have, 
		// if not skip the whole process. Otherwise, look for the existing
		// document in mongo, create one if not exist or update the existing 
		
		Date latestWhenChanged = repository.getLatestWhenChanged();
		
		try {
			Date whenChanged = parser.parseDate(computer.getWhenChanged());
			
			if (latestWhenChanged!=null && latestWhenChanged.after(whenChanged)) {
				// skip this record as we already have a newer snapshot in place
				return;
			}
			
			// check if the repository already contains such a computer
			Computer saved = repository.findByName(computer.getCn());
			if (saved==null)
				saved = new Computer();
			
			mergeComputerInfo(saved, computer);
			// TODO: consider to replace with update
			repository.save(saved);
			
		} catch (ParseException e) {
			logger.error("computer whenChanged field value '{}' not match expected format for computer {}", computer.getWhenChanged(), computer.getCn());
		}
	}
	
	private void mergeComputerInfo(Computer computer, AdComputer adComputer) {
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
