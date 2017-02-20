package fortscale.domain.core;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.hibernate.validator.constraints.NotBlank;
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
	public static final String deletedField = "deleted";
	public static final String isAssignableField = "isAssignable";
	public static final String isPredefinedField = "isPredefined";

	public static final String ADMIN_TAG = "admin";
	public static final String EXECUTIVE_TAG = "executive";
	public static final String SERVICE_ACCOUNT_TAG = "service";

	public Tag() {}

	public Tag(String name) {
		this.name = name;
		displayName = name;
		createsIndicator = false;
		rules = new ArrayList<>();
		deleted = false;
		isAssignable = true;
		isPredefined = false;
	}

	public Tag(String name, String displayName, boolean setCreatesIndicator, boolean isAssignable, boolean isPredefined) {
		this.name = name;
		this.displayName = displayName;
		this.isAssignable = isAssignable;
		createsIndicator = setCreatesIndicator;
		rules = new ArrayList<>();
		deleted = false;
		this.isPredefined = isPredefined;
	}

	@NotBlank
	private String name;

	@NotBlank
	private String displayName;

	@NotNull
	private Boolean createsIndicator;

	@NotNull
	private Boolean deleted;

	@NotNull
	private Boolean isAssignable;

	private boolean isPredefined;

	@NotNull
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

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public Boolean getIsAssignable() {
		return isAssignable;
	}

	public void setIsAssignable(Boolean isAssignable) {
		this.isAssignable = isAssignable;
	}

	public boolean isPredefined() {
		return isPredefined;
	}

	public void setPredefined(boolean predefined) {
		isPredefined = predefined;
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