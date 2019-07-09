package ml.sakii.factoryisland;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

public class RenderThread extends Thread
{
	
	Game game;
	//BufferedImage secondaryBuffer=new BufferedImage(Main.Frame.getWidth(), Main.Frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
	private boolean running=true;
	boolean screenshot=false;
	boolean screenshotstarted=false;
	BufferedImage tmpBuffer;
	
	public RenderThread(Game game) {
		this.game=game;
		this.setName("Render Thread");
		//this.secondaryBuffer=secondaryBuffer;
		this.setPriority(MAX_PRIORITY);
		 
	}
	
	@Override
	public void run()
	{
		//tmpBuffer= Main.graphicsconfig.createCompatibleImage(Main.getWidth(), Main.Frame.getHeight());
		while(running) {

			game.render(game.FrameBuffer.getGraphics());
			//tmpBuffer.getGraphics().drawImage(game.FrameBuffer, 0, 0,tmpBuffer.getWidth(), tmpBuffer.getHeight(), null);
			game.getGraphics().drawImage(game.FrameBuffer, 0, 0,Main.Width, Main.Height, null);
						
			//game.render(game.getGraphics());
			
			if(screenshot && !screenshotstarted) {
				game.prevFrame.getGraphics().drawImage(game.FrameBuffer, 0, 0, null);
				screenshotstarted=true;
				
				SwingUtilities.invokeLater(new Runnable() {
					
					@Override
					public void run()
					{
							
					       File outputfile = new File(Config.username+"-"+new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date())+".png");
							try
							{
								ImageIO.write(game.prevFrame, "png", outputfile);
							} catch (IOException e)
							{
								e.printStackTrace();
							}
							Main.log("Screenshot saved");
							screenshot=false;
							screenshotstarted=false;

		
					}
					
			     
				});

			}
			
			game.FrameBuffer.flush();
			
			/*try
			{
				Thread.sleep(500);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}*/
			
		}
		
		game.pause();
		

	}
	
	public void kill() {
		running=false;
	}
}
