package fortscale.domain.analyst;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;



@Component
public class GrantedAuthorityToStringConverter implements Converter<GrantedAuthority, String> {

	@Override
	public String convert(GrantedAuthority source) {
		return source == null ? null : source.getAuthority();
	}

}
