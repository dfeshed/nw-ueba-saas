package fortscale.entity.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EntityEventConf implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	private Integer daysToRetainDocument;
	private List<String> contextFields;
	private Map<String, List<String>> aggregatedFeatureEventNamesMap;
	private Set<String> allAggregatedFeatureEventNames;
	private JSONObject entityEventFunction;

	@JsonCreator
	public EntityEventConf(
			@JsonProperty("name") String name,
			@JsonProperty("contextFields") List<String> contextFields,
			@JsonProperty("aggregatedFeatureEventNamesMap") Map<String, List<String>> aggregatedFeatureEventNamesMap,
			@JsonProperty("entityEventFunction") JSONObject entityEventFunction) {

		// Validate name
		Assert.isTrue(StringUtils.isNotBlank(name));

		// Validate contextFields
		Assert.notEmpty(contextFields);
		for (String contextField : contextFields) {
			Assert.isTrue(StringUtils.isNotBlank(contextField));
		}

		// Validate aggregatedFeatureEventNamesMap and create allAggregatedFeatureEventNames
		Assert.notEmpty(aggregatedFeatureEventNamesMap);
		allAggregatedFeatureEventNames = new HashSet<>();
		for (Map.Entry<String, List<String>> entry : aggregatedFeatureEventNamesMap.entrySet()) {
			Assert.isTrue(StringUtils.isNotBlank(entry.getKey()));
			Assert.notEmpty(entry.getValue());
			for (String aggregatedFeatureEventName : entry.getValue()) {
				Assert.isTrue(StringUtils.isNotBlank(aggregatedFeatureEventName));
				allAggregatedFeatureEventNames.add(aggregatedFeatureEventName);
			}
		}

		// Validate entityEventFunction
		Assert.notEmpty(entityEventFunction);

		this.name = name;
		this.daysToRetainDocument = null;
		this.contextFields = contextFields;
		this.aggregatedFeatureEventNamesMap = aggregatedFeatureEventNamesMap;
		this.entityEventFunction = entityEventFunction;
	}

	public String getName() {
		return name;
	}

	public Integer getDaysToRetainDocument() {
		return daysToRetainDocument;
	}

	public List<String> getContextFields() {
		return contextFields;
	}

	public Map<String, List<String>> getAggregatedFeatureEventNamesMap() {
		return aggregatedFeatureEventNamesMap;
	}

	public Set<String> getAllAggregatedFeatureEventNames() {
		return allAggregatedFeatureEventNames;
	}

	public JSONObject getEntityEventFunction() {
		return entityEventFunction;
	}
}
