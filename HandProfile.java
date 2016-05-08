import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.opencv.core.*;
import org.opencv.imgproc.*;

public class HandProfile {
	
	List<double[]> colors = new ArrayList<double[]>();
	
	public HandProfile(Mat mat){
		//Imgproc.rectangle(mat, r.tl(), r.br(), new Scalar(255, 255, 255), 2);
		Imgproc.medianBlur(mat, mat, 5);
		
		for (int i = 0; i < 4; i++){
			for (int j = 0; j < 4; j++){
				Point p = new Point(i * (mat.width()-1)/3, j * (mat.height()-1)/3);
				//System.out.println(p.toString() + " " + Arrays.toString((mat.get((int) p.y, (int) p.x))));
				colors.add(mat.get((int) p.y, (int) p.x)); 
			}
		}
		
		for (double[] color: colors)
		{
			System.out.print(Arrays.toString(color));
			System.out.print("*");
		}
		System.out.println();
		//Imgproc.circle(mat, new Point(mat.width() / 2, mat.height() / 2), 20, new Scalar(255));
	}
	
	public HandProfile(String input){
		String[] strs = input.split("\\*");
		for (String color: strs){
			if (color.isEmpty()){
				continue;
			}
			
			color = color.substring(1, color.length()-1);
			String[] values = color.split(",");
			
			double h = Double.parseDouble(values[0].trim());
			double s = Double.parseDouble(values[1].trim());
			double v = Double.parseDouble(values[2].trim());
			
			double[] hsv = new double[]{h, s, v};
			colors.add(hsv);
		}
		
		for (double[] color: colors)
		{
			System.out.print(Arrays.toString(color));
			System.out.print("*");
		}
		System.out.println();
	}
	
	public  String toString(){
		String ret = "";
		for (double[] color: colors)
		{
			ret += Arrays.toString(color) + "*";
		}
		return ret;
	}
	
	public Mat thresholdHand(Mat mat){
		Mat sum = null;
		for(double[] hsv: colors){
			Mat thresh = new Mat();
			//System.out.println(Arrays.toString(hsv));
			
			if (hsv == null){
				continue;
			}
			
			int dA = 20;
			int dB = 50;
			int dC = 50;
			Scalar low = new Scalar(truncate(hsv[0]) - dA, truncate(hsv[1]) - dB, truncate(hsv[2]) - dC);
			Scalar high = new Scalar(truncate(hsv[0]) + dA, truncate(hsv[1]) + dB, truncate(hsv[2]) + dC);
			Core.inRange(mat, low, high, thresh);
			if (sum == null){
				//System.out.print("a");
				sum = thresh;
				//break;
			}else {
				//System.out.print("b");
				Core.bitwise_or(sum, thresh, sum);
			}
		}
		
		return sum;
	}
	
	private static double truncate(double x){
		if (x < 0){
			return 0;
		}
		return x;
	}
	
}
