package fortscale.web.fields.converter;

import org.springframework.core.convert.converter.Converter;

import fortscale.web.fields.EmailAddress;

public class StringToEmailAddressConverter implements Converter<String, EmailAddress> {

	@Override
	public EmailAddress convert(String source) {
		return new EmailAddress(source);
	}

}
