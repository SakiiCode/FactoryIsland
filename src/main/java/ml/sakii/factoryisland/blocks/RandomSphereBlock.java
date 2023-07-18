package ml.sakii.factoryisland.blocks;

import ml.sakii.factoryisland.AssetLibrary;
import ml.sakii.factoryisland.Color4;
import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Sphere3D;
import ml.sakii.factoryisland.Surface;
import ml.sakii.factoryisland.Vector;

public class RandomSphereBlock extends Block{
		

	private static Surface[] surfaces = Block.generateSurfacesCopy(AssetLibrary.randomSphere);
	
	private Sphere3D sphere;
	
	public RandomSphereBlock(int x, int y, int z, GameEngine engine){
		super("RandomSphere",x, y, z,engine);
		sphere = new Sphere3D(new Vector(x,y,z), (int)(Math.random()*8)+2, 20, new Surface(new Color4((float)Math.random(),(float)Math.random(),(float)Math.random(),(float)(Math.random()*0.8f+0.2f))), this);
		
		Objects.add(sphere);
	}

	@Override
	public Surface[] getSurfaces() {
		return surfaces;
	}
		
}
