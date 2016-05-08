import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import javax.swing.*;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.video.BackgroundSubtractorMOG2;
import org.opencv.video.Video;

public class BackgroundSubtraction {

	private static final int VIDEO_WIDTH = 640;
	private static final int VIDEO_HEIGHT = 360;

	public static void main(String[] args) throws Exception {

		System.loadLibrary(Core.NATIVE_LIBRARY_NAME + "");

		JFrame frame = new JFrame("Finger Tracking");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(VIDEO_WIDTH, VIDEO_HEIGHT);

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

		boolean calibrated = false;
		
		BackgroundSubtractorMOG2 sub = Video.createBackgroundSubtractorMOG2();
		sub.setHistory(300);
		  
		System.out.println("Place your palm in the green box");
		if (webcam.isOpened()) {
			Thread.sleep(200);
			while (true) {
				webcam.read(mat);
				if (!mat.empty()) {
					System.out.println("new");
					// Thread.sleep(200);
					
					// Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2HSV);
					
					//Imgproc.GaussianBlur(mat, mat, new Size(5, 5), 0);
					Imgproc.medianBlur(mat, mat, 21);
					Mat n = new Mat();
					sub.apply(mat, n);
					

					BufferedImage img = MatConverter.convertMatToBufferedImage(n);
					panel.setImage(img);
					panel.repaint();
				}
			}
		}

	}
	
	private static Mat histAndBackproj(Mat source, Mat sourceHist) {
	    Mat hist = new Mat();
	    int h_bins = 30; 
	    int s_bins = 32;

	    // C++:
	    //int histSize[] = { h_bins, s_bins };
	    MatOfInt mHistSize = new MatOfInt (h_bins, s_bins);

	    // C++:
	    //float h_range[] = { 0, 179 };
	    //float s_range[] = { 0, 255 };     
	    //const float* ranges[] = { h_range, s_range };     
	    //int channels[] = { 0, 1 };

	    MatOfFloat mRanges = new MatOfFloat(0, 179, 0, 255);
	    MatOfInt mChannels = new MatOfInt(0, 1);

	    // C++:
	    // calcHist( &hsv, 1, channels, mask, hist, 2, histSize, ranges, true, false );

	    boolean accumulate = false;
	    Imgproc.calcHist(Arrays.asList(sourceHist), mChannels, new Mat(), hist, mHistSize, mRanges, accumulate);

	    // C++:
	    // normalize( hist, hist, 0, 255, NORM_MINMAX, -1, Mat() );        
	    Core.normalize(hist, hist, 0, 255, Core.NORM_MINMAX, -1, new Mat());

	    // C++:
	    // calcBackProject( &hsv, 1, channels, hist, backproj, ranges, 1, true );        
	    Mat backproj = new Mat();
	    Imgproc.calcBackProject(Arrays.asList(source), mChannels, hist, backproj, mRanges, 1);
	    return backproj;
	}

}
