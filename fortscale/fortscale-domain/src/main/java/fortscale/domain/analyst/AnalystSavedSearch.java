package fortscale.domain.analyst;

import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.Assert;

import com.mongodb.DBObject;

import fortscale.domain.core.AbstractAuditableDocument;


@Document
public class AnalystSavedSearch extends AbstractAuditableDocument{

	private String analystId;
	
	private String analystUsername;
	
	private String name;
	
	private String category;
	
	private String description;
	
	private DBObject filter;
	
	
	@PersistenceConstructor
	public AnalystSavedSearch(String analystId, String analystUsername, String name, String category, DBObject filter) {
		Assert.hasText(analystId);
		Assert.hasText(analystUsername);
		Assert.hasText(name);
		Assert.hasText(category);
		Assert.notNull(filter);

		this.analystId = analystId;
		this.analystUsername = analystUsername;
		this.name = name;
		this.category = category;
		this.filter = filter;
	}


	public String getAnalystId() {
		return analystId;
	}


	public void setAnalystId(String analystId) {
		this.analystId = analystId;
	}


	public String getAnalystUsername() {
		return analystUsername;
	}


	public void setAnalystUsername(String analystUsername) {
		this.analystUsername = analystUsername;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getCategory() {
		return category;
	}


	public void setCategory(String category) {
		this.category = category;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public DBObject getFilter() {
		return filter;
	}


	public void setFilter(DBObject filter) {
		this.filter = filter;
	}
	
	
	
}
