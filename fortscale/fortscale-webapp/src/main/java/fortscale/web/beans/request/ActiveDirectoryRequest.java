package fortscale.web.beans.request;

import fortscale.domain.ad.AdConnection;

/**
 * Created by Amir Keren on 8/10/16.
 */
public class ActiveDirectoryRequest extends AdConnection {

	private boolean encryptedPassword;

	public AdConnection getAdConnection() {
		return new AdConnection(dcs, domainBaseSearch, domainUser, domainPassword);
	}

	public boolean isEncryptedPassword() {
		return encryptedPassword;
	}

	public void setEncryptedPassword(boolean encryptedPassword) {
		this.encryptedPassword = encryptedPassword;
	}

}