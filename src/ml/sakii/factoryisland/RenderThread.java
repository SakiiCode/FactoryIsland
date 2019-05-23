package ml.sakii.factoryisland;


public class RenderThread extends Thread
{
	
	private Game game;
	//BufferedImage secondaryBuffer=new BufferedImage(Main.Frame.getWidth(), Main.Frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
	private boolean running=true;
	public RenderThread(Game game) {
		this.game=game;
		//this.secondaryBuffer=secondaryBuffer;
		this.setPriority(MAX_PRIORITY);
	}
	
	@Override
	public void run()
	{
		while(running) {
			//game.render(secondaryBuffer.getGraphics());
			//game.getGraphics().drawImage(secondaryBuffer, 0, 0, null);
			game.render(game.FrameBuffer.getGraphics());
			game.getGraphics().drawImage(game.FrameBuffer, 0, 0,Main.Frame.getWidth(), Main.Frame.getHeight(), null);
		}

	}
	
	public void kill() {
		running=false;
	}
}
