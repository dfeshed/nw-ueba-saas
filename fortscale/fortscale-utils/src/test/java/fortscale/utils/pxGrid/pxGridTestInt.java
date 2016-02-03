package fortscale.utils.pxGrid;

import org.junit.Test;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class pxGridTestInt {

	@Test
	public void testPrivateKeyGeneration(){
		keysGenerationHandler handler = new keysGenerationHandler();
		try {
			handler.generatePrivateKey();
			//handler.generateCSRrequest();
			//handler.generatePKCS12();
			//handler.importIntoIdentityKeystore();
		}  catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
}
