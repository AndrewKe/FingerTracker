import java.awt.image.BufferedImage;

import javax.swing.*;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

public class Main {
	
	public static void main(String[] args) throws Exception {
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME + "");
		
		JFrame frame = new JFrame("Finger Tracking");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(320, 180);

		VideoPanel panel = new VideoPanel();
		frame.setContentPane(panel);

		VideoCapture webcam = new VideoCapture(0);
		if (webcam.isOpened()) {
			System.out.println("Found Webcam: " + webcam.toString());
		} else {
			System.out.println("Connection to webcam failed!");
		}
		
		frame.setVisible(true);
		Mat mat = new Mat();
		
		if (webcam.isOpened()){
			Thread.sleep(200);
			while (true) {
				webcam.read(mat);
				if (!mat.empty()){
					//Thread.sleep(200);
					//Imgproc.blur(mat, mat, new Size(50, 50));
					BufferedImage img = MatConverter.convertMatToBufferedImage(mat);
					panel.setImage(img);
					panel.repaint();
				}
			}
		}

	}
}
