package fortscale.utils;

import org.junit.Assert;
import org.junit.Test;


public class ConversionUtilsTest {

	@Test
	public void asLong_should_convert_string() {
		Long actual = ConversionUtils.convertToLong("1000");
		Assert.assertEquals((Long)1000L, actual);		
	}
	
	@Test
	public void asLong_should_convert_int() {	
		Long actual = ConversionUtils.convertToLong((Integer)1000);
		Assert.assertEquals((Long)1000L, actual);		
	}
	
	@Test
	public void asLong_should_convert_long() {	
		Long actual = ConversionUtils.convertToLong((Long)1000L);
		Assert.assertEquals((Long)1000L, actual);		
	}
	
	@Test
	public void asLong_should_convert_long_string() {	
		Long actual = ConversionUtils.convertToLong("5123123123");
		Assert.assertEquals((Long)5123123123L, actual);		
	}
	
	
	
}
