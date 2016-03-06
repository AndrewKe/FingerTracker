import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class VideoPanel extends JPanel {
	private BufferedImage img;
	private long last = System.currentTimeMillis();
	
	
	public VideoPanel() {
		super();
	}
	
	public void setImage(BufferedImage img){
		this.img = img;
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (this.img == null) {
			return;
		}
		
		long now = System.currentTimeMillis();
		int fps = (int) Math.round(1/((now-last)/1000.0));
		last = now;
		
		g.drawImage(img, 0, 0, 320, 180, null);
		g.drawString(fps + " fps", 5, 15);
	}
}
