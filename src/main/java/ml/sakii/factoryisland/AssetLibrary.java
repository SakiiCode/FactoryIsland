package ml.sakii.factoryisland;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import javax.imageio.ImageIO;

public class AssetLibrary {
	public static BufferedImage drillSide;
	public static Color4 drillGradientBeginColor, drillSideColor, chestModule, tankModule;
	public static Surface fire;
	public static Surface lamp;
	public static Surface stone, grass, dirt, sand, playerSide, playerFront, wood, leaf, sapling, saplingTop,
	alienFront, alienSide;

	public static Surface sphere, randomSphere;

	public static Surface[] waters, oils;
	public static Color4 wmSideColor, wmGradientBeginColor, wmPoweredColor;
	private static AudioThread BGMusic;
	
	static Color skyColor = Color.BLACK;
	
	public static BufferedImage GUIBG, Logo, MainMenuBG;
	public static BufferedImage StandardBG, FreezeBG;
	public static BufferedImage PausedBG, PausedTitle;
	public static BufferedImage SettingsTitle;
	
	
	public static void load() {
		GUIBG = loadTexture("textures/guibg.png");
		PausedBG = loadTexture("textures/paused.png");

		SettingsTitle = loadTexture("textures/settings_title.png");
		PausedTitle = loadTexture("textures/paused_title.png");
		StandardBG = loadTexture("textures/BG.png");
		Logo = loadTexture("textures/logo.png");
		MainMenuBG = loadTexture("textures/mainmenu.png");

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
		chestModule = new Color4(85, 85, 85);
		tankModule = new Color4(255, 153, 0);
		playerSide = new Surface(Color.BLUE);
		playerFront = new Surface(Color.RED);
		alienSide = new Surface(Color.GREEN.brighter());
		alienFront = new Surface(Color.GREEN.darker().darker());
		drillSide = loadTexture("textures/blocks/drill_side5.png");
		drillSideColor = Surface.averageColor(drillSide);
		drillGradientBeginColor = new Color4(200, 70, 60, 255);

		lamp = new Surface(new Color(240, 220, 170));
		sphere = new Surface(loadTexture("textures/blocks/sphere2.png"));
		randomSphere = new Surface(loadTexture("textures/blocks/sphere3.png"));

		wmSideColor = new Color4(200, 200, 255);
		wmGradientBeginColor = new Color4(20, 20, 70);
		fire = new Surface(new Color(255, 153, 0));
		wmPoweredColor = new Color4(255,218,0);
		
		
	}
	
	
	public static void playBgMusic() {
		BGMusic = new AudioThread();
		BGMusic.start();
	}

	
	public static void stopBgMusic() {
		BGMusic.kill();
	}
	
	private static BufferedImage loadTexture(String path)
	{
		if(Main.headless) {
			return newEmptyImage();
		}
		BufferedImage image;
		try(InputStream imageStream = Main.class.getResourceAsStream(path))
		{
			image = ImageIO.read(imageStream);

		} catch (Exception e)
		{
			image = newEmptyImage();
			Main.log("Could not load texture from '" + path + "': " + e.getMessage());

		}
		return Main.toCompatibleImage(image);
	}
	
	private static BufferedImage newEmptyImage() {
		BufferedImage i = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
		Graphics ig = i.createGraphics();
		ig.setColor(Color.WHITE);
		ig.fillRect(0, 0, 1, 1);
		ig.dispose();
		return i;
	}
}
