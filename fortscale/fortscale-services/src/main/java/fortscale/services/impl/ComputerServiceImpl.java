package fortscale.services.impl;

import static org.python.google.common.base.Preconditions.checkNotNull;

import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import fortscale.domain.ad.AdComputer;
import fortscale.domain.core.Computer;
import fortscale.domain.core.ComputerUsageType;
import fortscale.domain.core.dao.ComputerRepository;
import fortscale.services.ComputerService;
import fortscale.services.FilterMachinesService;
import fortscale.services.computer.EndpointDetectionService;
import fortscale.utils.ConfigurationUtils;
import fortscale.utils.actdir.ADParser;


@Service
public class ComputerServiceImpl implements ComputerService {

	private static Logger logger = LoggerFactory.getLogger(ComputerServiceImpl.class);
	
	@Autowired
	private ComputerRepository repository;
	
	@Autowired
	private FilterMachinesService filterMachinesService;
	
	@Autowired
	private EndpointDetectionService endpointDetectionService;
    
	@Value("${computer.cluster.regex.patterns:}")
	private String clusterGroupsRegexProperty;
	
	
	private RegexMatcher clusterMatcher;
	
	private ADParser parser = new ADParser();
	
	public void updateComputerWithADInfo(AdComputer computer) {
		checkNotNull(computer);
		
		Date whenChanged = null;
		try {
			whenChanged = parser.parseDate(computer.getWhenChanged());
		} catch (ParseException e) {
			logger.error("computer whenChanged field value '{}' not match expected format for computer {}", computer.getWhenChanged(), computer.getCn());
			return;
		}

		
		// find out if the AD computer record is newer than what we have, 
		// if not skip the whole process. Otherwise, look for the existing
		// document in mongo, create one if not exist or update the existing 
		Computer saved = repository.findByName(computer.getCn().toUpperCase());
		if (saved!=null && saved.getWhenChanged()!=null && !saved.getWhenChanged().before(whenChanged)) {
			// skip this record as we already have a newer snapshot in place
			return;
		}

		// check if the repository already contains such a computer
		if (saved==null)
			saved = new Computer();
		
		// merge new computer info into the saved computer
		mergeComputerInfo(saved, computer);
		
		// re-calculate the computer classification for new or updated computer info
		endpointDetectionService.classifyNewComputer(saved); 
		
		try {
			repository.save(saved);
			//update OU machines cache
			if(filterMachinesService != null){
				filterMachinesService.invalidateKey(computer.getCn());
			}
		} catch (org.springframework.dao.DuplicateKeyException e) {
			// safe to ignore as it will be saved in the next ETL run  
			logger.warn("race condition encountered when trying to save computer {}", saved.getName());
		}
	}
	
	public ComputerUsageType getComputerUsageType(String hostname) {
		checkNotNull(hostname);

		// get the computer from the repository, use upper case for host name
		// as we case insensitive search
		Computer computer = repository.findByName(hostname.toUpperCase());
		if (computer==null) {
			// create a new computer instance type for the discovered host
			computer = new Computer();
			computer.setName(hostname.toUpperCase());
			computer.setTimestamp(new Date());
			
			// classify the new computer
			endpointDetectionService.classifyNewComputer(computer);
			
			// save the new computer
			try {
				computer = repository.save(computer);
			} catch (org.springframework.dao.DuplicateKeyException e) {
				// safe to ignore as it is saved by some thread that beat us to it
				logger.warn("race condition encountered when trying to save computer {}", computer.getName());
			}
		}
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

	
	/**
	 * Gets the cluster group name for the given hostname. The cluster 
	 * group name is a virtual name used to depict all hosts that are 
	 * part of a cluster of hosts and serve a common functionality 
	 * in the system.
	 */
	public String getClusterGroupNameForHostname(String hostname) {
		checkNotNull(hostname);
		
		// strip the hostname up to the first .
		if (hostname.contains("."))
			hostname = hostname.substring(0, hostname.indexOf("."));
				
		RegexMatcher matcher = getClusterGroupsRegexMatcher();
		return matcher.replaceInPlace(hostname).toUpperCase();
	}
	
	private RegexMatcher getClusterGroupsRegexMatcher() {
		if (clusterMatcher==null) {
			String[][] configPatternsArray = ConfigurationUtils.getStringArrays(clusterGroupsRegexProperty);
			clusterMatcher = new RegexMatcher(configPatternsArray);
		}
		return clusterMatcher;
	}
	
	public void setClusterGroupsRegexProperty(String val) {
		this.clusterGroupsRegexProperty = val;
	}
	
	public void classifyAllComputers() {
		// go over the computers in the repository in pages 
		// and classify all of them 
		Pageable pageRequest = new PageRequest(0, 50);
		Page<Computer> computers = repository.findAll(pageRequest);
		while (computers!=null && computers.hasContent()) {
			// classify all computers in the page
			List<Computer> changedComptuers = new LinkedList<Computer>();
			for (Computer computer : computers) {
				boolean changed = endpointDetectionService.classifyComputer(computer);
				if (changed)
					changedComptuers.add(computer);
			}
			
			// save all changed computers
			repository.save(changedComptuers);
			changedComptuers.clear();
			
			// get next page
			pageRequest = pageRequest.next();
			computers = repository.findAll(pageRequest);
		}
	}
	
}
