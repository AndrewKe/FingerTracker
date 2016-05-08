import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.opencv.core.*;
import org.opencv.imgproc.*;

public class Segmenter {
	
	/*
	 * Primary segmentation algorithm using HSV thresholding and morphological operations
	 */
	public static Mat segment(Mat mat, HandProfile profile) {
		mat = mat.clone();
		// Convert the color space from BGR to HSV
		Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2HSV);
		Imgproc.medianBlur(mat, mat, 5);
		
		if (true){
			//return mat;
		}
		//HandProfile profile = new HandProfile(new Mat(mat, r));
		Mat sum = profile.thresholdHand(mat);
		
		
		Imgproc.medianBlur(sum, sum, 11);
		//Imgproc.erode(sum, sum, Mat.ones(10, 10, CvType.CV_8UC1));
		
		//Imgproc.morphologyEx(sum, sum, Imgproc.MORPH_CLOSE, Mat.ones(5, 5, CvType.CV_8UC1));		

		return sum;
	}
	
	
	
	/*
	 * Experimental second segmentation algorithm using a histogram and back projection. Doesn't really work
	 */
	public static void segment2(Mat in, Mat source, Mat mat) {

		//Imgproc.GaussianBlur(in, mat, new Size(31, 31), 0);
		Imgproc.medianBlur(in, mat, 21);
		Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2HSV);

		mat = histAndBackproj(mat, source);

		Imgproc.morphologyEx(mat, mat, Imgproc.MORPH_CLOSE, Mat.ones(10, 10, CvType.CV_8UC1));
		Imgproc.GaussianBlur(mat, mat, new Size(31, 31), 0);
	}

	private static Mat histAndBackproj(Mat source, Mat sourceHist) {
		Mat hist = new Mat();
		int h_bins = 30;
		int s_bins = 32;

		MatOfInt mHistSize = new MatOfInt(h_bins, s_bins);
		MatOfFloat mRanges = new MatOfFloat(0, 179, 0, 255);
		MatOfInt mChannels = new MatOfInt(0, 1);

		boolean accumulate = false;
		Imgproc.calcHist(Arrays.asList(sourceHist), mChannels, new Mat(), hist, mHistSize, mRanges, accumulate);

		Core.normalize(hist, hist, 0, 255, Core.NORM_MINMAX, -1, new Mat());

		Mat backproj = new Mat();
		Imgproc.calcBackProject(Arrays.asList(source), mChannels, hist, backproj, mRanges, 1);

		return backproj;
	}

}

//sum.copyTo(mat);



//Mat matC = new Mat();
//Mat matD = new Mat();
//
//Core.inRange(mat, new Scalar(0, 60, 30), new Scalar(10, 120, 255), matC);
//Core.inRange(mat, new Scalar(175, 95, 100), new Scalar(195, 115, 150), matD);
//
//
//// Perform morphological operations
//Imgproc.morphologyEx(mat, mat, Imgproc.MORPH_CLOSE, Mat.ones(2, 2, CvType.CV_8UC1));
//Imgproc.erode(mat, mat, Mat.ones(55, 55, CvType.CV_8UC1));
//
//Core.bitwise_or(matC, matD, mat);
//return mat;
