package fortscale.utils.image;

import sun.misc.BASE64Decoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

/**
 * Created by Amir Keren on 19/01/16.
 */
public class ImageUtils {

	public void convertBase64ToPNG(String base64Str, String destFilePath) throws IOException {
		BufferedImage image;
		byte[] imageByte;
		BASE64Decoder decoder = new BASE64Decoder();
		imageByte = decoder.decodeBuffer(base64Str);
		ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
		image = ImageIO.read(bis);
		bis.close();
		File outputfile = new File(destFilePath);
		ImageIO.write(image, "png", outputfile);
	}

}