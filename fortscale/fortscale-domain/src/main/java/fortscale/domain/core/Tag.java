package fortscale.domain.core;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Document(collection=Tag.collectionName)
@CompoundIndexes({
		// index for making sure our evidence is unique
		@CompoundIndex(name="unique_tag_name", def = "{'" + Tag.nameField + "': 1}", unique = true),
		@CompoundIndex(name="unique_tag_display_name", def = "{'" + Tag.displayNameField + "': 1}", unique = true)
})
public class Tag extends AbstractDocument{

	public static final String collectionName = "tags";
	public static final String nameField = "name";
	public static final String displayNameField = "displayName";
	public static final String createsIndicatorField = "createsIndicator";
	public static final String rulesField = "rules";
	public static final String activeField = "active";

	public Tag() {}

	public Tag(String name) {
		this.name = name;
		displayName = name;
		createsIndicator = false;
		rules = new ArrayList<>();
		active = true;
	}

	public Tag(String name, String displayName) {
		this.name = name;
		this.displayName = displayName;
		createsIndicator = false;
		rules = new ArrayList<>();
		active = true;
	}

	public Tag(String name, String displayName, boolean setCreatesIndicator) {
		this.name = name;
		this.displayName = displayName;
		createsIndicator = setCreatesIndicator ? true : false;
		rules = new ArrayList<>();
		active = true;
	}

	@NotNull
	@NotEmpty
	private String name;

	@NotNull
	@NotEmpty
	private String displayName;

	@NotNull
	@NotEmpty
	private Boolean createsIndicator;

	@NotNull
	@NotEmpty
	private Boolean active;

	@NotNull
	@NotEmpty
	private List<String> rules;

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

	public List<String> getRules() {
		return rules;
	}

	public void setRules(List<String> rules) {
		this.rules = rules;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.name);
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
		return new EqualsBuilder().append(name, tag.name).isEquals();
	}

}