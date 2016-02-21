package fortscale.utils.pxGrid;

/**
 * Created by tomerd on 19/01/2016.
 */
public enum PxGridConnectionStatus {
	DISCONNECTED ("Disconnected"),
	MISSING_CONFIGURATION ("Configuration error"),
	INVALID_KEYS_SETTINGS ("Invalid keys setting"),
	INVALID_KEYS ("Invalid keys"),
	CONNECTION_ERROR ("Connection Error"),
	CONNECTED ("Connected");

	PxGridConnectionStatus(String message){
		this.message = message;
	}

	private final String message;

	public String message(){
		return  message;
	}
}
