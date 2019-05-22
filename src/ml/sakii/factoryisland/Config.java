package ml.sakii.factoryisland;

import java.util.Random;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
//import java.util.Random;

public class Config {

	private static Preferences Prefs = Preferences.userRoot().node("FactoryIsland");//.userNodeForPackage(ml.sakii.factoryisland.Main.class);
	public static int renderDistance = Prefs.getInt("renderDistance", 32);
	
	public static int sensitivity = Prefs.getInt("sensitivity", 5);
	public static boolean useTextures = Prefs.getBoolean("useTextures", false);
	public static boolean fogEnabled = Prefs.getBoolean("fogEnabled", false);
	public static String username = Prefs.get("username", "Guest"+new Random().nextInt(100000));
	public static String multiAddr = Prefs.get("multiAddr", "");
	public static int zoom = Prefs.getInt("zoom", 500);
	public static int width = Prefs.getInt("width", 640);
	public static int height = Prefs.getInt("height", 480);
	public static boolean creative=Prefs.getBoolean("creative", true);
	public static String selectedMap=Prefs.get("selectedMap", "");
	public static final int brightness=7;
	
	public static void save(){
		Prefs.putInt("renderDistance",	renderDistance);
		Prefs.putInt("sensitivity", sensitivity);
		Prefs.putInt("zoom", zoom);
		Prefs.putInt("width", width);
		Prefs.putInt("height", height);
		//Prefs.putInt("brightness", brightness);
		
		Prefs.putBoolean("useTextures", useTextures);
		Prefs.putBoolean("fogEnabled", fogEnabled);
		Prefs.putBoolean("creative", creative);
		/*Prefs.putBoolean("skyEnabled", skyEnabled);
		Prefs.putBoolean("fastQuality", fastQuality);*/
		/*if(username.equals("Guest")) {
			Main.log("guest string");
			Prefs.put("username", "Guest"+new Random().nextInt(100000));
		}else {
			Prefs.put("username", username);
		}*/
		//Prefs.put("username", username.equals("Guest") ? "Guest"+new Random().nextInt(100000) : username);
		Prefs.put("username", username);
		Prefs.put("multiAddr", multiAddr);
		Prefs.put("selectedMap", selectedMap);
		
		
		
		if(Main.GAME != null) {
			Main.GAME.resizeScreen(Config.width, Config.height);
			//Main.GAME.resizeScreen();
			//Main.GAME.Engine.world.remapLight();
			/*if(!Main.GAME.creative && creative)
				Main.GAME.Engine.switchToCreative();
			else if(Main.GAME.creative && !creative)
				Main.GAME.Engine.switchFromCreative();*/
		}
		//Prefs.putInt("viewportscale", viewportscale);
		
		
		/*if(Main.GAME != null){
			Main.GAME.DepthBuffer = new double[Main.Frame.getWidth()/Config.viewportscale][Main.Frame.getHeight()/Config.viewportscale];
			Main.GAME.FrameBuffer = new BufferedImage(Main.Frame.getWidth()/Config.viewportscale, Main.Frame.getHeight()/Config.viewportscale, BufferedImage.TYPE_INT_ARGB); 
			
		}*/
		
		
	}

	static void reset()
	{
		try
		{
			Prefs.clear();
			renderDistance = Prefs.getInt("renderDistance", 32);
			
			sensitivity = Prefs.getInt("sensitivity", 5);
			useTextures = Prefs.getBoolean("useTextures", false);
			fogEnabled = Prefs.getBoolean("fogEnabled", false);
			username = Prefs.get("username", "Guest"+new Random().nextInt(100000));
			multiAddr = Prefs.get("multiAddr", "");
			zoom = Prefs.getInt("zoom", 500);
			width = Prefs.getInt("width", 640);
			height = Prefs.getInt("height", 480);
			creative=Prefs.getBoolean("creative", true);
			selectedMap=Prefs.get("selectedMap", "");
		} catch (BackingStoreException e)
		{
			e.printStackTrace();
		}
	}
		
	
		
}
