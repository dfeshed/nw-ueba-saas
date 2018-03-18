package fortscale.web.fields;

import org.hibernate.validator.constraints.NotEmpty;

public class NotEmptyString {
	@NotEmpty
	protected String value;
	public NotEmptyString(){}
	
	
	public NotEmptyString(String value){
		this.value = value;
	}
	
	public String toString(){
		return value;
	}


	public String getValue() {
		return value;
	}


	public void setValue(String value) {
		this.value = value;
	}
	
	
	
}
