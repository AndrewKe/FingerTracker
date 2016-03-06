import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.*;

public class MatConverter {

	public static BufferedImage convertMatToBufferedImage(Mat mat) {
		
		MatOfByte mob = new MatOfByte();
		Imgcodecs.imencode(".png", mat, mob);
		
		byte[] arr = mob.toArray();
		BufferedImage img = null;
		try {
			InputStream in = new ByteArrayInputStream(arr);
			img = ImageIO.read(in);
		} catch (Exception e){
			System.out.println("Mat to buffered image conversion failed");
		}
		
		return img;
	}
}
