package fortscale.domain.fetch;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * Created by Amir Keren on 8/15/16.
 */
public class LogRepository {

	public static final String LOG_REPOSITORY_KEY = "system.logRepository.settings";
	public static final String DEFAULT_SIEM = SIEMType.SPLUNK.name().toLowerCase();

	@NotBlank
	protected String type;

	@NotBlank
	protected String host;

	@NotBlank
	protected String user;

	@NotBlank
	protected String password;

	@Min(1)
	@Max(65535)
	protected int port;

	public LogRepository() {}

	public LogRepository(String type, String host, String user, String password, int port) {
		this.type = type;
		this.host = host;
		this.user = user;
		this.password = password;
		this.port = port;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}