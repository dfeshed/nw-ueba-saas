package presidio.ui.presidiouiapp.beans;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import fortscale.domain.core.Alert;
import fortscale.domain.core.User;
import fortscale.services.UserServiceFacade;
import fortscale.utils.logging.Logger;

import java.io.Serializable;
import java.util.List;

public class UserDetailsBean implements Serializable{

	private static final long serialVersionUID = 1L;

    @JsonUnwrapped
	private User user;


	private User manager;
	private List<User> directReports;
	private String thumbnailPhoto;
	private UserServiceFacade userServiceFacade;

	private List<Alert> alerts;

	public UserDetailsBean(User user, User manager, List<User> directReports, UserServiceFacade userServiceFacade){
		this.user = user;
		this.manager = manager;
		this.directReports = directReports;
		this.userServiceFacade = userServiceFacade;
	}

	public List<Alert> getAlerts() {
		return alerts;
	}

	public void setAlerts(List<Alert> alerts) {
		this.alerts = alerts;
	}

	public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

	public void setThumbnailPhoto(String thumbnailPhoto) {
		this.thumbnailPhoto = thumbnailPhoto;
	}

	public String getOu(){
	    return userServiceFacade.getOu(user);
	}

	public String getImage() {
		return thumbnailPhoto;
	}


}
