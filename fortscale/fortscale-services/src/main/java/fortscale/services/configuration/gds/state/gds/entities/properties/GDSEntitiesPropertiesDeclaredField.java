package fortscale.services.configuration.gds.state.gds.entities.properties;

/**
 * entities properties field declaration holds the data needed to in order to declare a field in the GDS entities
 * properties process.
 * every field of the entity should be declared in entities properties, even if it's not in use.
 *
 * Created by galiar on 20/01/2016.
 */
public class GDSEntitiesPropertiesDeclaredField {

	private String fieldName;
	private boolean isInUse;

	public GDSEntitiesPropertiesDeclaredField(String fieldName, boolean isInUse) {
		this.fieldName = fieldName;
		this.isInUse = isInUse;
	}

	public String getFieldName() {
		return fieldName;
	}

	public boolean isInUse() {
		return isInUse;
	}


}
