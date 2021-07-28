package ml.sakii.factoryisland.screens;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import ml.sakii.factoryisland.Main;

public class PaintedScreen extends Screen {
	
	private static final long serialVersionUID = 8391969921046037544L;
	private BufferedImage pattern;
	
	public PaintedScreen(BufferedImage pattern) {
		this.pattern=pattern;
	}

	@Override
	public void paintComponent(Graphics g){
		//g.clearRect(0, 0, Main.Frame.getWidth(), Main.Frame.getHeight());
		Graphics2D g2 = (Graphics2D)g;
		g2.setPaint(new TexturePaint(pattern, new Rectangle2D.Float(0,0,pattern.getWidth(),pattern.getHeight())));
		g2.fillRect(0, 0, Main.Frame.getWidth(), Main.Frame.getHeight());

	}
	
	

}
