package fortscale.web.fields.converter;

import org.springframework.core.convert.converter.Converter;

import fortscale.web.fields.NotEmptyString;

public class StringToNotEmptyStringConverter implements Converter<String, NotEmptyString> {

	@Override
	public NotEmptyString convert(String source) {
		return new NotEmptyString(source);
	}

}