import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;

import javax.swing.JPanel;

public class VideoPanel extends JPanel implements MouseListener{
	private BufferedImage img;
	private long last = System.currentTimeMillis();
	public Main delegate;
	
	public VideoPanel() {
		super();
		addMouseListener(this);
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
		
		g.drawImage(img, 0, 0, getWidth(), getHeight(), null);
		g.setColor(Color.WHITE);
		g.drawString(fps + " fps", 5, 15);
	}
	
	public void mousePressed(MouseEvent e) {
	       if (delegate != null){
	    	   try {
				delegate.mousePressed();
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	       }
	    }

	    public void mouseReleased(MouseEvent e) {
	       
	    }

	    public void mouseEntered(MouseEvent e) {
	       
	    }

	    public void mouseExited(MouseEvent e) {
	       
	    }

	    public void mouseClicked(MouseEvent e) {
	       
	    }

	    
}
