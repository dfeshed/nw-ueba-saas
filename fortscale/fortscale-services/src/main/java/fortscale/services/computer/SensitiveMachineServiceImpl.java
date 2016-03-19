
package fortscale.services.computer;

import fortscale.domain.core.dao.ComputerRepository;
import fortscale.services.cache.CacheHandler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SensitiveMachineServiceImpl implements SensitiveMachineService, InitializingBean {

	private static Logger logger = LoggerFactory.getLogger(SensitiveMachineServiceImpl.class);

	@Autowired
	private ComputerRepository computerRepository;

	@Autowired
	@Qualifier("sensitiveMachineCache")
	private CacheHandler<String, String> cache;

	@Value("${user.list.service_sensitive_machine.path}")
	private String filePath;

	@Value("${user.list.service_sensitive_machine.deletion_symbol:-}")
	private String deletionSymbol;

    @Value("${computer.tag.service.lazy.upload:false}")
    private boolean isLazyUpload;

	public SensitiveMachineServiceImpl() {
	}

	public SensitiveMachineServiceImpl(ComputerRepository computerRepository, CacheHandler<String, String> cache) {
		this();
		this.computerRepository = computerRepository;
		setCache(cache);
	}

	@Override
	public void afterPropertiesSet()
		throws Exception {
        //In case that Lazy flag turned on the tags will be loaded from db during the tagging or querying process
        if (!isLazyUpload) {
            refreshSensitiveMachines();
        }
	}

	@Override
	public boolean isMachineSensitive(String machineName) {
		boolean isMachineSensitive = false;
		machineName = machineName.toUpperCase();
		if (cache != null && cache.get(machineName) != null) {
			isMachineSensitive = true;
		}
		return isMachineSensitive;
	}

	public void refreshSensitiveMachines() {

		List<String> computers = computerRepository.findNameByIsSensitive(true);
		for (String computer : computers){
			cache.put(computer,computer);
		}
	}

	public void updateSensitiveMachines()
		throws IOException {

		if (!StringUtils.isEmpty(filePath)) {
			File machinesFile = new File(filePath);
			if (machinesFile.exists() && machinesFile.isFile()) {
				Set<String> machinesFromFile = null;
				machinesFromFile = new HashSet<String>(FileUtils.readLines(machinesFile));
				for (String machineLine : machinesFromFile) {
					if (machineLine.startsWith(deletionSymbol)) {
						String machine = machineLine.substring(1).toUpperCase();
						if (cache.get(machine) != null) {
							boolean computerExists = computerRepository.findIfComputerExists(machine);
							if (computerExists) {
								computerRepository.updateSensitiveMachineByName(machine, false);
							}
							cache.remove(machine);
						}
					}
					else {
						String machine = machineLine.toUpperCase();
						if (cache.get(machine) == null) {
							boolean computerExists = computerRepository.findIfComputerExists(machine);
							if (computerExists) {
								computerRepository.updateSensitiveMachineByName(machine, true);
								cache.put(machine, machine);
							}
						}
					}
				}
			}
			else {
				logger.warn("SensitiveMachine file not found in path: {}", filePath);
			}
		}
		else {
			logger.info("SensitiveMachine file path not configured");
		}
	}

	public String getFilePath() {

		return filePath;
	}

	public void setFilePath(String filePath) {

		this.filePath = filePath;
	}

	public CacheHandler getCache() {
		return cache;
	}

	public void setCache(CacheHandler cache) {
		this.cache = cache;
	}

	public String getDeletionSymbol() {

		return deletionSymbol;
	}

	public void setDeletionSymbol(String deletionSymbol) {

		this.deletionSymbol = deletionSymbol;
	}

	@Override public void handleNewValue(String key, String value) throws Exception {
		if(value == null){
			getCache().remove(key);
		}
		else {
			getCache().putFromString(key, value);
		}
	}
}
