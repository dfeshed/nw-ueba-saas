package fortscale.collection.usersfiltering.service;

import java.util.ArrayList;
import java.util.regex.Pattern;


public interface SSHUsersWhitelistService {
	public ArrayList<Pattern> getSshUsersRegList();
}
