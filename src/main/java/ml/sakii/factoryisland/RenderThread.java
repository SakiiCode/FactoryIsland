package ml.sakii.factoryisland;

import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

public class RenderThread extends Thread
{
	boolean screenshot=false;
	
	private Game game;
	private boolean running=true;
	private boolean screenshotstarted=false;
	private BufferStrategy strategy; 
	private GUIManager guiManager;
	
	public RenderThread(Game game, GUIManager guiManager) {
		this.game=game;
		this.guiManager=guiManager;
		this.setName("Render Thread");
		this.setPriority(MAX_PRIORITY);
		strategy = guiManager.createBufferStrategy();
	}
	
	@Override
	public void run()
	{
		Graphics graphics = strategy.getDrawGraphics();
		guiManager.SwitchWindow("game");
		while(running) {
			
				
			if(screenshot) {
				game.render(game.FrameBuffer.getGraphics());
				game.getGraphics().drawImage(game.FrameBuffer, 0, 0,Main.Width, Main.Height, null);
				game.prevFrame = Main.deepCopy(game.FrameBuffer);
				saveScreenshot(game.prevFrame);
				screenshot=false;
			}else {
				switch(Config.renderMethod) {
				case DIRECT:
					game.render(graphics);
					strategy.show();
					break;
				case VOLATILE:
					game.render(game.VolatileFrameBuffer.getGraphics());
					game.getGraphics().drawImage(game.VolatileFrameBuffer, 0, 0,Main.Width, Main.Height, null);
					break;
				case BUFFERED:
					game.render(game.FrameBuffer.getGraphics());
					game.getGraphics().drawImage(game.FrameBuffer, 0, 0,Main.Width, Main.Height, null);
					break;
				}
			}

			
		}
		
		
		graphics.dispose();
		strategy.dispose();
		

	}

	
	 private void saveScreenshot(BufferedImage img) {
		
		if(!screenshotstarted) {
			
			
			screenshotstarted=true;
			
			SwingUtilities.invokeLater(new Runnable() {
				
				@Override
				public void run()
				{
					File screenshots = new File("screenshots");
					if(!screenshots.exists()) {
						screenshots.mkdir();
					}
			       	File outputfile = new File("screenshots/"+Config.username+"-"+new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date())+".png");
					try
					{
						ImageIO.write(img, "png", outputfile);
					} catch (IOException e)
					{
						e.printStackTrace();
					}
					Main.log("Screenshot saved");
					screenshotstarted=false;

	
				}
				
		     
			});

		}
	}
	
	void kill() {
		running=false;
		try {
			this.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
