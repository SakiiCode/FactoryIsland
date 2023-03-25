package ml.sakii.factoryisland;

import java.awt.CardLayout;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferStrategy;
import java.util.function.Consumer;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import ml.sakii.factoryisland.screens.BenchmarkGUI;
import ml.sakii.factoryisland.screens.DeadGUI;
import ml.sakii.factoryisland.screens.MainMenuGUI;
import ml.sakii.factoryisland.screens.MultiplayerGUI;
import ml.sakii.factoryisland.screens.PauseGUI;
import ml.sakii.factoryisland.screens.SettingsGUI;
import ml.sakii.factoryisland.screens.SingleplayerGUI;

public class GUIManager {
	private JFrame Frame;
	public Game GAME;
	private JPanel Base;
	

	private MultiplayerGUI MPGui;
	private PauseGUI pauseGui;
	SingleplayerGUI SPGui;
	private BenchmarkGUI BMGui;
	
	private String CurrentCLCard = "";
	private String PreviousCLCard = "";
	
	
	
	public GUIManager(Rectangle bounds) {
		Frame= new JFrame();
		Frame.setUndecorated(true);
		


		
		if (System.getProperty("os.name").toLowerCase().contains("win"))
		{
			Frame.setBounds(bounds);

			
		} else // linuxon a talcat es stb nem lehet eltakarni
		{
			GraphicsDevice d = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[Main.screen];
			d.setFullScreenWindow(Frame);
		}

		

		Frame.setTitle("FactoryIsland " + Main.MAJOR + "." + Main.MINOR + "." + Main.REVISION);

		Base = new JPanel(new CardLayout());
		MPGui = new MultiplayerGUI(this);
		SPGui = new SingleplayerGUI(this);
		Base.add(SPGui, "generate");
		Base.add(new BenchmarkGUI(this), "benchmark");
		Base.add(MPGui, "connect");
		Base.add(new MainMenuGUI(this), "mainmenu");
		Base.add(new SettingsGUI(this), "settings");
		pauseGui= new PauseGUI(this);
		Base.add(pauseGui, "pause");
		Base.add(new DeadGUI(this),"died");
		BMGui = new BenchmarkGUI(this);

		Frame.add(Base);
		
		Frame.addWindowListener(new WindowListener()
		{

			@Override
			public void windowActivated(WindowEvent e)
			{
				if (GAME != null)
				{
					GAME.centered = false;
				}
			}

			@Override
			public void windowClosed(WindowEvent e)
			{
				
			}

			@Override
			public void windowClosing(WindowEvent e)
			{
				if (GAME != null)
				{
					GAME.disconnect(null);
				}
				AssetLibrary.stopBgMusic();
				Frame.dispose();

			}

			@Override
			public void windowDeactivated(WindowEvent e)
			{
			}

			@Override
			public void windowDeiconified(WindowEvent e)
			{
			}

			@Override
			public void windowIconified(WindowEvent e)
			{
	
			}

			@Override
			public void windowOpened(WindowEvent e)
			{
			}

		});



		SwitchWindow("mainmenu");
		
		Frame.setVisible(true);
	}
	
	public void SwitchWindow(String To)
	{

		if ((CurrentCLCard.equals("pause") && To.equals("mainmenu")) || CurrentCLCard.equals(""))
		{
			AssetLibrary.playBgMusic();
		}else if(To.equals("game")) {
			AssetLibrary.stopBgMusic();
		}
		((CardLayout) (Base.getLayout())).show(Base, To);
		PreviousCLCard = CurrentCLCard;
		CurrentCLCard = To;

	}
	
	public void SwitchBack() {
		SwitchWindow(PreviousCLCard);
	}
	
	public boolean joinServer(String IP, JLabel statusLabel)
	{
		GAME = new Game(IP, 0, LoadMethod.MULTIPLAYER, this, MPGui.updateFunction);

		if (GAME.error == null)
		{
			statusLabel.setText("Opening game screen...");			

			openGame();
			return true;
		}
		
		Main.err(GAME.error);
		statusLabel.setText("<html>Error: "+GAME.error+"</html>");
		JOptionPane.showMessageDialog(Frame, GAME.error);
		return false;
		
	}

	public boolean launchWorld(String mapName, boolean generate, Consumer<String> update)
	{
		if (generate)
		{
			GAME = new Game(mapName, Main.seed, LoadMethod.GENERATE, this, update);
			if(GAME.error==null) {
				update.accept("Executing post-worldgen instructions...");
				GAME.Engine.afterGen();
			}else {
				Main.err(GAME.error);
				update.accept("<html>Error: "+GAME.error+"</html>");
				GAME=null;
				return false;
			}
		} else
		{
			GAME = new Game(mapName, 0, LoadMethod.EXISTING, this, update);
			if(GAME.error != null) {
				Main.err(GAME.error);
				update.accept("<html>Error: "+GAME.error+"</html>");
				GAME=null;
				return false;
			}
		}
		update.accept("Opening game screen...");

		openGame();
		return true;
	}
	
	
	public boolean runBenchmark(String mapName) {
		if(GAME != null) {
			Main.err("Already joined a game");
			return false;
		}
		
		GAME = new Game(mapName, 0, LoadMethod.BENCHMARK, this, BMGui.updateFunction);
		
		if(GAME.error != null) {
			Main.err(GAME.error);
			BMGui.updateFunction.accept("<html>Error: "+GAME.error+"</html>");
			GAME=null;
			return false;
		}
		
		BMGui.updateFunction.accept("Opening game screen...");

		openGame();
		return true;
		
	}

	private void openGame() {
		Base.add(GAME, "game");
		GAME.start();
		
	}
	
	public void closeGame() {
		SwitchWindow("mainmenu");
		Base.remove(GAME);
		GAME = null;
	}
	
	public static void showMessageDialog(String title, String message, int icon) {
		JOptionPane.showMessageDialog(null, title, message, icon);
	}
	
	public static int showConfirmDialog(String title, String message, int optionType) {
		return JOptionPane.showConfirmDialog(null, title, message, optionType);
	}
	
	public int getX() {
		return Frame.getX();
	}
	public int getY() {
		return Frame.getY();
	}
	
	public boolean isActive() {
		return Frame.isActive();
	}
	
	public BufferStrategy createBufferStrategy() {
		Frame.createBufferStrategy(2);
		return Frame.getBufferStrategy();
	}
	
	public void exit() {
		Frame.dispatchEvent(new WindowEvent(Frame, WindowEvent.WINDOW_CLOSING));
	}

}
