package fortscale.web.rest;

import fortscale.domain.core.ApplicationConfiguration;
import fortscale.services.ApplicationConfigurationService;
import fortscale.utils.logging.Logger;
import fortscale.utils.logging.annotation.LogException;
import fortscale.utils.pxGrid.KeysGenerationHandler;
import fortscale.utils.pxGrid.PxGridConnectionStatus;
import fortscale.utils.pxGrid.PxGridHandler;
import fortscale.web.DataQueryController;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Decoder;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Controller @RequestMapping("/api/pxgrid") public class ApiPxGridController extends DataQueryController {

	private static Logger logger = Logger.getLogger(ApiPxGridController.class);

	private final static String CER_FILE_NAME = "pxGridClient.cer";

	private final static String CSR_KEY = "system.pxgrid.csr";
	private final static String CER_KEY = "system.pxgrid.cer";

	private final static String HOSTS_KEY = "system.pxgrid.hosts";
	private final static String USERNAME_KEY = "system.pxgrid.username";
	private final static String GROUP_KEY = "system.pxgrid.group";
	private final static String KEYSTOREPATH_KEY = "system.pxgrid.keystorepath";
	private final static String KEYSTORE_PASSPHARSE_KEY = "system.pxgrid.keystorepasspharse";
	private final static String TRUSTSTORE_PATH_KEY = "system.pxgrid.truststore";
	private final static String TRUSTSTORE_PASSPHARSE_KEY = "system.pxgrid.truststorepasspharse";
	private final static String CONNECTION_RETRY_MILLISECOND_KEY = "system.pxgrid.connectionretrymillisecond";

	@Autowired ApplicationConfigurationService applicationConfigurationService;

	@RequestMapping(method = RequestMethod.GET) @LogException public @ResponseBody ResponseEntity connectToPxGrid() {
		PxGridHandler pxGridHandler = createPxGridHandler();
		PxGridConnectionStatus status = pxGridHandler.connectToGrid();
		switch (status) {
		case CONNECTED:
			return ResponseEntity.ok().body("{ \"server\": \"" + pxGridHandler.getHost() + "\"}");
		case DISCONNECTED:
		case CONNECTION_ERROR:
		case INVALID_KEYS:
		case MISSING_CONFIGURATION:
			return new ResponseEntity(status.message(), HttpStatus.BAD_REQUEST);
		default:
			return new ResponseEntity(status.message(), HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/generate_cer", method = RequestMethod.GET) @LogException public @ResponseBody
	ResponseEntity generateCER() {
		KeysGenerationHandler keysHandler = new KeysGenerationHandler();
		try {
			String base64Cert = keysHandler.generateKeySelfSignedCert();
			applicationConfigurationService.insertConfigItem(CER_KEY, base64Cert);
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/generate_csr", method = RequestMethod.GET) @LogException public @ResponseBody
	ResponseEntity generateCSR() {
		KeysGenerationHandler keysHandler = new KeysGenerationHandler();
		try {
			String base64Csr = keysHandler.generateCSR();
			applicationConfigurationService.insertConfigItem(CSR_KEY, base64Csr);
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/update_cer", method = RequestMethod.POST) @LogException public @ResponseBody
	ResponseEntity updateCER(@RequestBody String body) {
		JSONObject params;
		try {
			params = new JSONObject(body);
			String base64CerFile = params.getString("base64CerFile");
			saveFile(base64CerFile, CER_FILE_NAME);
			applicationConfigurationService.insertConfigItem(CER_KEY, base64CerFile);
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		} catch (JSONException e) {
			return responseErrorHandler("Could not update config items. Failed to parse POST Body to JSON.", HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/export", method = RequestMethod.GET, produces = "application/text") @LogException public
	@ResponseBody ResponseEntity exportFile(@RequestParam String fileType) {
		try {
			switch (fileType) {
			case "cer": {
				String base64CER = readFromBase64Config(CER_KEY);
				return ResponseEntity.ok().header("content-disposition", "attachment; filename=pxGridClient.cer").contentLength(base64CER.length()).contentType(MediaType.parseMediaType("application/octet-stream")).body(base64CER);
			}
			case "csr": {
				String base64CSR = readFromBase64Config(CSR_KEY);
				return ResponseEntity.ok().header("content-disposition", "attachment; filename=pxGridClient.csr").contentLength(base64CSR.length()).contentType(MediaType.parseMediaType("application/octet-stream")).body(base64CSR);
			}
			default:
				return new ResponseEntity(HttpStatus.BAD_REQUEST);
			}
		} catch (Exception e) {
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/generate_keys", method = RequestMethod.POST) @LogException public @ResponseBody
	ResponseEntity generateKeys(@RequestBody String body) {
		JSONObject params;
		try {
			params = new JSONObject(body);

			KeysGenerationHandler keysHandler = new KeysGenerationHandler();
			String password = params.getString("password");
			String base64PemFile = params.getString("base64PemFile");
			Map.Entry<String, String> entry = keysHandler.generateKeys(password, base64PemFile);
			updateConfig(entry, password);
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		} catch (JSONException e) {
			return responseErrorHandler("Could not update config items. Failed to parse POST Body to JSON.", HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			return responseErrorHandler("Error while generate keys.", HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/generate_ca_keys", method = RequestMethod.POST) @LogException public @ResponseBody
	ResponseEntity generateCAKeys(@RequestBody String body) {
		JSONObject params;

		try {
			params = new JSONObject(body);

			String password = params.getString("password");
			String base64PemFile = params.getString("base64PemFile");
			String base64CaFile = params.getString("base64CaFile");

			KeysGenerationHandler keysHandler = new KeysGenerationHandler();
			Map.Entry<String, String> entry = keysHandler.generateKeys(password, base64PemFile, base64CaFile);
			updateConfig(entry, password);
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		} catch (JSONException e) {
			return responseErrorHandler("Could not update config items. Failed to parse POST Body to JSON.", HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			return new ResponseEntity(HttpStatus.BAD_REQUEST);
		}
	}

	private ResponseEntity<String> responseErrorHandler(String message, HttpStatus status) {
		JSONObject errorBody = new JSONObject();
		errorBody.put("message", message);
		return new ResponseEntity<>(errorBody.toString(), status);
	}

	private PxGridHandler createPxGridHandler() {
		String hosts = readFromConfigurationService(HOSTS_KEY);
		String userName = readFromConfigurationService(USERNAME_KEY);
		String group = readFromConfigurationService(GROUP_KEY);
		String keystorePath = readFromConfigurationService(KEYSTOREPATH_KEY);
		String keystorePassphrase = readFromConfigurationService(KEYSTORE_PASSPHARSE_KEY);
		String truststorePath = readFromConfigurationService(TRUSTSTORE_PATH_KEY);
		String truststorePassphrase = readFromConfigurationService(TRUSTSTORE_PASSPHARSE_KEY);
		int connectionRetryMillisecond = Integer.
				parseInt(readFromConfigurationService(CONNECTION_RETRY_MILLISECOND_KEY));
		return new PxGridHandler(hosts, userName, group, keystorePath, keystorePassphrase, truststorePath, truststorePassphrase, connectionRetryMillisecond);
	}

	private String readFromConfigurationService(String key) {
		ApplicationConfiguration applicationConfiguration = applicationConfigurationService.
				getApplicationConfigurationByKey(key);
		if (applicationConfiguration != null) {
			return applicationConfiguration.getValue();
		}
		return null;
	}

	private String readFromBase64Config(String key) {
		ApplicationConfiguration configItem = applicationConfigurationService.getApplicationConfigurationByKey(key);
		if (configItem == null) {
			return "";
		}

		BASE64Decoder decoder = new BASE64Decoder();
		try {
			byte[] encoded = decoder.decodeBuffer(configItem.getValue());
			return new String(encoded, StandardCharsets.UTF_8);
		} catch (IOException e) {
			return "";
		}
	}

	private void saveFile(String base64File, String path) throws IOException {
		byte[] keyBytes;

		BASE64Decoder decoder = new BASE64Decoder();
		keyBytes = decoder.decodeBuffer(base64File);
		try (OutputStream stream = new FileOutputStream(path)) {
			stream.write(keyBytes);
		}
	}

	private void updateConfig(Map.Entry<String, String> entry, String password) {
		applicationConfigurationService.insertConfigItem(KEYSTORE_PASSPHARSE_KEY, password);
		applicationConfigurationService.insertConfigItem(KEYSTOREPATH_KEY, entry.getKey());
		applicationConfigurationService.insertConfigItem(TRUSTSTORE_PASSPHARSE_KEY, password);
		applicationConfigurationService.insertConfigItem(TRUSTSTORE_PATH_KEY, entry.getValue());
	}
}