package fortscale.collection.jobs.gds.input.populators.enrichment;

import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.EntityType;
import fortscale.services.configuration.gds.state.GDSCompositeConfigurationState;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by galiar on 17/01/2016.
 * creates entities.properties config's block.
 * according to the definition the user defined in the schema definition step, the populator create the adjusted entities
 * properties - declare them for the new data entity.
 * if the user has declared additional fields (not part of base entity), then she will configure them here properly, with
 * all the configurations that the UI need.
 */
public class GDSEntitiesPropertiesCLIPopulator implements GDSConfigurationPopulator {


	private static final String EXTENDS = "extends";

	private static final String GDS_CONFIG_ENTRY = "gds.config.entry.";

	@Override public Map<String, Map<String, ConfigurationParam>> populateConfigurationData(
			GDSCompositeConfigurationState currentConfigurationState) throws Exception {

		Map<String, Map<String, ConfigurationParam>> configurationsMap = new HashMap<>();
		HashMap<String, ConfigurationParam> paramsMap = new HashMap<>();

		configurationsMap.put(GDS_CONFIG_ENTRY, paramsMap);

		//get the fieldsMetaData from state/argument


		//get the base entity
		String baseEntity = currentConfigurationState.getEntityType().getEntityName();


		//fill the paramMap with all the relevant fields
		paramsMap.put(EXTENDS , new ConfigurationParam(EXTENDS,false,baseEntity));


		//for field in fields meta data fields:
		//is the field in use?
		//configure new stuff if this is additional field


		return configurationsMap;
	}
}
