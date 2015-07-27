package fortscale.domain.core;

import java.util.List;

/**
 * Created by danal on 15/07/2015.
 */
public class EntityTags {

	private EntityType entityType;

	private String entityName;

	private List<String> tags;

	public EntityTags(EntityType entityType, String entityName, List<String> tags) {
		this.entityType = entityType;
		this.entityName = entityName;
		this.tags = tags;
	}

	public EntityType getEntityType() {
		return entityType;
	}

	public void setEntityType(EntityType entityType) {
		this.entityType = entityType;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	@Override public String toString() {
		return "EntityTags{" +
				"entityName='" + entityName + '\'' +
				", entityType=" + entityType +
				'}';
	}
}
