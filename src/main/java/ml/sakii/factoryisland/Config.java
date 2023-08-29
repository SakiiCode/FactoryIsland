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
	public static int FOV = Prefs.getInt("FOV", 90);
	public static float resolutionScaling = Prefs.getFloat("resolutionScaling", 1f);
	public static boolean creative=Prefs.getBoolean("creative", true);
	public static String selectedMap=Prefs.get("selectedMap", "");
	public static final int brightness=7;
	public static RenderMethod renderMethod = RenderMethod.values()[Prefs.getInt("renderMethod", 1)];
	public static TargetMarkerType targetMarkerType = TargetMarkerType.values()[Prefs.getInt("targetMarkerType", 0)];
	public static boolean ambientOcclusion = Prefs.getBoolean("ambientOcclusion", true);
	
	private static int zoom;
	
	public static void save(){
		Prefs.putInt("renderDistance",	renderDistance);
		Prefs.putInt("sensitivity", sensitivity);
		Prefs.putInt("FOV", FOV);
		Prefs.putFloat("resolutionScaling", resolutionScaling);
		
		Prefs.putBoolean("useTextures", useTextures);
		Prefs.putBoolean("fogEnabled", fogEnabled);
		Prefs.putBoolean("creative", creative);
		Prefs.putInt("renderMethod", renderMethod.id);
		Prefs.putInt("targetMarkerType", targetMarkerType.id);
		Prefs.put("username", username);
		Prefs.put("multiAddr", multiAddr);
		Prefs.put("selectedMap", selectedMap);
		Prefs.putBoolean("ambientOcclusion", ambientOcclusion);
		zoom=FOVToZoom(FOV);
		
	}

	public static void reset()
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
			FOV = Prefs.getInt("FOV", 90);
			resolutionScaling=Prefs.getFloat("resolutionScaling", 1f);
			creative=Prefs.getBoolean("creative", true);
			selectedMap=Prefs.get("selectedMap", "");
			renderMethod = RenderMethod.values()[Prefs.getInt("renderMethod", 1)];
			targetMarkerType = TargetMarkerType.values()[Prefs.getInt("targetMarkerType", 0)];
			ambientOcclusion = Prefs.getBoolean("ambientOcclusion", true);
			save();
		} catch (BackingStoreException e)
		{
			e.printStackTrace();
		}
	}
	
	public static int FOVToZoom(double FOV) {
		return (int)(Config.getHeight()/2f/Math.tan(Math.toRadians(FOV/2)));
	}
	
	public static int getZoom() {
		return zoom;
	}
	
	public static int getWidth() {
		return (int)(Main.Width*resolutionScaling); 
	}
	
	public static int getHeight() {
		return (int)(Main.Height*resolutionScaling);
	}
	
	public static float getHorizontalFOV() {
		return (float) (2*Math.atan(getWidth()/2/zoom));
	}
	
	public static float getDiagonalFOV() {
		return (float) (2*Math.atan2(Math.sqrt(getWidth()*getWidth()+getHeight()*getHeight())/2,zoom));
	}
		
	
		
}

