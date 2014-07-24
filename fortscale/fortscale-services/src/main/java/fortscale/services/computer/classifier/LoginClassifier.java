package fortscale.services.computer.classifier;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fortscale.domain.ad.UserMachine;
import fortscale.domain.ad.dao.UserMachineDAO;
import fortscale.domain.core.Computer;
import fortscale.domain.core.ComputerUsageClassifier;
import fortscale.domain.core.ComputerUsageType;
import fortscale.services.computer.EndpointClassifier;

/**
 * Classify machine according to login pattern. A machine that only one user log on to, and 
 * does this frequently enough is considered to be end-point. Servers are not classified by this
 * classifier
 */
@Component
public class LoginClassifier implements EndpointClassifier {

	public static final String CLASSIFIER_NAME = "LoginClassifier";
	
	@Autowired
	private UserMachineDAO dao;
	
	@Value("${endpoint.login.unknown.stale.threshold:7}")
	private int unkownStaleThreshold;
	
	@Value("${endpoint.login.desktop.stale.threshold:60}")
	private int desktopStaleThreshold;
	
	@Value("${endpoint.login.pattern.period:30}")
	private int loginPeriod;
	
	@Value("${endpoint.login.min.events.required:1000}")
	private int minimunEventsRequired;
	
	@Value("${enpoint.login.min.events.backoff.hours:10}")
	private int hoursToBackoff;
	
	private long lastTimeCheckedForMinimumEvents;
	private boolean hasMinimumEvents = true;
	
	@Override
	public boolean canClassify(Computer computer) {
		return computer!=null && StringUtils.isNotEmpty(computer.getName()) && haveMinimumEvents();
	}

	/**
	 * Checks if the table contains the minimum number of events to operate.
	 * If not, we will not check this again for some time. 
	 */
	private boolean haveMinimumEvents() {
		// check if the last time checked for minimum events was recently
		if (minimunEventsRequired!=0 && lastTimeCheckedForMinimumEvents < (System.currentTimeMillis() - (hoursToBackoff*1000*60*60))) {
			// get the events count in the table and check if it passes the min required
			int eventsCount = dao.count(loginPeriod);
			hasMinimumEvents = (eventsCount > minimunEventsRequired);
			lastTimeCheckedForMinimumEvents = System.currentTimeMillis();
		}
		
		return hasMinimumEvents;
	}
	
	public void setMinimumEventsRequired(int minRequired) {
		this.minimunEventsRequired = minRequired;
	}
	
	@Override
	public boolean classify(Computer computer) {
		if (!canClassify(computer))
			return false;
		
		// skip classification in case other classifier already decided on classification
		if (hasOtherClassifications(computer))
			return false;
				
		// check if we already have a classification that is not stale
		Date desktopThresholdDate = new Date( (new Date()).getTime() - desktopStaleThreshold*1000*60*60*24 ); 
		Date unknownThresholdDate = new Date( (new Date()).getTime() - unkownStaleThreshold*1000*60*60*24 );
		ComputerUsageClassifier classification = computer.getUsageClassifier(CLASSIFIER_NAME);
		if (classification!=null) {
			if ((classification.getUsageType()==ComputerUsageType.Desktop && classification.getWhenComputed().after(desktopThresholdDate)) ||
				(classification.getUsageType()==ComputerUsageType.Unknown && classification.getWhenComputed().after(unknownThresholdDate))) {
				// no computation is required, classification is not stale
				return false;
			}
		}
			
		
		// if classification is required, look up login table for all logins to 
		// this computer in the last month and get the login count for each user to it
		// and check that the user with a most logins logs in to this machine mostly
		List<UserMachine> loginsToMachine = dao.findByHostname(computer.getName(), loginPeriod);
		UserMachine dominatingUser = getDominatingLogin(loginsToMachine);
		if (dominatingUser!=null) {
			// a possible dominating user found, verify that he log on only to 
			// this computer the most times
			List<UserMachine> loginsByUser = dao.findByUsername(dominatingUser.getUsername());
			UserMachine dominatingComputer = getDominatingLogin(loginsByUser);
			
			if (dominatingComputer!=null && dominatingComputer.getHostname().equalsIgnoreCase(computer.getName())) {
				// found out a user how log on to this machine the most times and only to this
				computer.putUsageClassifier(new ComputerUsageClassifier(CLASSIFIER_NAME, ComputerUsageType.Desktop, new Date()));
				return true;
			}
		}
		// no dominating logins found, mark it as unknown so we won't compute it for the next 
		// stale period. We put the current date as the classifier date 
		computer.putUsageClassifier(new ComputerUsageClassifier(CLASSIFIER_NAME, ComputerUsageType.Unknown, new Date()));
		return true;
	}

	
	private boolean hasOtherClassifications(Computer computer) {
		for (ComputerUsageClassifier classification : computer.getUsageClassifiers()) {
			if (!classification.getClassifierName().equals(CLASSIFIER_NAME)) {
				if (classification.getUsageType()!=null && !ComputerUsageType.Unknown.equals(classification.getUsageType()))
					return true;
			}
		}
		return false;
	}
	
	private UserMachine getDominatingLogin(List<UserMachine> logins) {
		
		int topMostLoginsCount = 0;
		int secondLoginsCount = 0;
		UserMachine topMostLogin = null;
		
		for (UserMachine login : logins) {
			int count = login.getLogonCount();
			if (count>topMostLoginsCount) {
				secondLoginsCount = topMostLoginsCount;
				topMostLoginsCount = count;
				topMostLogin = login;
			} else {
				if (count>secondLoginsCount)
					secondLoginsCount = count;
			}
		}
		
		// check if the top most logins count is more than 10 from the second most
		// for every 30 days of time period taken
		int dominationThreshold = 10 * loginPeriod / 30;
		if (topMostLoginsCount > secondLoginsCount+dominationThreshold)
			return topMostLogin;
		else
			return null;
		
	}
	
	
	public void setUnknownStaleThreshold(int unkownStaleThreshold) {
		this.unkownStaleThreshold = unkownStaleThreshold;
	}
	public void setDesktopStaleThreshold(int desktopStaleThreshold) {
		this.desktopStaleThreshold = desktopStaleThreshold;
	}
	public void setLoginPeriod(int loginPeriod) {
		this.loginPeriod = loginPeriod;
	}
}
