import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.opencv.core.*;
import org.opencv.imgproc.*;

public class Hand {
	
	Mat mat;
	Point cog = new Point();
	List<Point> fingers = new ArrayList<Point>();
	Mat clean;
	boolean fist;
	
	public Hand(Mat mat, Mat clean) {
		this.mat = mat;
		this.clean = clean;
		process();
	}

	public void process() {
		
		// Get the contour for the hand
		MatOfPoint hand = getHandContour();
		
		if (hand == null){
			return;
		}
		
		// Get center of gravity
		cog = getCOG(hand);
		Imgproc.circle(clean, cog, 30, new Scalar(255, 255, 0));
		
		//System.out.print("Cog: " + cog.toString());
		Imgproc.putText(clean, cog.toString(), new Point(50, 25), 0, 0.6, new Scalar(0, 0, 255), 2);

		// Convert hand to simpler polygon
		MatOfPoint2f handPoly2f = new MatOfPoint2f();
		Imgproc.approxPolyDP(new MatOfPoint2f(hand.toArray()), handPoly2f, 15, true);
		hand = new MatOfPoint(handPoly2f.toArray());
		
		// Get the convex hull
		MatOfInt hull = new MatOfInt();
		Imgproc.convexHull(hand, hull, true);

		// Turn the convex hull from a MatOfInt in to a MatOfPoint
		MatOfPoint hullContour = new MatOfPoint();
		List<Point> hullPoints = new ArrayList<Point>();

		for (int j = 0; j < hull.toList().size(); j++) {
			Point p = hand.toList().get(hull.toList().get(j));
			Imgproc.putText(clean, hull.toList().get(j) + "", p, 0, 0.6, new Scalar(0, 255, 0));
			////System.out.println(hull.toList().get(j));
			Point pBefore;
			
			if (j >= 1 ){
				pBefore = hand.toList().get(hull.toList().get(j-1));
			}else {
				pBefore = hand.toList().get(hull.toList().get(hull.toList().size()-1));
			}
			
			if (distance(pBefore, p) < 50){
				continue;
			}
			
			hullPoints.add(p);
		}
		hullContour.fromList(hullPoints);
		
		
		for (Point p: hand.toList()){
			//Imgproc.putText(clean, hand.toList().indexOf(p) + "", p, 0, 0.6, new Scalar(0, 255, 0));
		}
		
		double minY = Double.MAX_VALUE;
		for (Point p: hullPoints){
			if (p.y < minY){
				minY = (int) p.y;
			}
		}
		
		double dist = cog.y - minY;
		////System.out.println(" Dist: " + dist);
		
		System.out.println(dist);
		if (dist < 150){
			//System.out.println("FIST");
			fist = true;
			Imgproc.putText(clean, "FIST", new Point(50, 75), 0, 0.6, new Scalar(255, 0, 0), 2);
			return;
		}else {
			fist = false;
			Imgproc.putText(clean, "NO FIST", new Point(50, 75), 0, 0.6, new Scalar(255, 0, 0), 2);
		}
		
		Imgproc.drawContours(clean, Arrays.asList(hand), 0, new Scalar(255, 255, 255, 255), 1);
		Imgproc.drawContours(clean, Arrays.asList(hullContour), 0, new Scalar(255, 0, 0), 2);
		
		fingers = new ArrayList<Point>();
		int fingerCount = 0;
		for (Point p : hullPoints) {
			if (p.y < cog.y){
				if (distance(p, cog) < 110){
					continue;
				}
				fingerCount++;
				fingers.add(p);
				Imgproc.circle(clean, p, 5, new Scalar(0, 0, 255), 5);
			}
		}
		
		//System.out.println(fingerCount);
		Imgproc.putText(clean, fingerCount + " fingers", new Point(50, 50), 0, 0.6, new Scalar(0, 255, 0), 2);
	}

	private static double angleBetween(Point center, Point current, Point previous) {

		return Math.toDegrees(Math.atan2(current.x - center.x, current.y - center.y)
				- Math.atan2(previous.x - center.x, previous.y - center.y));
	}

	private static Point mid(Point left, Point right) {
		return new Point((left.x + right.x) / 2, (left.y + right.y) / 2);
	}
	
	
	private static double distance(Point a, Point b){
		return Math.sqrt((a.x - b.x)*(a.x - b.x) + (a.y - b.y)*(a.y - b.y));
	}
	
	private static Integer hullNeighbor(int defectIndex, MatOfInt hull, boolean direction) {
		List<Integer> hullPoints = hull.toList();
		Collections.sort(hullPoints);

		if (direction) {
			for (Integer i : hullPoints) {
				if (i == defectIndex) {
					return null;
				}
				if (i > defectIndex) {
					return i;
				}
			}
			return 0;
		} else {
			int last = 0;
			for (Integer i : hullPoints) {
				if (i == defectIndex) {
					return null;
				}
				if (i > defectIndex) {
					return last;
				}
				last = i;
			}
			return last;
		}

	}
	
	private MatOfPoint getHandContour(){
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Imgproc.findContours(mat.clone(), contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
		
		MatOfPoint hand = null;
		for (MatOfPoint contour : contours) {
			if (hand != null) {
				if (Imgproc.contourArea(hand) < Imgproc.contourArea(contour) && Imgproc.contourArea(contour) > 4000)
					hand = contour;
			} else {
				hand = contour;
			}
		}
		
		if (hand == null || Imgproc.contourArea(hand) < 5000){
			return null;
		}
		
		System.out.println(Imgproc.contourArea(hand));
		
		return hand;
	}
	
	private Point getCOG(Mat contour){
		Moments p = Imgproc.moments(contour);
		int x = (int) (p.get_m10() / p.get_m00());
        int y = (int) (p.get_m01() / p.get_m00());
        
        return new Point(x, y);
	}
}
