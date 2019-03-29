package ml.sakii.factoryisland;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import ml.sakii.factoryisland.blocks.ModBlock;
import ml.sakii.factoryisland.items.Item;
import ml.sakii.factoryisland.items.ItemStack;
import ml.sakii.factoryisland.items.PlayerInventory;

public class Main
{

	public final static byte MAJOR = 0;
	public final static byte MINOR = 8;
	public final static byte REVISION = 5;
	
	public static boolean devmode = false, nopause = false;
	public static Color drillGradientBeginColor = new Color(100, 40, 40, 200);
	public static BufferedImage drillSide;
	public static Color drillSideColor, drillFrontColor, chestModule, tankModule;
	public static Surface fire;

	public final static JFrame Frame = new JFrame();
	public static Game GAME;
	/*
	 * public static HashMap<String, String> BlockRegistry = new HashMap<>(); public
	 * static HashMap<String, BufferedImage> ItemTextures = new HashMap<>(); public
	 * static HashMap<String, BufferedImage> ViewmodelTextures = new HashMap<>();
	 */
	public static final HashMap<String, Item> Items = new HashMap<>(11);

	// public static byte MMframeCount = 0;

	public static Surface lamp;
	public static final ArrayList<String> ModRegistry = new ArrayList<>();

	public final static int MP_PACKET_EACH = 300;
	public static Surface stone, grass, dirt, sand, playerSide, playerFront, wood, leaf, sapling, saplingTop,
			alienFront, alienSide;
	public final static float TICKSPEED = 0.05f;
	public final static int PHYSICS_FPS = 10;
	public static final Color TRANSPARENT = new Color(0, 0, 0, 0);

	public static Surface[] waters, oils;
	public static Color wmSideColor, wmGradientBeginColor, wmPoweredColor;
	static final JPanel Base = new JPanel(new CardLayout());
	static Clip BGMusic;

	static boolean focused = true;
	static BufferedImage GUIBG;
	static BufferedImage Logo;
	static BufferedImage MainMenuBG, PausedBG, SettingsBG;
	static BufferedImage MenuButtonTexture;
	static ArrayList<String> Mods = new ArrayList<>();

	static String PreviousCLCard = "";
	static long seed;

	// public static AffineTransform at;

	static Color skyColor = Color.BLACK;// new Color(161, 191, 217);
	static boolean sound = true;
	private static AudioInputStream BGAudioStream;

	private static String CurrentCLCard = "";

	
	
	private static MultiplayerGUI MPGui;

	

	private static int screen;
	static FileOutputStream logStream;
	
	
	public static void main(String[] args) throws Exception
	{
		try
		{
			logStream=new FileOutputStream("log.txt", true);
			if(!devmode) { // ha nincs debug, a fájlba írja a kivételeket
				PrintStream out = new PrintStream(logStream);
				System.setErr(out);
			}
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		
		javax.swing.SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				setupWindow();
			}
		});
		

		
	}

	public static void setupWindow()
	{

		if (Config.username.equals("Guest"))
		{
			//Config.Prefs.remove("username");
			Config.username = "Guest" + new Random().nextInt(100000);
		}

		String username = JOptionPane.showInputDialog("Enter username", Config.username);
		Config.username = username == null ? Config.username : username;

		Config.save();

		GraphicsDevice[] gs = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		String[] resolutions = new String[gs.length];

		for (int i = 0; i < gs.length; i++)
		{
			resolutions[i] = "<html><body><div align='center'>Screen " + i + "<br>"
					+ gs[i].getDefaultConfiguration().getBounds().width + "x"
					+ gs[i].getDefaultConfiguration().getBounds().height + "</div></body></html>";
		}
		if (gs.length > 1)
			screen = JOptionPane.showOptionDialog(null, "Please select a display", "Please select a display",
					JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, resolutions, null);

		if (screen < 0)
			screen = 0;

		GraphicsDevice d = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[screen];

		Frame.setUndecorated(true);

		//Frame.setExtendedState(java.awt.Frame.NORMAL);
		Rectangle bounds = d.getDefaultConfiguration().getBounds();
		if (System.getProperty("os.name").toLowerCase().contains("win"))
		{
			Frame.setBounds(bounds);

			
		} else // a linux egy bug miatt mindig kiteszi egy bizonyos kï¿½pernyï¿½re
		{
			d.setFullScreenWindow(Frame);
		}

		

		Frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		Frame.setTitle("FactoryIsland " + MAJOR + "." + MINOR + "." + REVISION);

		LoadResources();

		MPGui = new MultiplayerGUI();
		Base.add(new SingleplayerGUI(), "generate");
		Base.add(MPGui, "connect");
		Base.add(new MainMenu(), "mainmenu");
		Base.add(new SettingsGUI(), "settings");
		Base.add(new PauseGUI(), "pause");

		Frame.add(Base);
		
		Frame.addWindowListener(new WindowListener()
		{

			@Override
			public void windowActivated(WindowEvent e)
			{
				focused = true;
				if (GAME != null)
				{
					GAME.centered = false;
				}
				Main.log("focused:"+focused);
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
					GAME.disconnect();
				}
				try
				{
					logStream.close();
				} catch (IOException e1)
				{
					e1.printStackTrace();
				}
			}

			@Override
			public void windowDeactivated(WindowEvent e)
			{
				focused = false;
						
				Main.log("focused:"+focused);
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
		Main.log(Config.username + " @ "+ Frame.getBounds().toString());


	}

	public static boolean joinServer(String IP, JLabel statusLabel)
	{
		if (sound)
			BGMusic.stop();
		GAME = new Game(IP, 0, LoadMethod.MULTIPLAYER, statusLabel);

		if (GAME.error == null)
		{
			statusLabel.setText("Opening game screen...");			

			openGame();
			return true;
		}
		
		Main.err(GAME.error);
		statusLabel.setText("Error:"+GAME.error);
		return false;
		
	}

	public static boolean launchWorld(String mapName, boolean generate, JLabel statusLabel)
	{
		if (sound)
			BGMusic.stop();
		if (generate)
		{
			GAME = new Game(mapName, seed, LoadMethod.GENERATE, statusLabel);
			if(GAME.error==null) {
				statusLabel.setText("Post-worldgen");
				GAME.Engine.afterGen();
			}else {
				Main.err(GAME.error);
				statusLabel.setText("<html>Error:"+GAME.error+"</html>");
				GAME=null;
				return false;
			}
		} else
		{
			GAME = new Game(mapName, 0, LoadMethod.EXISTING, statusLabel);
			if(GAME.error != null) {
				Main.err(GAME.error);
				statusLabel.setText("<html>Error:"+GAME.error+"</html>");
				GAME=null;
				return false;
			}
		}
		statusLabel.setText("Opening game screen...");

		openGame();
		return true;
	}

	private static void openGame() {
		Main.log("Game setup done.");
		GAME.Engine.ticker.start();
		GAME.Engine.startPhysics();
		
		
		//focused = true;
		
		Main.log("Switching to game window...");
		
		//GAME.repaint();
		
		Base.add(GAME, "game");
		SwitchWindow("game");
		GAME.renderThread.start();
	}
	
	public static void SwitchWindow(String To)
	{

		if (CurrentCLCard.equals("pause") && To.equals("mainmenu") && sound)
		{
			try
			{
				if (!BGMusic.isOpen())
				{
					BGMusic.open(BGAudioStream);
					BGMusic.start();
				}
			} catch (LineUnavailableException | IOException e)
			{
				Main.log("Could not load main menu music: " + e.getMessage());
			}

		}
			((CardLayout) (Main.Base.getLayout())).show(Base, To);

		PreviousCLCard = CurrentCLCard;
		CurrentCLCard = To;

	}

	private static void LoadResources()
	{
		GUIBG = loadTexture("textures/stone.png");
		Logo = loadTexture("textures/logo.png");
		MainMenuBG = loadTexture("textures/mainmenu.png");
		PausedBG = loadTexture("textures/paused.png");
		SettingsBG = loadTexture("textures/settings.png");

		MenuButtonTexture = loadTexture("textures/button.png");

		stone = new Surface(loadTexture("textures/blocks/stone.png"));
		waters = new Surface[]
		{ null, new Surface(loadTexture("textures/blocks/water_1.png")),
				new Surface(loadTexture("textures/blocks/water_2.png")),
				new Surface(loadTexture("textures/blocks/water_3.png")),
				new Surface(loadTexture("textures/blocks/water_4.png")) };
		oils = new Surface[]
		{ null, new Surface(new Color(80, 80, 80)), new Surface(new Color(60, 60, 60)),
				new Surface(new Color(35, 35, 35)) };
		grass = new Surface(loadTexture("textures/blocks/grass.png"));
		dirt = new Surface(loadTexture("textures/blocks/dirt.png"));
		sand = new Surface(loadTexture("textures/blocks/sand.png"));
		wood = new Surface(loadTexture("textures/blocks/wood2.png"));
		leaf = new Surface(loadTexture("textures/blocks/leaf3.png"));
		sapling = new Surface(new Color(150, 100, 50).darker());
		saplingTop = new Surface(Color.GREEN);
		chestModule = new Color(85, 85, 85);
		tankModule = new Color(255, 153, 0);
		playerSide = new Surface(Color.BLUE);
		playerFront = new Surface(Color.RED);
		alienSide = new Surface(Color.GREEN.brighter());
		alienFront = new Surface(Color.GREEN.darker().darker());
		drillSide = loadTexture("textures/blocks/drill_side5.png");
		drillSideColor = Surface.averageColor(drillSide);
		//Color4 tmp = new Color4(drillGradientBeginColor);
		drillFrontColor = drillSideColor.darker().darker();//tmp.blend(drillSideColor);
		lamp = new Surface(new Color(240, 220, 170));

		wmSideColor = new Color(200, 200, 255);
		wmGradientBeginColor = new Color(20, 20, 70);
		fire = new Surface(new Color(255, 153, 0));
		wmPoweredColor = fire.c.brighter();

		BufferedReader reader = new BufferedReader(
				new InputStreamReader(Main.class.getResourceAsStream("blocks/BlockRegistry.txt")));

		String line = "";
		try
		{
			while ((line = reader.readLine()) != null)
			{

				GameEngine.nullBlock("ml.sakii.factoryisland.blocks." + line + "Block");
				PlayerInventory.Creative.items.add(new ItemStack(Items.get(line), 1));
			}
			PlayerInventory.Creative.hotbarIndex=0;
			PlayerInventory.Creative.SelectedStack=PlayerInventory.Creative.items.get(0);
		} catch (IOException e1)
		{
			e1.printStackTrace();
		}

		File mods = new File("mods");
		if (!mods.exists())
		{
			mods.mkdir();
		} else
		{
			/*
			 * File[] listOfFiles = mods.listFiles(); if(listOfFiles != null) { for(File mod
			 * : listOfFiles) { String[] parts = mod.getName().split("\\.");
			 * if(parts[parts.length-1].equals("js")) {
			 * 
			 * @SuppressWarnings("unused") ModBlock modBlock = new ModBlock(parts[0], 0, 0,
			 * 0, null); } } }
			 */
			// File file = new File("mods");
			File[] directories = mods.listFiles(new FilenameFilter()
			{
				@Override
				public boolean accept(File current, String name)
				{
					return new File(current, name).isDirectory();
				}
			});
			for (File mod : directories)
			{
				if (new File(mod, "mod.js").exists())
				{
					Main.log("Found mod: " + mod.getName());
					@SuppressWarnings("unused")
					ModBlock modBlock = new ModBlock(mod.getName(), 0, 0, 0, null);
					Mods.add(mod.getName());
				}

			}
			// Main.log(Arrays.toString(directories));
		}

		File saves = new File("saves");
		if (!saves.exists())
		{
			saves.mkdir();
		}

		try
		{

			BufferedInputStream inputStream = new BufferedInputStream(
					Main.class.getResourceAsStream("sounds/Zongora.wav"));
			BGAudioStream = AudioSystem.getAudioInputStream(inputStream);

			BGMusic = AudioSystem.getClip();
			BGMusic.open(BGAudioStream);
			sound = true;
		} catch (Exception e)
		{
			Main.log("Could not load sounds: " + e.getMessage());
			sound = false;
		}
	}

	private static BufferedImage loadTexture(String path)
	{
		BufferedImage image;
		try
		{
			image = ImageIO.read(Main.class.getResourceAsStream(path));

			BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(),
					BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = newImage.createGraphics();
			g.drawImage(image, 0, 0, null);
			g.dispose();
			return newImage;
		} catch (Exception e)
		{
			image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
			Graphics ig = image.createGraphics();
			ig.setColor(Color.WHITE);
			ig.fillRect(0, 0, 1, 1);
			ig.dispose();
			Main.log("Could not load texture from '" + path + "': " + e.getMessage());

		}
		return image;
	}
	
	static BufferedImage deepCopy(BufferedImage bi) {
		 ColorModel cm = bi.getColorModel();
		 boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		 WritableRaster raster = bi.copyData(null);
		 return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
		}
	
	public static void log(Object message) {
		String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		String msg = "[" + timeStamp + "] INFO: "+message;
		try
		{
			logStream.write((msg+"\r\n").getBytes());
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		System.out.println(msg);
	}
	
	public static void err(Object message) {
		try
		{
			logStream.write(("ERROR:"+message+"\r\n").getBytes());
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		if(devmode) { // nem debug esetén ez már át van irányítva ugyanabba a fájlba
			System.err.println(message);
		}
	}

}
