package fortscale.collection.jobs;

import org.junit.Assert;
import org.junit.Test;
import org.kitesdk.morphline.api.Record;

import fortscale.collection.morphlines.RecordToBeanItemConverter;
import fortscale.domain.ad.AdUser;

public class RecordToAdUserItemsConverterTest {

	@Test
	public void simpleConvertion() throws InstantiationException, IllegalAccessException{
		Record record = new Record();
		String runtimeField = "runtime";
		String timestampValue = "23/3/2014";
		record.put(runtimeField, timestampValue);
		String sAMAccountTypeField = "sAMAccountType";
		Long sAMAccountTypeValue = 22L;
		record.put(sAMAccountTypeField, sAMAccountTypeValue);
		String timestampepochField = "timestampepoch";
		Long timestampepochValue = 1389632400000L;
		record.put(timestampepochField, timestampepochValue);
		String userAccountControlField = "userAccountControl";
		Integer userAccountControlValue = 5;
		record.put(userAccountControlField, userAccountControlValue);
		
		RecordToBeanItemConverter<AdUser> converter = new RecordToBeanItemConverter<AdUser>(runtimeField, sAMAccountTypeField, userAccountControlField, timestampepochField);
		converter.initMetricsClass(null,"TEST-NOT-USED");
		AdUser adUser = new AdUser();
		converter.convert(record, adUser);
		
		Assert.assertEquals(timestampValue, adUser.getRuntime());
		Assert.assertEquals(sAMAccountTypeValue, adUser.getsAMAccountType());
		Assert.assertEquals(userAccountControlValue, adUser.getUserAccountControl());
		Assert.assertEquals(timestampepochValue, adUser.getTimestampepoch());
	}
}
