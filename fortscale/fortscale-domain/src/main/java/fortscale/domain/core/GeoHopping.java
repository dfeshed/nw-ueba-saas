package fortscale.domain.core;
        
        import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
        import com.fasterxml.jackson.annotation.JsonInclude;
        import org.springframework.data.annotation.Transient;
        import org.springframework.data.mongodb.core.index.CompoundIndex;
        import org.springframework.data.mongodb.core.index.CompoundIndexes;
        import org.springframework.data.mongodb.core.index.Indexed;
        import org.springframework.data.mongodb.core.mapping.Document;
        import org.springframework.data.mongodb.core.mapping.Field;
        
        import java.util.Date;
        import java.util.List;
        import java.util.Map;
        import java.util.UUID;
        
        /**
  * Represents single evidence in MongoDB
  *
  * More information: https://fortscale.atlassian.net/wiki/display/FSC/Evidence+Collection+in+MongoDB
  *
  * Date: 6/22/2015.
  */
        //@Document(collection = GeoHopping.COLLECTION_NAME)
        //@CompoundIndexes({
        //		// index for getting all evidences for specific user
        //	@CompoundIndex(name="entity_idx", def = "{'" + GeoHopping.entityNameField + "': 1, '" + GeoHopping.entityTypeField +"': 1}", unique = false),
        //		// index for making sure our evidence is unique
        //	@CompoundIndex(name="new_unique_evidence", def = "{'" + GeoHopping.startDateField + "': 1, '" + GeoHopping.endDateField +"': 1, '" + GeoHopping.entityTypeField +"': 1, '" + GeoHopping.entityNameField +"': 1, '" + GeoHopping.anomalyTypeFieldNameField +"': 1, '"+ GeoHopping.anomalyValueField +"': 1}", unique = true)
        //})
        //@JsonIgnoreProperties(ignoreUnknown = true)
        public class GeoHopping extends AbstractDocument{
    
            	/**
     	 * Collection Name
     	 */
            	public static final String COLLECTION_NAME = "geoHopping";
    
            
            	// Document's Field Names
            
            
            	// Entity information
            	public static final String startDateField = "startDate";
    	public static final String endDateField = "endDate";
    	public static final String retentionDateField = "retentionDate";
    
            	public static final String normalizedUserNameField = "normalizedUserName";
    	public static final String locationsField = "locations";
    
            
            
            	// Document's Fields
            	@Field(startDateField)
    	public Long startDate;
    
            
            	@Field(endDateField)
    	public Long endDate;
    
            	// Index for expiration (TTL): one year
            	@Indexed(expireAfterSeconds = 31536000)
    	@Field(retentionDateField)
    	public Date retentionDate;
    
            	@Indexed()
    	@Field(normalizedUserNameField)
    	public String normalizedUserName;
    
            	@Indexed()
    	@Field(locationsField)
    	public String locations;
    
            
            	public GeoHopping() {
        	}
    
            	public Long getStartDate() {
        		return startDate;
        	}
    
            	public void setStartDate(Long startDate) {
        		this.startDate = startDate;
        	}
    
            	public Long getEndDate() {
        		return endDate;
        	}
    
            	public void setEndDate(Long endDate) {
        		this.endDate = endDate;
        	}
    
            	public Date getRetentionDate() {
        		return retentionDate;
        	}
    
            	public void setRetentionDate(Date retentionDate) {
        		this.retentionDate = retentionDate;
        	}
    
            	public String getNormalizedUserName() {
        		return normalizedUserName;
        	}
    
            	public void setNormalizedUserName(String normalizedUserName) {
        		this.normalizedUserName = normalizedUserName;
        	}
    
            	public String getLocations() {
        		return locations;
        	}
    
            	public void setLocations(String locations) {
        		this.locations = locations;
        	}
    }