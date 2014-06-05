package fortscale.services.impl;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.InitializingBean;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import fortscale.services.SensetiveMachineService;

@Service("sensetiveMachineService")
public class SensetiveMachineServiceImpl implements SensetiveMachineService,
		InitializingBean {

	private static Logger logger = LoggerFactory
			.getLogger(SensetiveMachineServiceImpl.class);

	private Set<String> sensetiveMachines = null;
	@Value("${user.list.service_sensetive_machine.path:}")
	private String filePath;

	@Override
	public void afterPropertiesSet() throws Exception {
		if (!StringUtils.isEmpty(filePath)) {
			File machinesFile = new File(filePath);
			if (machinesFile.exists() && machinesFile.isFile()) {
				sensetiveMachines = new HashSet<String>(
						FileUtils.readLines(machinesFile));
			} else {
				logger.warn("SensetiveMachine file not found in path: %s",
						filePath);
			}
		} else {
			logger.info("SensetiveMachine file path not configured");
		}
	}

	@Override
	public boolean isMachineSensitive(String machineName) {

		if (sensetiveMachines != null) {
			return sensetiveMachines.contains(machineName);
		}
		return false;
	}

}
