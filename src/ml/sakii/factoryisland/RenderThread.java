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
	
	Game game;
	//BufferedImage secondaryBuffer=new BufferedImage(Main.Frame.getWidth(), Main.Frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
	private boolean running=true;
	boolean screenshot=false;
	boolean screenshotstarted=false;
	BufferedImage tmpBuffer;
	private BufferStrategy strategy; 
	
	
	public RenderThread(Game game) {
		this.game=game;
		this.setName("Render Thread");
		//this.secondaryBuffer=secondaryBuffer;
		this.setPriority(MAX_PRIORITY);
		Main.Frame.createBufferStrategy(2);
		strategy = Main.Frame.getBufferStrategy();
	}
	
	@Override
	public void run()
	{
		Graphics graphics = strategy.getDrawGraphics();
		while(running) {
			try {
				if(Config.directRendering && !screenshot) {
					game.render(graphics);
					strategy.show();
				}else {
					game.render(game.FrameBuffer.getGraphics());
					game.getGraphics().drawImage(game.FrameBuffer, 0, 0,Main.Width, Main.Height, null);
					if(screenshot) {
						game.prevFrame = Main.deepCopy(game.FrameBuffer);
						saveScreenshot(game.prevFrame);
						screenshot=false;
					}
				}
				
				
			}catch(Exception e) {
				e.printStackTrace();
				kill();
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
						
				       File outputfile = new File(Config.username+"-"+new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date())+".png");
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
	
	public void kill() {
		running=false;
	}
	
	
	/*public class ScreenCapture      implements Composite, CompositeContext
	{
	      private BufferedImage screenShot;
	      private ColorModel screenColorModel;

	      public ScreenCapture()
	      {
	      }

	      @Override
		public CompositeContext      createContext(ColorModel srcColorModel, ColorModel      dstColorModel,      RenderingHints      hints)
	      {
	            screenColorModel = dstColorModel;
	            return this;
	      }
	      
	      @Override
		public void compose(Raster      src, Raster      dstIn, WritableRaster dstOut)
	      {
	            screenShot = new BufferedImage(screenColorModel,dstOut,false,(Hashtable)null);
	      }
	      
	      @Override
		public void dispose()
	      {
	      }

	      public BufferedImage screenShot(Graphics2D g2d, Rectangle bounds)
	      {
	            Composite oldComposite = g2d.getComposite();
	            ScreenCapture ssc      = new      ScreenCapture();
	            g2d.setComposite(ssc);
	            g2d.fillRect(bounds.x,bounds.y,bounds.width,bounds.height);
	            g2d.setComposite(oldComposite);
	            return ssc.screenShot;
	      }
	      
	}*/
}
