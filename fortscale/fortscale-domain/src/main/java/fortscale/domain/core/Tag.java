package fortscale.domain.core;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection=Tag.collectionName)
public class Tag extends AbstractDocument{

	public static final String collectionName =  "tags";

	private String name;

	private String displayName;

	private Boolean createsIndicator;

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public Boolean getCreatesIndicator() {
		return createsIndicator;
	}

	public void setCreatesIndicator(Boolean createsIndicator) {
		this.createsIndicator = createsIndicator;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}