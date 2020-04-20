package fortscale.domain.analyst;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;




@Component
public class StringToGrantedAuthorityConverter implements Converter<String, GrantedAuthority>{

	@Override
	public GrantedAuthority convert(String source) {
		return StringUtils.hasText(source) ? new SimpleGrantedAuthority((String)source) : null;
	}

}
