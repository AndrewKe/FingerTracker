import org.opencv.core.Point;


public class FingerDefect implements Comparable {
	Point left;
	Point right;
	Point defect;

	public FingerDefect(Point l, Point r, Point d) {
		left = l;
		right = r;
		defect = d;
	}

	public int compareTo(Object other) {
		FingerDefect d = (FingerDefect) other;
		return (int) (defect.x - d.defect.x);
	}

}