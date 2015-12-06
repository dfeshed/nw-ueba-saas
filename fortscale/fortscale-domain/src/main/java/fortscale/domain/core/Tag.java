package fortscale.domain.core;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
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
	public static final String isFixedField = "isFixed";

	public Tag() {}

	public Tag(String name) {
		this.name = name;
		displayName = name;
		createsIndicator = false;
		isFixed = false;
	}

	public Tag(String name, String displayName, Boolean isFixed) {
		this.name = name;
		this.displayName = displayName;
		this.isFixed = isFixed;
		createsIndicator = false;
	}

	public Tag(String name, String displayName, Boolean createsIndicator, Boolean isFixed) {
		this.name = name;
		this.displayName = displayName;
		this.createsIndicator = createsIndicator;
		this.isFixed = isFixed;
	}

	private String name;

	private String displayName;

	private Boolean createsIndicator;

	private Boolean isFixed;

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

	public Boolean getIsFixed() {
		return isFixed;
	}

	public void setIsFixed(Boolean isFixed) {
		this.isFixed = isFixed;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).
				append(name).
				toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Tag)) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		Tag tag = (Tag)obj;
		return new EqualsBuilder().
				append(name, tag.name).
				isEquals();
	}

}