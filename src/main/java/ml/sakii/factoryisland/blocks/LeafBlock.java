package ml.sakii.factoryisland.blocks;

import java.util.Random;

import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Main;
import ml.sakii.factoryisland.Surface;
import ml.sakii.factoryisland.entities.Entity;
import ml.sakii.factoryisland.entities.PlayerMP;

public class LeafBlock extends Block implements BreakListener{
	
	public static Surface[] surfaces = new Surface[] { Main.leaf,Main.leaf,Main.leaf,Main.leaf,	Main.leaf, Main.leaf};
	@Override
	public Surface[] getSurfaces() {
		return surfaces;
	}
	
	
	public LeafBlock(int x, int y, int z, GameEngine engine) {
		super("Leaf", x, y, z, engine);
		
	}

	@Override
	public boolean breaked(String username) {
		if(username == null) {
			return false;
		}
		boolean giveSapling = new Random().nextInt(10) == 1;
		for(Entity e : Engine.world.getAllEntities()) {
			if(e.name.equals(username) && e instanceof PlayerMP) {
				String item =  giveSapling ? "Sapling":"Leaf";
				((PlayerMP)e).inventory.add(Main.Items.get(item), 1, true);

			}
		}

		return false;
		
	}



}
