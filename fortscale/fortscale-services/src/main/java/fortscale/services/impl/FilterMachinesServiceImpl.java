package fortscale.services.impl;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class FilterMachinesServiceImpl implements FilterMachinesService, InitializingBean{
    LoadingCache<String, Boolean> OUMachinesCache;
	@Autowired
    private ComputerRepository computerRepository;
    @Value("${machines.ou.filter:}")
    private String ouName; 
	private static Logger logger = LoggerFactory
			.getLogger(FilterMachinesServiceImpl.class);
    
	@Override
	public void afterPropertiesSet() throws Exception {

        OUMachinesCache = CacheBuilder.newBuilder()
      	       .maximumSize(100000)
      	       .expireAfterWrite(1, TimeUnit.HOURS)
      	       .build(
      	           new CacheLoader<String, Boolean>() {
      	             public Boolean load(String key){
      	               return getIsBelongToOU(key);
      	             }
      	           });
	}
	
	private Boolean getIsBelongToOU(String computerName){
    	Computer computer = computerRepository.findByName(computerName);
        if(computer == null){
            return false;
        }else{
            String dn = computer.getDistinguishedName();
            if(dn != null && dn.endsWith(ouName)){
                return true;
            }else{
                return false;
            }
        }
    }
	public boolean toFilter(String computerName){
    	if(ouName == null || ouName.equals("")){
    		return false;
    	}
    	try {
			if (OUMachinesCache.get(computerName)){
				 return false;
			}else{
				return true;
			}
		} catch (ExecutionException e) {
			logger.error("Error in cache execution");
			return true;
		}
	}
}

