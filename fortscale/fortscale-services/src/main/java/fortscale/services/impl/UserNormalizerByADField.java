
package fortscale.services.impl;

		import org.apache.commons.lang3.StringUtils;
		import parquet.org.slf4j.Logger;
		import parquet.org.slf4j.LoggerFactory;

/**
 * Created by idanp on 1/30/2016.
 */
public class UserNormalizerByADField extends UsernameNormalizer {

	private static Logger logger = LoggerFactory.getLogger(UserNormalizerByADField.class);



	private String aDFieldName;
	private boolean partOrFullFlag;
	private boolean chainToDomainFlag;
	private String fieldValueRgexpWraper;



	@Override
	public String normalize(String aDFieldValue, String fakeDomain, String classifier, boolean updateOnly)
	{
		String ret;
		String fieldRegexpReplacer;
		logger.debug("Normalizing field - {}", aDFieldName);
		logger.debug("Normalizing user - {}", aDFieldValue);


		//get the username by his DN
		if (aDFieldName.equals("DN"))
			ret = usernameService.getUserNameByDn(aDFieldValue);

			//get the username based on user collection field
		else {


			//if a regexp wrapper was defined wrap the field value woth that regexp
			if(!StringUtils.isEmpty(fieldValueRgexpWraper))
			{
				fieldRegexpReplacer = fieldValueRgexpWraper.replace("#VALUE#",aDFieldValue);
			}
			//else keep the filed value as is
			else
				fieldRegexpReplacer = aDFieldValue;

			ret = usernameService.getUserNameByADField(aDFieldName, fieldRegexpReplacer, partOrFullFlag);
		}

		if (ret != null )
			logger.debug("user found - {}", ret);


		else {
			logger.debug("No users found or more than one found");
			//in case that we want post normalization of chaining the domain do it
			if(chainToDomainFlag)
			{
				ret = postNormalize(aDFieldValue,fakeDomain,classifier,updateOnly);
			}

		}

		return ret;

	}


	public boolean isPartOrFullFlag() {
		return partOrFullFlag;
	}

	public void setPartOrFullFlag(boolean partOrFullFlag) {
		this.partOrFullFlag = partOrFullFlag;
	}


	public boolean isChainToDomainFlag() {
		return chainToDomainFlag;
	}

	public void setChainToDomainFlag(boolean chainToDomainFlag) {
		this.chainToDomainFlag = chainToDomainFlag;
	}


	public String getFieldValueRgexpWraper() {
		return fieldValueRgexpWraper;
	}

	public void setFieldValueRgexpWraper(String fieldValueRgexpWraper) {
		this.fieldValueRgexpWraper = fieldValueRgexpWraper;
	}

	public String getaDFieldName() {
		return aDFieldName;
	}

	public void setaDFieldName(String aDFieldName) {
		this.aDFieldName = aDFieldName;
	}
}
