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

import fortscale.services.SensitiveMachineService;

@Service("sensitiveMachineService")
public class SensitiveMachineServiceImpl implements SensitiveMachineService,
		InitializingBean {

	private static Logger logger = LoggerFactory
			.getLogger(SensitiveMachineServiceImpl.class);

	private Set<String> sensitiveMachines = null;
	@Value("${user.list.service_sensitive_machine.path:}")
	private String filePath;

	@Override
	public void afterPropertiesSet() throws Exception {
		if (!StringUtils.isEmpty(filePath)) {
			File machinesFile = new File(filePath);
			if (machinesFile.exists() && machinesFile.isFile()) {
				sensitiveMachines = new HashSet<String>(
						FileUtils.readLines(machinesFile));
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

}
