package fortscale.web.fields;



public class EmailAddress extends NotEmptyString{	
	
	public EmailAddress(){}
	
	public EmailAddress(String value){
		super(value);
	}
	
	public String toString(){
		return value;
	}
}
