package fortscale.services.impl;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import fortscale.domain.core.Computer;
import fortscale.domain.core.dao.ComputerRepository;
import fortscale.services.SensitiveMachineService;

@Service("sensitiveMachineService")
public class SensitiveMachineServiceImpl implements SensitiveMachineService,
		InitializingBean {

	private static Logger logger = LoggerFactory
			.getLogger(SensitiveMachineServiceImpl.class);

	@Autowired
	private ComputerRepository computerRepository;

	private Set<String> sensitiveMachines = null;

	@Value("${user.list.service_sensitive_machine.path:}")
	private String filePath;

	@Value("${user.list.service_sensitive_machine.deletion_symbol:}")
	private String deletionSymbol;

	@Override
	public void afterPropertiesSet() throws Exception {

		sensitiveMachines = loadSensitiveMachinesFromMongo();
		if (!StringUtils.isEmpty(filePath)) {
			File machinesFile = new File(filePath);
			if (machinesFile.exists() && machinesFile.isFile()) {
				Set<String> machinesFromFile = new HashSet<String>(
						FileUtils.readLines(machinesFile));
				for (String machineLine : machinesFromFile) {
					if (machineLine.startsWith(deletionSymbol)) {
						String machine = machineLine.substring(1);
						if (sensitiveMachines.contains(machine)) {
							computerRepository.updateSensitiveMachine(computerRepository.findByName(machine), false);
							sensitiveMachines.remove(machine);
						}
					} else {
						if (!sensitiveMachines.contains(machineLine)) {
							computerRepository.updateSensitiveMachine(
									computerRepository.findByName(machineLine),
									true);
							sensitiveMachines.add(machineLine);
						}
					}
				}
			} else {
				logger.warn("SensitiveMachine file not found in path: %s",
						filePath);
			}
		} else {
			logger.info("SensitiveMachine file path not configured");
		}
	}

	@Override
	public boolean isMachineSensitive(String machineName) {
		if (sensitiveMachines != null) {
			return sensitiveMachines.contains(machineName);
		}
		return false;
	}

	private Set<String> loadSensitiveMachinesFromMongo() {
		Set<String> res = new HashSet<String>();
		List<Computer> computers = computerRepository.findByIsSensitive(true);
		for (Computer c : computers) {
			res.add(c.getName());
		}
		return res;
	}

}