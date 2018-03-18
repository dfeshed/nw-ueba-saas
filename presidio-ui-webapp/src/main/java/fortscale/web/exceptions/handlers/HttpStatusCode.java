package fortscale.web.exceptions.handlers;


public enum HttpStatusCode {

	PASSWORD_EXPIRED(40301,"Password expired"),
	WRONG_PASSWORD(40101,"Wrong password"),
	USER_ALREADY_EXIST(40001,"User already exist"),
	USERNAME_NOT_FOUND(40002,"Username not found"),
	INVALID_VALUE(40003,"Invalid value");
	
	private final int value;

	private final String reasonPhrase;


	private HttpStatusCode(int value, String reasonPhrase) {
		this.value = value;
		this.reasonPhrase = reasonPhrase;
	}

	/**
	 * Return the integer value of this status code.
	 */
	public int value() {
		return this.value;
	}

	/**
	 * Return the reason phrase of this status code.
	 */
	public String getReasonPhrase() {
		return reasonPhrase;
	}


	/**
	 * Return a string representation of this status code.
	 */
	@Override
	public String toString() {
		return Integer.toString(value);
	}


	/**
	 * Return the enum constant of this type with the specified numeric value.
	 * @param statusCode the numeric value of the enum to be returned
	 * @return the enum constant with the specified numeric value
	 * @throws IllegalArgumentException if this enum has no constant for the specified numeric value
	 */
	public static HttpStatusCode valueOf(int statusCode) {
		for (HttpStatusCode status : values()) {
			if (status.value == statusCode) {
				return status;
			}
		}
		throw new IllegalArgumentException("No matching constant for [" + statusCode + "]");
	}
}
