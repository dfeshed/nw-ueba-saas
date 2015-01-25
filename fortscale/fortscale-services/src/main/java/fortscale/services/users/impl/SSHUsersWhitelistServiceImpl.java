
package fortscale.services.users.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import fortscale.services.users.SSHUsersWhitelistService;

@Service("sshUsersWhitelist")
public class SSHUsersWhitelistServiceImpl implements SSHUsersWhitelistService, InitializingBean {

	private static Logger logger = LoggerFactory.getLogger(SSHUsersWhitelistServiceImpl.class);

	@Value("${user.ssh.list:}")
	private String sshUsersFile;
	private ArrayList<Pattern> sshUsersRegList;

	@Override
	public void afterPropertiesSet()
		throws Exception {

		if (!StringUtils.isEmpty(sshUsersFile)) {
			File f = new File(sshUsersFile);
			if (f.exists() && f.isFile()) {
				sshUsersRegList = new ArrayList<Pattern>();
				ArrayList<String> usersRegex = new ArrayList<String>(FileUtils.readLines(f));
				for (String regex : usersRegex) {
					sshUsersRegList.add(Pattern.compile(regex));
				}
			}
			else {
				logger.warn("Ssh users file not found in path: {}", sshUsersFile);
			}
		}
	}

	
	public ArrayList<Pattern> getSshUsersRegList() {
	
		return sshUsersRegList;
	}
	
}
