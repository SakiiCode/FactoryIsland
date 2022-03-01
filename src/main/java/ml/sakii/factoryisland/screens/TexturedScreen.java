package ml.sakii.factoryisland.screens;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import ml.sakii.factoryisland.GUIManager;
import ml.sakii.factoryisland.Main;

public class TexturedScreen extends Screen {
	private static final long serialVersionUID = 3236635442012595188L;
	
	private BufferedImage Background;
	
	@SuppressWarnings("hiding")
	protected int EntrySpacing = (int) (Main.Height*0.011f);
	
	
	public TexturedScreen(BufferedImage bg, GUIManager guiManager) {
		super(guiManager);
		this.Background=bg;
	}
	
	@Override
	  protected void paintComponent(Graphics g) {
	    
	    super.paintComponent(g);
		Rectangle2D area = centerImage(Main.Width, Main.Height, Background.getWidth(), Background.getHeight());
		g.drawImage(Background, (int)area.getX(), (int)area.getY(), (int)area.getWidth(), (int)area.getHeight(), null);

	}
	
	private static Rectangle2D.Float centerImage(int sw, int sh, int iw, int ih){
		float imageRatio = iw*1.0f/ih;
        float windowRatio = sw*1.0f/sh;
        if(windowRatio>imageRatio){
        	return new Rectangle2D.Float(0, -(sw/imageRatio-sh)/2, sw, sw/imageRatio);
        }
		return new Rectangle2D.Float(-(sh*imageRatio-sw)/2, 0, sh*imageRatio, sh);
		
	}

}
