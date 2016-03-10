package fortscale.domain.core;
        
        import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
        import org.springframework.data.mongodb.core.index.Indexed;
        import org.springframework.data.mongodb.core.mapping.Document;
        import org.springframework.data.mongodb.core.mapping.Field;

        import java.util.*;

/**
* Represents single GeoHopping data in MongoDB
 * Might arrive from VPN_GEO_HOPPING or other GEO_HOPPING
*
*
* Date: 3/10/2016.
*/
    @Document(collection = GeoHopping.COLLECTION_NAME)
    @JsonIgnoreProperties(ignoreUnknown = false)
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
    private Long startDate;

    @Field(endDateField)
    private Long endDate;

    // Index for expiration (TTL): one year
    @Indexed(expireAfterSeconds = 31536000)
    @Field(retentionDateField)
    private Date retentionDate;


    @Field(normalizedUserNameField)
    private String normalizedUserName;


    @Field(locationsField)
    private Set<CountryCity> locations;

    //Constructor
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

    public Set<CountryCity> getLocations() {
    return locations;
}

    public void setLocations(Set<CountryCity> locations) {
    this.locations = locations;
}

    //Subclass which contain couple of country and city
    public static class CountryCity{
        private String country;
        private  String city;

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            CountryCity that = (CountryCity) o;

            if (country != null ? !country.equals(that.country) : that.country != null) return false;
            return !(city != null ? !city.equals(that.city) : that.city != null);

        }

        @Override
        public int hashCode() {
            int result = country != null ? country.hashCode() : 0;
            result = 31 * result + (city != null ? city.hashCode() : 0);
            return result;
        }
    }
}