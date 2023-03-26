package ml.sakii.factoryisland.blocks;

import ml.sakii.factoryisland.AssetLibrary;
import ml.sakii.factoryisland.Color4;
import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Sphere3D;
import ml.sakii.factoryisland.Surface;
import ml.sakii.factoryisland.Vector;

public class SphereBlock extends Block{
		

		public static Surface[] surfaces = Block.generateSurfacesCopy(AssetLibrary.sphere);
		
		private Sphere3D sphere;
		
		public SphereBlock(int x, int y, int z, GameEngine engine){
			super("Sphere",x, y, z,engine);
			sphere = new Sphere3D(new Vector(x,y,z), 7, 20, new Surface(new Color4(1f,0f,1f,0.5f)), this);
			
			Objects.add(sphere);
		}

		@Override
		public Surface[] getSurfaces() {
			return surfaces;
		}
		
}
