package fortscale.services.impl;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import fortscale.domain.core.Computer;
import fortscale.domain.core.dao.ComputerRepository;
import fortscale.services.FilterMachinesService;

@Service("filterMachinesService")
public class FilterMachinesServiceImpl implements FilterMachinesService,
		InitializingBean {
	LoadingCache<String, Boolean> OUMachinesCache;
	@Autowired
	private ComputerRepository computerRepository;
	@Value("${machines.ou.filter:}")
	private String ouName;

	@Override
	public void afterPropertiesSet() throws Exception {

		OUMachinesCache = CacheBuilder.newBuilder().maximumSize(100000)
				.expireAfterWrite(1, TimeUnit.HOURS)
				.build(new CacheLoader<String, Boolean>() {
					public Boolean load(String key) {
						return getIfBelongToOU(key);
					}
				});
	}

	private Boolean getIfBelongToOU(String computerName) {
		Computer computer = computerRepository.findByName(computerName);
		if (computer == null) {
			return false;
		} else {
			String dn = computer.getDistinguishedName();
			if (dn != null && dn.endsWith(ouName)) {
				return true;
			} else {
				return false;
			}
		}
	}

	public boolean toFilter(String computerName) {
		if (StringUtils.isEmpty(ouName)) {
			return false;
		}
		if (OUMachinesCache.getUnchecked(computerName)) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void invalidateKey(String computerName) {
		if (StringUtils.isEmpty(ouName)) {
			return;
		}
		OUMachinesCache.invalidate(computerName);
	}

}
