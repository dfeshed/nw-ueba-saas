package fortscale.web.beans;

import java.util.Date;

import fortscale.domain.ad.UserMachine;

public class UserMachineBean {

	private UserMachine userMachine;
	
	public UserMachineBean(UserMachine userMachine){
		this.userMachine = userMachine;
	}
	
	public String getUsername(){
		return userMachine.getUsername();
	}
	
	public String getHostname(){
		return userMachine.getHostname();
	}
	
	public String getIpAddress(){
		return userMachine.getHostnameip();
	}
	
	public int getCount(){
		return userMachine.getLogoncount();
	}
	
	public Date getLastSignIn(){
		return userMachine.getLastlogon();
	}
}
