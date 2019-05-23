package presidio.ui.presidiouiapp.beans;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import fortscale.domain.core.Alert;
import fortscale.domain.core.Entity;
import fortscale.services.EntityServiceFacade;

import java.io.Serializable;
import java.util.List;

public class EntityDetailsBean implements Serializable{

	private static final long serialVersionUID = 1L;

    @JsonUnwrapped
	private Entity entity;


	private Entity manager;
	private List<Entity> directReports;
	private String thumbnailPhoto;
	private EntityServiceFacade entityServiceFacade;

	private List<Alert> alerts;

	public EntityDetailsBean(Entity entity, Entity manager, List<Entity> directReports, EntityServiceFacade entityServiceFacade){
		this.entity = entity;
		this.manager = manager;
		this.directReports = directReports;
		this.entityServiceFacade = entityServiceFacade;
	}

	public List<Alert> getAlerts() {
		return alerts;
	}

	public void setAlerts(List<Alert> alerts) {
		this.alerts = alerts;
	}

	public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

	public void setThumbnailPhoto(String thumbnailPhoto) {
		this.thumbnailPhoto = thumbnailPhoto;
	}

	public String getOu(){
	    return entityServiceFacade.getOu(entity);
	}

	public String getImage() {
		return thumbnailPhoto;
	}


}
