package fortscale.services.configuration.gds.state;

import fortscale.services.configuration.gds.state.gds.entities.properties.GDSEntitiesPropertiesAdditionalField;
import fortscale.services.configuration.gds.state.gds.entities.properties.GDSEntitiesPropertiesDeclaredField;
import fortscale.services.configuration.gds.state.gds.entities.properties.GDSEntitiesPropertiesTable;

import java.util.ArrayList;
import java.util.List;

/**
 * holds the state given by the user in the populator.
 * the state is kept in memory until the user apply (writes to file) or reset.
 * Created by galiar on 19/01/2016.
 */
public class GDSEntitiesPropertiesState extends GDSStreamingTaskState {


	private GDSEntitiesPropertiesTable tableConfigs;

	private List<GDSEntitiesPropertiesDeclaredField> declaredFields = new ArrayList<>();

	private List<GDSEntitiesPropertiesAdditionalField> additionalFields = new ArrayList<>();


	public List<GDSEntitiesPropertiesDeclaredField> getDeclaredFields() {
		return declaredFields;
	}

	public List<GDSEntitiesPropertiesAdditionalField> getAdditionalFields() {
		return additionalFields;
	}

	public void addDeclaredField(GDSEntitiesPropertiesDeclaredField fieldDeclaration){
		declaredFields.add(fieldDeclaration);
	}

	public void addAdditionalField(GDSEntitiesPropertiesAdditionalField additionalField){
		additionalFields.add(additionalField);
	}
	public GDSEntitiesPropertiesTable getTableConfigs() {
		return tableConfigs;
	}

	public void setTableConfigs(GDSEntitiesPropertiesTable tableConfigs) {
		this.tableConfigs = tableConfigs;
	}

}

