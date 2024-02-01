package ml.sakii.factoryisland;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;

public class Config {

	private static final String CONFIG_FILENAME = "config.properties";
	private static Properties props = new Properties();
	
	public static int renderDistance;
	public static int sensitivity;
	public static boolean useTextures;
	public static boolean fogEnabled;
	public static String username;
	public static String multiAddr;
	public static int FOV;
	public static float resolutionScaling;
	public static boolean creative;
	public static String selectedMap;
	public static int brightness;
	public static RenderMethod renderMethod;
	public static TargetMarkerType targetMarkerType;
	public static boolean ambientOcclusion;
	
	private static int zoom;
	
	public static void save(){
		put("renderDistance",	renderDistance);
		put("sensitivity", sensitivity);
		put("FOV", FOV);
		put("resolutionScaling", resolutionScaling);
		
		put("useTextures", useTextures);
		put("fogEnabled", fogEnabled);
		put("creative", creative);
		put("renderMethod", renderMethod.id);
		put("targetMarkerType", targetMarkerType.id);
		put("username", username);
		put("multiAddr", multiAddr);
		put("selectedMap", selectedMap);
		put("ambientOcclusion", ambientOcclusion);
		zoom=FOVToZoom(FOV);
		try(FileOutputStream outputStream = new FileOutputStream(CONFIG_FILENAME)){
			props.store(outputStream, "Factory Island Settings");	
		}catch(IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	public static void load() {
		boolean win = System.getProperty("os.name").toLowerCase().contains("win");

		renderDistance = getInt("renderDistance", 128);
		sensitivity = getInt("sensitivity", 5);
		useTextures = getBoolean("useTextures", false);
		fogEnabled = getBoolean("fogEnabled", false);
		username = get("username", "Guest"+new Random().nextInt(100000));
		multiAddr = get("multiAddr", "");
		FOV = getInt("FOV", 90);
		resolutionScaling=getFloat("resolutionScaling", 1f);
		creative=getBoolean("creative", true);
		selectedMap=get("selectedMap", "");
		brightness=7;
		renderMethod = RenderMethod.values()[getInt("renderMethod", win?2:1)];
		targetMarkerType = TargetMarkerType.values()[getInt("targetMarkerType", 0)];
		ambientOcclusion = getBoolean("ambientOcclusion", true);
		zoom=FOVToZoom(FOV);
	}

	public static void reset()
	{
		props.clear();
	}
	
	static {
		File f = new File(CONFIG_FILENAME);
		if(!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try (InputStream file = new FileInputStream(f)) {
			props.load(file);
		}catch(IOException e) {
			e.printStackTrace();
		}
		load();
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
		
	
	@SuppressWarnings("unchecked")
	private static <T> T getObject(Class<T> type, String key, T defaultValue){
		String value = props.getProperty(key, defaultValue.toString());
		switch(type.getSimpleName()) {
		case "Boolean": return (T) Boolean.valueOf(value);
		case "Float": return (T) Float.valueOf(value);
		case "Integer": return (T) Integer.valueOf(value);
		case "String": return (T) value;
		default: throw new ClassCastException("Could not recognize type " + type.getName());
		}
	}
	
	private static int getInt(String key, int defaultValue) {
		return getObject(Integer.class, key, defaultValue);
	}
	
	private static boolean getBoolean(String key, boolean defaultValue) {
		return getObject(Boolean.class, key, defaultValue);
	}
	
	private static String get(String key, String defaultValue) {
		return getObject(String.class, key, defaultValue);
	}
	
	private static float getFloat(String key, float defaultValue) {
		return getObject(Float.class, key, defaultValue);
	}
	
	
	private static void put(String key, Object value){
		props.put(key,value.toString());
	}
	
		
}

