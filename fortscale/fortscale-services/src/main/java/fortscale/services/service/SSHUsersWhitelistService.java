package fortscale.services.service;

import java.util.ArrayList;
import java.util.regex.Pattern;


public interface SSHUsersWhitelistService {
	public ArrayList<Pattern> getSshUsersRegList();
}
