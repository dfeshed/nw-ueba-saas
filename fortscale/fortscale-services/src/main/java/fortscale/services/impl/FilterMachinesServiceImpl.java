package fortscale.services.impl;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	@Value("${machines.ou.filters:}")
	private String ouFilters;

	private ArrayList<String> ouFiltersList;
	@Override
	public void afterPropertiesSet() throws Exception {
		
		ouFiltersList = parseOUfilters();
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
		for (String ouFilter : ouFiltersList) {
			if (dn != null && dn.endsWith(ouFilter)) {
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
	
	private ArrayList<String> parseOUfilters(){
		if (StringUtils.isEmpty(ouFilters)){
			return null;
		}
		if(!ouFilters.startsWith("[") || !ouFilters.endsWith("]")){
			throw new IllegalArgumentException("machines OU filter list must be enclosed with []");
		}
		String filtersStr = ouFilters.substring(1, ouFilters.length()-1);
		String[] filtersList = filtersStr.split("\\s*;\\s*");
		ArrayList<String> ouList = new ArrayList<String>();
		for(String filter : filtersList){
			String regex = "\\s*\"(.*)\"\\s*";
			Pattern pattern = Pattern.compile(regex);
			Matcher m = pattern.matcher(filter);
			if(m.matches() == false){
				throw new IllegalArgumentException("Bad machines OU filter format");
			}
			String filterName = m.group(1);
			ouList.add(filterName);
		}
		return ouList;
	}

}
