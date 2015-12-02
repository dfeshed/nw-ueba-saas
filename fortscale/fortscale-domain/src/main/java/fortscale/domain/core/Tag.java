package fortscale.domain.core;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection=Tag.collectionName)
@CompoundIndexes({
		// index for making sure our evidence is unique
		@CompoundIndex(name="unique_tag", def = "{'" + Tag.nameField + "': 1}", unique = true)
})
public class Tag extends AbstractDocument{

	public static final String collectionName = "tags";
	public static final String nameField = "name";
	public static final String displayNameField = "displayName";
	public static final String createsIndicatorField = "createsIndicator";

	public Tag() {}

	public Tag(String name) {
		this.name = name;
		displayName = name;
		createsIndicator = false;
	}

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