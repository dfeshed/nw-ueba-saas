package fortscale.services.impl;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
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
	@Value("${machines.ou.filters:}")
	private String ouFilters;

	private ArrayList<Pair<String, UsersMachinesFilterEnum>> ouFiltersList;
	
	@Override
	public void afterPropertiesSet() throws Exception {

		if (!StringUtils.isEmpty(ouFilters)) {
			ouFiltersList = ParsingUsersMachinesFiltering.getFiltersList(ouFilters);
			for (Pair<String, UsersMachinesFilterEnum> filter : ouFiltersList) {
				if (!filter.getRight().equals(UsersMachinesFilterEnum.OU)) {
					throw new IllegalArgumentException("Machines filter only supports OUs");
				}
			}
		}
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
		}

		String dn = computer.getDistinguishedName();
		if (dn == null){
			return false;
		}
		for (Pair<String, UsersMachinesFilterEnum> filter : ouFiltersList) {
			if (dn.endsWith(filter.getLeft())) {
				return true;
			}
		}
		return false;
	}

	public boolean toFilter(String computerName) {
		if (ouFiltersList == null) {
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
		if (ouFiltersList == null) {
			return;
		}
		OUMachinesCache.invalidate(computerName);
	}
}
