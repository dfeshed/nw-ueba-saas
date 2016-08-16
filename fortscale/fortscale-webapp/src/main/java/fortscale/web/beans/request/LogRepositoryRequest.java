package fortscale.web.beans.request;

import fortscale.domain.fetch.LogRepository;

/**
 * Created by Amir Keren on 8/15/16.
 */
public class LogRepositoryRequest extends LogRepository {

	private boolean encryptedPassword;

	public LogRepository getLogRepository() {
		return new LogRepository(type, host, user, password, port);
	}

	public boolean isEncryptedPassword() {
		return encryptedPassword;
	}

	public void setEncryptedPassword(boolean encryptedPassword) {
		this.encryptedPassword = encryptedPassword;
	}

}