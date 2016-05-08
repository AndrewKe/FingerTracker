import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.*;

import javax.swing.*;

import org.java_websocket.drafts.Draft_10;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

public class Main {

	private static final int VIDEO_WIDTH = 640;
	private static final int VIDEO_HEIGHT = 360;

	public static void main(String[] args) throws Exception {
		Main m = new Main();
	}
	
	
	HandProfile p;
	Server s;
	
	Main() throws InterruptedException, FileNotFoundException, URISyntaxException, UnknownHostException {
		
		s = new Server(8888);
		s.start();
		System.out.println( "ChatServer started on port: " + s.getPort() );
		
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME + "");
		
		
		// Restore hand profile
		Scanner in = new Scanner(new File("settings"));
		if (in.hasNextLine()){
			p = new HandProfile(in.nextLine());
		}
		
		
		JFrame frame = new JFrame("Finger Tracking");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(500, 800);

		VideoPanel panel = new VideoPanel();
		frame.setContentPane(panel);
		panel.delegate = this;
		
		VideoCapture webcam = new VideoCapture(0);
		if (webcam.isOpened()) {
			System.out.println("Found Webcam: " + webcam.toString());
		} else {
			System.out.println("Connection to webcam failed!");
		}

		frame.setVisible(true);
		Mat mat = new Mat();
		//p = new HandProfile(ProfilePresets.naturalLight);
		
		if (webcam.isOpened()) {
			Thread.sleep(200);
			while (true) {
				try {
					webcam.read(mat);
					if (!mat.empty()) {
						
						/*
						 * Crop image to hand area
						 */
						Point a = new Point(VIDEO_WIDTH * 0.46 * 2, VIDEO_HEIGHT * 0.3 * 2);
						Point b = new Point(VIDEO_WIDTH * 0.6 * 2, VIDEO_HEIGHT * 0.64 * 2);

						Rect roi = new Rect((int) a.x, (int) a.y, (int) (b.x - a.x), (int) (b.y - a.y));
						Mat cropped = new Mat(mat, roi);

						Imgproc.rectangle(mat, a, b, new Scalar(0, 0, 255), 5);

						mat = processMat(mat);

						BufferedImage img = MatConverter.convertMatToBufferedImage(mat);
						panel.setImage(img);
						panel.repaint();
						// break;
					}
				}catch (Exception e){
					
				}
				
			}
		}
	}
	
	Mat profileRoi;
	private Mat processMat(Mat original){
		Mat mat = original.clone();
		

		Point a = new Point(0, 0);
		Point b = new Point(VIDEO_WIDTH * 0.3 * 2, VIDEO_HEIGHT * 0.6 * 2);

		Rect roi = new Rect((int) a.x, (int) a.y, (int) (b.x - a.x), (int) (b.y - a.y));
		mat = new Mat(mat, roi);
		
		int width = 80;
		int height = 80;
		Rect r = new Rect(mat.width()/2-width/2, mat.height()/2-height/2, width, height);
		Mat clean = mat.clone();
		//System.out.println(r);
		
		//Imgproc.medianBlur(mat, mat, 21);
		Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2HSV);
		
		profileRoi = new Mat(mat.clone(), r);
		Imgproc.medianBlur(mat, mat, 5);
		Imgproc.rectangle(mat, r.tl(), r.br(), new Scalar(0, 255, 0), 2);
		
		if (p == null){
			//p = new HandProfile("[170.0, 26.0, 117.0]*[170.0, 27.0, 114.0]*[170.0, 27.0, 112.0]*[170.0, 28.0, 108.0]*[165.0, 26.0, 120.0]*[168.0, 28.0, 117.0]*[168.0, 27.0, 114.0]*[170.0, 28.0, 111.0]*[164.0, 27.0, 123.0]*[161.0, 24.0, 118.0]*[166.0, 28.0, 118.0]*[166.0, 29.0, 115.0]*[163.0, 24.0, 125.0]*[163.0, 25.0, 121.0]*[164.0, 27.0, 121.0]*[164.0, 28.0, 119.0]*");
			return mat;
		}
		
		Mat segmented =  Segmenter.segment(clean, p);
		
		//return segmented;
		Hand h = new Hand(segmented, clean);
		//String info = h.cog + " " + (h.fist ? "true": "false") + " " + h.fingers.size();
		//System.out.println(info);
		
		//s.sendToAll(info);
		
		int xMovement =  (int) ((h.cog.x / segmented.width() - 0.5)*10.0);
		int yMovement =  (int) ((h.cog.y / segmented.height() - 0.5)*10.0);
		int fingers = h.fingers.size();
		System.out.printf("%d, %d, %d, %d, %d, %d, %d, %d, %d\n", xMovement, yMovement, 0, 0, 0, fingers, 0, 0, 0);
		
		
		return h.clean;
	}
	
	boolean first = true;
	public void mousePressed() throws FileNotFoundException {
		if (first && p != null){
			
			p = null;
			first = false;
			return;
		}else if (profileRoi != null){
			p = new HandProfile(profileRoi);
			first = true;
			
			PrintWriter writer = new PrintWriter(new File("settings"));
			writer.print(p.toString());
			writer.close();
		}
	}
	

}
