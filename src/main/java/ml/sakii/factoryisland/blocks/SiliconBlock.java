package ml.sakii.factoryisland.blocks;

import java.util.HashSet;

import ml.sakii.factoryisland.Color4;
import ml.sakii.factoryisland.Game;
import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Object3D;
import ml.sakii.factoryisland.Polygon3D;
import ml.sakii.factoryisland.Surface;
import ml.sakii.factoryisland.Vector;
import ml.sakii.factoryisland.entities.PlayerMP;

public class SiliconBlock extends Block implements DayNightListener, PowerGenerator, TextureListener, LoadListener{

	
	public static Surface[] surfaces =new Surface[] {
			new Surface(new Color4(0.2f,0.2f,0.2f,1f)),
			new Surface(new Color4(0.8f,0.8f,0.8f,1f)),
			new Surface(new Color4(0.5f,0.5f,0.5f,1f)),
			new Surface(new Color4(0.3f,0.3f,0.3f,1f)),
			new Surface(new Color4(0.9f,0.9f,0.9f,1f)),
			
		};
	
	private HashSet<PowerConsumer> consumerCache=new HashSet<>();
	
	
	public SiliconBlock(int x, int y, int z, GameEngine engine) {
		super("Silicon", x, y, z,0.9f, 0.9f, 0.9f, engine);
		
	}

	@Override
	public Surface[] getSurfaces() {
		return Block.generateSurfaces(new Color4(125, 125, 125, 255));
	}

	@Override
	public void onDay() {
		setPower(20);
		
	}

	@Override
	public void onNight() {
		setPower(0);
		
	}

	@Override
	public HashSet<PowerConsumer> getConsumerCache() {
		return consumerCache;
	}

	@Override
	public void updateTexture(Vector tmp, Game game) {
		PlayerMP PE = game.PE;
		for(Object3D o : Objects) {
			if(o instanceof Polygon3D p) {
				float brightness = Math.abs(tmp.set(p.getCentroid()).substract(PE.ViewFrom).normalize().DotProduct(p.getNormal()));
				p.s.c.set(brightness,brightness,brightness,1f);//) = new Surface(new Color4(brightness,brightness,brightness,1f));
				p.recalcLightedColor();
			}
		}
		
	}



	@Override
	public float getDesiredPower() {
		if(Engine.isDay(z)) {
			return 20;
		}else {
			return 0;
		}
	}

	@Override
	public void onLoad(Game game) {
		updateTexture(new Vector(), game);
	}

}
