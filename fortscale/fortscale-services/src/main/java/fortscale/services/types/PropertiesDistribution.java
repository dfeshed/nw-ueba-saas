package fortscale.services.types;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.api.client.repackaged.com.google.common.base.Objects;

import fortscale.utils.json.MapAsArraySerializer;


/**
 * Data transfer object structure to hold event or user property values distribution results
 */
@JsonAutoDetect(fieldVisibility=Visibility.ANY, getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
public class PropertiesDistribution {

	private String propertyName;
	private boolean conclusive = true;
	@JsonIgnore
	private int totalCount;
	@JsonSerialize(using=MapAsArraySerializer.class)
	private Map<String, PropertyEntry> entries = new HashMap<String, PropertyEntry>();
	
	@JsonCreator
	public PropertiesDistribution(@JsonProperty(value="propertyName") String propertyName) {
		this.propertyName = propertyName;
	}
	
	public void incValueCount(String propertyValue, int count) {
		if (StringUtils.isEmpty(propertyValue))
			return;

		// increment the property entry count
		PropertyEntry entry = entries.get(propertyValue);
		if (entry==null) {
			entry = new PropertyEntry(propertyValue);
			entries.put(propertyValue, entry);
		}
		
		entry.incrementCount(count);
		totalCount += count;
	}
	
	public String getPropertyName() {
		return propertyName;
	}

	public boolean isConclusive() {
		return conclusive;
	}
	
	public void setConclusive(boolean conclusive) {
		this.conclusive = conclusive;
	}
	
	public Collection<PropertyEntry> getPropertyValues() {
		return entries.values();
	}
	
	public int getNumberOfValues() {
		return entries.size();
	}
	
	public void calculateValuesDistribution() {
		for (PropertyEntry entry : entries.values()) {
			entry.setPercantage(  (float)entry.getCount() / totalCount );
		}
	}
	
	@JsonAutoDetect(fieldVisibility=Visibility.ANY, getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
	public class PropertyEntry {
		private String propertyValue;
		private int count;
		private float percantage;
		
		@JsonCreator
		public PropertyEntry(@JsonProperty(value="propertyValue") String propertyValue) {
			this.propertyValue = propertyValue;
			this.count = 0;
			this.percantage = 0.0f;
		}
		
		public String getPropertyValue() {
			return propertyValue;
		}
		
		public void setPropertyValue(String propertyValue) {
			this.propertyValue = propertyValue;
		}
		
		public int getCount() {
			return count;
		}
		
		public void incrementCount(int count) {
			this.count += count;
		}
		
		public float getPercantage() {
			return percantage;
		}
		
		public void setPercantage(float percantage) {
			this.percantage = percantage;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj==null)
				return false;
			
			if (getClass() != obj.getClass())
				return false;
			
			PropertyEntry other = (PropertyEntry)obj;
			return Objects.equal(propertyValue, other.propertyValue);
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(propertyValue);
		}
	}
}
