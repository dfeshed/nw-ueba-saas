package presidio.ui.presidiouiapp.fields.converter;

import org.springframework.core.convert.converter.Converter;
import presidio.ui.presidiouiapp.fields.NotEmptyString;


public class StringToNotEmptyStringConverter implements Converter<String, NotEmptyString> {

	@Override
	public NotEmptyString convert(String source) {
		return new NotEmptyString(source);
	}

}