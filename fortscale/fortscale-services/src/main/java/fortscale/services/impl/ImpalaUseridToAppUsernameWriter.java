package fortscale.services.impl;

import java.io.File;
import java.util.Date;
import java.util.List;

import fortscale.domain.core.ApplicationUserDetails;
import fortscale.domain.core.User;

public class ImpalaUseridToAppUsernameWriter extends ImpalaWriter{
	
	public ImpalaUseridToAppUsernameWriter(File file){
		super(file);
	}
		
	public void write(List<User> users, Date timestamp){
		for(User user: users){
			write(user,timestamp);
		}
	}
	
	private void write(User user, Date timestamp){
		for(ApplicationUserDetails applicationUserDetails: user.getApplicationUserDetails().values()){
			writeApplicationUserDetails(user, applicationUserDetails, timestamp);
		}
	}
	
	
	private void writeApplicationUserDetails(User user, ApplicationUserDetails applicationUserDetails, Date timestamp){
		String csvLineString = String.format("%s|%s|%s|%s|%s|%s", getRuntime(timestamp), applicationUserDetails.getApplicationName(), applicationUserDetails.getUserName(),
				user.getId(), user.getUsername(), user.getAdDn());
		write(csvLineString);
		newLine();
	}
}
