package fortscale.services.impl;

import fortscale.domain.core.User;

public class NewUser extends User{
	private static final long serialVersionUID = 1L;

	public NewUser(){
		super();
	}
	
	public NewUser(String id){
		setId(id);
	}
}