package fortscale.flume;

import com.google.common.collect.ImmutableMap;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.mozilla.universalchardet.UniversalDetector;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * Class helper to handle Flume event serialization
 * Have two mode of operation - stateful and stateless
 */
public class EnrichmentHandler implements Serializable {

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(EnrichmentHandler.class);
	private static final String DEFAULT_CHARSET = StandardCharsets.UTF_8.name();

	private static final String ENRICHMENT_HEADER = "Flume enrichment";

	/**
	 * The enrichment data, saved in a key\value list
	 */
	private ImmutableMap<String, String> extraData;

	public ImmutableMap<String, String> getExtraData() {
		return extraData;
	}

	public void setExtraData(ImmutableMap<String, String> extraData) {
		this.extraData = extraData;
	}

	/**
	 * The original event
	 */
	private String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setMessage (byte[] message) {

		// Detect the message charset
		UniversalDetector detector = new UniversalDetector(null);
		detector.handleData(message, 0, message.length);
		detector.dataEnd();
		String charset = detector.getDetectedCharset();
		detector.reset();

		if (charset == null) {
			charset = DEFAULT_CHARSET;
		}

		// Convert the data from byte[] to string
		try {
			this.message = new String(message, charset);
		} catch (UnsupportedEncodingException e) {
			logger.error("Could convert event data. error: " + e.getMessage());
		}
	}

	private static String convertMessage (byte[] message) {

		// Detect the message charset
		UniversalDetector detector = new UniversalDetector(null);
		detector.handleData(message, 0, message.length);
		detector.dataEnd();
		String charset = detector.getDetectedCharset();
		detector.reset();

		if (charset == null) {
			charset = DEFAULT_CHARSET;
		}

		// Convert the data from byte[] to string
		try {
			return new String(message, charset);
		} catch (UnsupportedEncodingException e) {
			logger.error("Could convert event data. error: " + e.getMessage());
		}

		return null;
	}

	public EnrichmentHandler() {

	}

	@JsonCreator public EnrichmentHandler(@JsonProperty("message") byte[] message,
			@JsonProperty("extraData") ImmutableMap<String, String> extraData) {

		setExtraData(extraData);
		setMessage(message);
	}

	/**
	 * Create new event body with the original message and the enrich data
	 * @return
	 * @throws IOException
	 */
	public byte[] buildEventBody() throws IOException {
		return toString().getBytes();
	}

	/**
	 * Create new event body with the original message and the enrich data.
	 * This is a stateless implementation of 'buildEventBody'
	 * @param message
	 * @param extraData
	 * @return
	 */
	public static byte[] buildEventBody(byte[] message, ImmutableMap<String, String> extraData ) {
		StringBuilder sb = new StringBuilder();
		sb.append(convertMessage(message) + " ");
		sb.append(ENRICHMENT_HEADER + " ");
		for (final String key: extraData.keySet()) {
			if (key != "type"){
				sb.append(key + " " + extraData.get(key).toString() + " ");
			}
		}

		return sb.toString().getBytes();
	}

	@Override public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(message + " ");
		sb.append(ENRICHMENT_HEADER + " ");
		for (final String key: extraData.keySet()) {
			if (key != "type"){
				sb.append(key + " " + extraData.get(key).toString() + " ");
			}
		}

		return sb.toString();
	}

}
