package fortscale.services.impl;

import fortscale.domain.core.User;

public class NewUser extends User{
	public NewUser(){
		super();
	}
	
	public NewUser(String id){
		setId(id);
	}
}