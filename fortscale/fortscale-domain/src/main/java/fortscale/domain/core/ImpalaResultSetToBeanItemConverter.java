package fortscale.domain.core;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;

import fortscale.utils.impala.ImpalaParser;

public class ImpalaResultSetToBeanItemConverter<T extends Object> {	
	private PropertyDescriptor propertyDescriptors[];
	private ImpalaParser impalaParser = new ImpalaParser();
	
	public ImpalaResultSetToBeanItemConverter(T bean) throws IllegalArgumentException {
		this.propertyDescriptors = PropertyUtils.getPropertyDescriptors(bean.getClass());
	}
	
	public void convert(ResultSet resultSet, T bean) throws ParseException, SQLException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if (resultSet==null){ 
			return;
		}
				
		for(PropertyDescriptor propertyDescriptor: propertyDescriptors){
			String fieldName = propertyDescriptor.getName();
			if(fieldName.equals("class")){
				continue;
			}
			if(propertyDescriptor.getPropertyType().equals(Date.class)){
				Date date = impalaParser.parseTimeDate(resultSet.getString(fieldName));
				
				BeanUtils.setProperty(bean, fieldName, date);
			} else{
				BeanUtils.setProperty(bean, fieldName, resultSet.getObject(fieldName));
			}
		}
	}
}
