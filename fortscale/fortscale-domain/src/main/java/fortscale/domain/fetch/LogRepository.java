package fortscale.domain.fetch;

import org.hibernate.validator.constraints.NotBlank;

/**
 * Created by Amir Keren on 8/15/16.
 */
public class LogRepository {

	public static final String LOG_REPOSITORY_KEY = "system.logRepository.settings";

	@NotBlank
	protected String id;

	@NotBlank
	protected String type;

	@NotBlank
	protected String host;

	protected String user;

	@NotBlank
	protected String password;

	protected Integer port;

	public LogRepository() {}

	public LogRepository(String id, String type, String host, String user, String password, Integer port) {
		this.id = id;
		this.type = type;
		this.host = host;
		this.user = user;
		this.password = password;
		this.port = port;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}