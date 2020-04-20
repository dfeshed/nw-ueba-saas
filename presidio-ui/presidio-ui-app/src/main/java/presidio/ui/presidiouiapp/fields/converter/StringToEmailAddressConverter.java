package presidio.ui.presidiouiapp.fields.converter;

import org.springframework.core.convert.converter.Converter;
import presidio.ui.presidiouiapp.fields.EmailAddress;

public class StringToEmailAddressConverter implements Converter<String, EmailAddress> {

	@Override
	public EmailAddress convert(String source) {
		return new EmailAddress(source);
	}

}
