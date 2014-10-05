package fortscale.services.ipresolving;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

/**
 * Ip resolving to hostname that is based on a file mapping from ip to hostnames
 */
@Service("fileResolver")
public class StaticFileBasedMappingResolver implements ResourceLoaderAware  {

	private static Logger logger = LoggerFactory.getLogger(StaticFileBasedMappingResolver.class);
	
	private Map<String,String> resolvingMap = new HashMap<String,String>();
	
	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		Resource mappingResource = resourceLoader.getResource("file:resources/ip-resolving.properties");
		if (mappingResource.exists()) {
			try {
				File mappingFile = mappingResource.getFile();
				if (mappingFile.exists() && mappingFile.canRead()) {
					// go over each line that should be in the form of "<ip> <hostname>" and put it into the map
					for (String line : FileUtils.readLines(mappingFile)) {
						String[] lineParts = line.split(" ");
						if (lineParts.length!=2) {
							logger.warn("static ip resolving map line is not in correct format: {}", line);
							continue;
						}
						String ip = lineParts[0];
						String hostname = lineParts[1];						
						resolvingMap.put(ip, hostname);
					}
				} else {
					logger.warn("mapping file {} is not readable", mappingFile.getAbsolutePath());
				}
				
			} catch (Exception e) {
				logger.error("error parsing static ip resolving map file", e);
			}
		}
	}
	
	public String getHostname(String ip) {
		return resolvingMap.get(ip);
	}
}
