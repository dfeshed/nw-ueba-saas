package fortscale.utils.servlet;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class HttpServletRequestWrapper extends javax.servlet.http.HttpServletRequestWrapper{

	public HttpServletRequestWrapper(HttpServletRequest request) {
		super(request);
	}
	
	@Override
	public String getRemoteUser() {
		String remoteUser = super.getRemoteUser();
		if(remoteUser == null){
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if(auth != null){
				Object principal = auth.getPrincipal();
				if(principal != null){
					remoteUser = principal.toString();
				}
			}
		}
		return remoteUser;
	}

	@Override
	public Principal getUserPrincipal() {
		Principal p = super.getUserPrincipal();
		if(p == null){
			p = SecurityContextHolder.getContext().getAuthentication();
		}
		return p;
	}
}
