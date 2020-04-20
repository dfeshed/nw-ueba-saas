package fortscale.utils.image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

/**
 * Created by Amir Keren on 19/01/16.
 */
public class ImageUtils {

	public void convertBase64ToImg(String base64Str, String destFilePath, String fileType) throws IOException {
		byte[] imageByte = Base64.getDecoder().decode(base64Str);
		ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
		BufferedImage image = ImageIO.read(bis);
		bis.close();
		File outputFile = new File(destFilePath);
		ImageIO.write(image, fileType, outputFile);
	}

}
