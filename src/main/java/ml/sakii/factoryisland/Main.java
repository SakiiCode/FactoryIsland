package ml.sakii.factoryisland;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JOptionPane;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;

import ml.sakii.factoryisland.api.API;
import ml.sakii.factoryisland.blocks.ChestModuleBlock;
import ml.sakii.factoryisland.blocks.DrillBlock;
import ml.sakii.factoryisland.blocks.GrassBlock;
import ml.sakii.factoryisland.blocks.LampBlock;
import ml.sakii.factoryisland.blocks.LeafBlock;
import ml.sakii.factoryisland.blocks.ModBlock;
import ml.sakii.factoryisland.blocks.OilBlock;
import ml.sakii.factoryisland.blocks.OldBlock;
import ml.sakii.factoryisland.blocks.SandBlock;
import ml.sakii.factoryisland.blocks.SaplingBlock;
import ml.sakii.factoryisland.blocks.SiliconBlock;
import ml.sakii.factoryisland.blocks.SphereBlock;
import ml.sakii.factoryisland.blocks.StoneBlock;
import ml.sakii.factoryisland.blocks.TankModuleBlock;
import ml.sakii.factoryisland.blocks.TestPowerConsumerBlock;
import ml.sakii.factoryisland.blocks.TestPowerWireBlock;
import ml.sakii.factoryisland.blocks.WaterBlock;
import ml.sakii.factoryisland.blocks.WaterMillBlock;
import ml.sakii.factoryisland.blocks.WoodBlock;
import ml.sakii.factoryisland.items.ItemType;
import ml.sakii.factoryisland.items.PlayerInventory;

public class Main
{
	// ARGUMENTS
	
	public static byte MAJOR, MINOR, REVISION;
	public static boolean verbose = false, headless=false, small = false, nopause=false;

	private static String map, name;
	
	// BLOCKS, ITEMS

	public final static HashMap<String, ItemType> Items = new HashMap<>(20);
	final static ArrayList<String> ModRegistry = new ArrayList<>();
	
	public static long seed;

	static int screen;
	public static int Width=1920;
	public static int Height=1080;
	
	private GUIManager guiManager;

	
	public static void main(String[] args)
	{

		System.setOut(new ProxyPrintStream(System.out, "log.txt"));
        System.setErr(new ProxyPrintStream(System.err, "log.txt"));
        Main.log("---------------------------------------------------");
        
       
		

        
		try(java.io.InputStream is = Main.class.getResourceAsStream("version.properties")){
	        java.util.Properties p = new Properties();
			p.load(is);
	        String[] version = p.getProperty("version","0.0.0").split("\\.");
	        
	        MAJOR = Byte.parseByte(version[0]);
	        MINOR = Byte.parseByte(version[1]);
	        REVISION = Byte.parseByte(version[2]);
		}catch(Exception e) {
			MAJOR = 0;
	        MINOR = 0;
	        REVISION = 0;
	        Main.err(e.getMessage());
	        
		}
		
		List<String> params = Arrays.asList(args);
        for(int i=0;i<params.size();i++) {
        	switch(params.get(i)) {
        	case "-name":name=params.get(i+1); i++;
        		break;
        	case "-map":map=params.get(i+1); i++;
        		break;
        	case "-server":headless=true;
        		break;
        	case "-small":small=true;
        		break;
        	case "-verbose":verbose=true;
    			break;
        	default: Main.err("Unknown launch parameter: "+params.get(i));
        	}
        }
		
		try {
			new Main().start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void start() throws Exception{
        
        if(headless) {
        	Config.username="SERVER";
        	Config.creative=true;
        	
        	LoadResources();
        	if(map==null) {
        		map="server";
        	}
        	File mapFile = new File("saves/"+map+"/map.xml");
        	GameEngine Engine;
        	if(mapFile.exists()) {
        		Engine = new GameEngine(map,null,0,LoadMethod.EXISTING,null);
        	}else {
        		Engine = new GameEngine(map,null,new Random().nextLong(),LoadMethod.GENERATE,null);
            	Engine.afterGen();
        	}
        	API.Engine=Engine;
        	ml.sakii.factoryisland.api.Block.Engine=Engine;
        	Engine.startPhysics();
        	Engine.ticker.start();
        	Main.log("Timers started");
        	String error = Engine.startServer();
        	if(error != null) {
        		Engine.disconnect(error);
        	}else {
	        	Main.log("Setup done");
	        	try(Scanner s = new Scanner(System.in)){
	        	while(s.hasNextLine()) {
	        		String line = s.nextLine();
	        		if(line.trim().equalsIgnoreCase("stop")) {
	        			Main.log("Stopping server...");
	        			Engine.disconnect(null);
	        			break;
	        		}
					Main.err("Unknown command: "+line);
	        	}
	        	}catch(Exception e) {
	        		e.printStackTrace();
	        	}
        	}
        }else {
        
	        
	        if(map != null) {
	        	Config.selectedMap=map;
		        setupWindow(name);

	        	guiManager.SwitchWindow("generate");
	        	guiManager.SPGui.join(false);
				
			}else {
		        setupWindow(name);
			}
        
        }
					

		
	}



	private void setupWindow(String username)
	{
		
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
		GraphicsConfiguration config = d.getDefaultConfiguration();
		Rectangle bounds = config.getBounds();
		Width=bounds.width;
		Height=bounds.height;
		
		
		if(username == null) {


		String username2 = JOptionPane.showInputDialog("Enter username", Config.username);
		Config.username = username2 == null ? Config.username : username2;

		Config.save();
		}else {
			Config.username = username;
		}

		AssetLibrary.load();
    	LoadResources();
    	
    	guiManager = new GUIManager(bounds);
		
		
		Main.log(Config.username + " @ "+ bounds.toString());
	}

	
	
	

	private static void LoadResources()
	{
		

		
		initBlockClass("Water",  WaterBlock.surfaces);
		initBlockClass("Stone",  StoneBlock.surfaces);
		initBlockClass("Old",    OldBlock.surfaces);
		initBlockClass("Grass",  GrassBlock.surfaces);
		initBlockClass("Sand",   SandBlock.surfaces);
		initBlockClass("Oil",    OilBlock.surfaces);
		initBlockClass("Drill",  DrillBlock.surfaces);
		initBlockClass("Wood",   WoodBlock.surfaces);
		initBlockClass("Sapling",SaplingBlock.surfaces);
		initBlockClass("Leaf",   LeafBlock.surfaces);
		initBlockClass("WaterMill",WaterMillBlock.surfaces);
		
		initBlockClass("ChestModule",ChestModuleBlock.surfaces);
		initBlockClass("TankModule",TankModuleBlock.surfaces);

		initBlockClass("Lamp",    LampBlock.surfaces);
		initBlockClass("Silicon", SiliconBlock.surfaces);

		initBlockClass("TestPowerConsumer", TestPowerConsumerBlock.surfaces);
		initBlockClass("TestPowerWire", TestPowerWireBlock.surfaces);
		
		initBlockClass("Sphere", SphereBlock.surfaces);



		File mods = new File("mods");
		if (!mods.exists())
		{
			mods.mkdir();
		} else
		{

			File[] directories = mods.listFiles(new FilenameFilter()
			{
				@Override
				public boolean accept(File current, String name)
				{
					return new File(current, name).isDirectory();
				}
			});
			if(directories.length>0) {
				NashornScriptEngineFactory nashorn = new NashornScriptEngineFactory();
		    	Main.log(nashorn.getEngineName() + " "+nashorn.getEngineVersion());
			}
			for (File mod : directories)
			{
				if (new File(mod, "mod.js").exists())
				{
					String name = mod.getName();
					Main.log("Found mod: " + name);
					ModBlock modBlock;
					try {
						modBlock = new ModBlock(name, 0, 0, 0, null);
					}catch(NullPointerException e) { // nincs nashorn
						Main.err(e.getMessage());
						break;
					}
					ModRegistry.add(name);
					
					Surface[] surfaces = modBlock.surfaces;
					ItemType item=new ItemType(name, name,
							generateIcon(surfaces[0], surfaces[3], surfaces[4]),
							generateViewmodel(surfaces[0], surfaces[3], surfaces[4]));
					
					Main.Items.put(name, item);
					
					PlayerInventory.Creative.add(item, 1, false);
					
					
				}

			}
		}

		File saves = new File("saves");
		if (!saves.exists())
		{
			saves.mkdir();
		}
	}
	
	private static void initBlockClass(String name, Surface[] surfaces) {
		String className = "ml.sakii.factoryisland.blocks." + name + "Block";
		ItemType item=new ItemType(name, className,
				generateIcon(surfaces[0], surfaces[3], surfaces[4]),
				generateViewmodel(surfaces[0], surfaces[3], surfaces[4]));
		
		Main.Items.put(name, item);
		
		PlayerInventory.Creative.add(item, 1, false);
	}
	
	private static BufferedImage generateIcon(Surface topS, Surface southS, Surface eastS)
	{
		int size, s16;
		if(Main.headless) {
			size=1;
			s16=1;
		}else {
			size = (int) (Main.Width * 64f / 1440f);
			s16 = (int) (Main.Width * 16f / 1440f);
		}
		BufferedImage icon = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		Graphics g = icon.createGraphics();
		Point[] p = new Point[]
		{ new Point(0, s16), new Point(s16, 0), new Point(size, 0), new Point(s16 * 3, s16), new Point(0, size),
				new Point(s16 * 3, size), new Point(size, s16 * 3) };
		Polygon top = new Polygon(new int[]
		{ p[0].x, p[1].x, p[2].x, p[3].x }, new int[]
		{ p[0].y, p[1].y, p[2].y, p[3].y }, 4);
		Polygon front = new Polygon(new int[]
		{ p[0].x, p[3].x, p[5].x, p[4].x }, new int[]
		{ p[0].y, p[3].y, p[5].y, p[4].y }, 4);
		Polygon side = new Polygon(new int[]
		{ p[2].x, p[3].x, p[5].x, p[6].x }, new int[]
		{ p[2].y, p[3].y, p[5].y, p[6].y }, 4);

		if (topS.color)
		{ // TOP
			g.setColor(topS.c.getColor());
			g.fillPolygon(top);
		} else
		{
			g.setClip(top);
			Rectangle bounds = top.getBounds();
			g.drawImage(topS.Texture, bounds.x, bounds.y, bounds.width, bounds.height, null);
			g.setClip(null);
		}

		if (southS.color)
		{ // SOUTH
			g.setColor(southS.c.getColor());
			g.fillPolygon(front);
		} else
		{
			g.setClip(front);
			Rectangle bounds = front.getBounds();
			g.drawImage(southS.Texture, bounds.x, bounds.y, bounds.width, bounds.height, null);
			g.setClip(null);
		}

		if (eastS.color)
		{ // EAST
			g.setColor(eastS.c.getColor());
			g.fillPolygon(side);
		} else
		{
			g.setClip(side);
			Rectangle bounds = side.getBounds();
			g.drawImage(eastS.Texture, bounds.x, bounds.y, bounds.width, bounds.height, null);
			g.setClip(null);
		}
		g.setColor(Color.BLACK);
		g.drawPolygon(top);
		g.drawPolygon(side);
		g.drawPolygon(front);
		g.dispose();
		return icon;
	}

	private static BufferedImage generateViewmodel(Surface topS, Surface southS, Surface eastS)
	{

		float res;
		if(Main.headless) {
			res=1;
		}else {
			res = Main.Height / 900f;
		}
		int size = (int) (res * 250);
		BufferedImage icon = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		Graphics g = icon.createGraphics();
		Point[] p = new Point[]
		{ new Point(0, (int) (res * 100)), new Point((int) (res * 90), (int) (res * 50)),
				new Point((int) (res * 200), (int) (res * 20)), new Point((int) (res * 100), (int) (res * 70)),
				new Point((int) (res * 50), (int) (res * 230)), new Point((int) (res * 160), (int) (res * 200)),
				new Point(size, (int) (res * 150)) };
		Polygon top = new Polygon(new int[]
		{ p[0].x, p[1].x, p[2].x, p[3].x }, new int[]
		{ p[0].y, p[1].y, p[2].y, p[3].y }, 4);
		Polygon front = new Polygon(new int[]
		{ p[0].x, p[3].x, p[5].x, p[4].x }, new int[]
		{ p[0].y, p[3].y, p[5].y, p[4].y }, 4);
		Polygon side = new Polygon(new int[]
		{ p[2].x, p[3].x, p[5].x, p[6].x }, new int[]
		{ p[2].y, p[3].y, p[5].y, p[6].y }, 4);

		if (topS.color)
		{ // TOP
			g.setColor(topS.c.getColor());
			g.fillPolygon(top);
		} else
		{
			g.setClip(top);
			Rectangle bounds = top.getBounds();
			g.drawImage(topS.Texture, bounds.x, bounds.y, bounds.width, bounds.height, null);
			g.setClip(null);
		}

		if (southS.color)
		{ // SOUTH
			g.setColor(southS.c.getColor());
			g.fillPolygon(front);
		} else
		{
			g.setClip(front);
			Rectangle bounds = front.getBounds();
			g.drawImage(southS.Texture, bounds.x, bounds.y, bounds.width, bounds.height, null);
			g.setClip(null);
		}

		if (eastS.color)
		{ // EAST
			g.setColor(eastS.c.getColor());
			g.fillPolygon(side);
		} else
		{
			g.setClip(side);
			Rectangle bounds = side.getBounds();
			g.drawImage(eastS.Texture, bounds.x, bounds.y, bounds.width, bounds.height, null);
			g.setClip(null);
		}
		g.setColor(Color.BLACK);
		g.drawPolygon(top);
		g.drawPolygon(side);
		g.drawPolygon(front);
		g.dispose();
		return icon;
	}


	
	
	
	static BufferedImage toCompatibleImage(BufferedImage image)
	{
		if(headless) {
			return image;
		}
	    // obtain the current system graphical settings
		GraphicsConfiguration gfxConfig = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[screen].getDefaultConfiguration();

	    /*
	     * if image is already compatible and optimized for current system 
	     * settings, simply return it
	     */
	    if (image.getColorModel().equals(gfxConfig.getColorModel()))
	        return image;

	    // image is not optimized, so create a new image that is
	    BufferedImage newImage = gfxConfig.createCompatibleImage(
	            image.getWidth(), image.getHeight(), image.getTransparency());

	    // get the graphics context of the new image to draw the old image on
	    Graphics2D g2d = newImage.createGraphics();

	    // actually draw the image and dispose of context no longer needed
	    g2d.drawImage(image, 0, 0, null);
	    g2d.dispose();

	    // return the new optimized image
	    return newImage; 
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
		System.out.println(msg);
	}
	
	public static void err(Object message) {
		String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		String msg = "[" + timeStamp + "] Error: "+message;
			System.err.println(msg);
	}
	
	public static void err(Object[] array) {
			err(Arrays.toString(array));
	}

}
