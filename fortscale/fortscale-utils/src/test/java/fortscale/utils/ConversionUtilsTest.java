package fortscale.utils;


import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;


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

	@Test
	public void convert_string_to_list_based_on_comma_delimiter(){

		String strList = "a,b,c,d";
		String delimiter =",";

		List<String> list = new ArrayList<>();
		list.add("a");
		list.add("b");
		list.add("c");
		list.add("d");

		List<String> excpectedList = ConversionUtils.convertStringToList(strList,delimiter);
		assertTrue("Expected 'list' and 'expected list' to be equal." +
						"\n  'list'        = " + list +
						"\n  'expected list' = " + excpectedList,
				excpectedList.equals(list));


	}

	@Test
	public void convert_string_to_list_based_on_comma_delimiter_empty_string(){

		String strList = "";
		String delimiter =",";



		List<String> list = new ArrayList<>();
		list.add("");

		List<String> excpectedList = ConversionUtils.convertStringToList(strList,delimiter);

		assertTrue("Expected 'list' and 'expected list' to be equal." +
						"\n  'list'        = " + list +
						"\n  'expected list' = " + excpectedList,
				excpectedList.equals(list));


	}
	
	
	
}
