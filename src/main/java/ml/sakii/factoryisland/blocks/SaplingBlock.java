package ml.sakii.factoryisland.blocks;

import java.util.ArrayList;
import java.util.Random;

import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Main;
//import ml.sakii.factoryisland.Polygon3D;

public class SaplingBlock extends Block implements TickListener{
	
	//int growTime=0;
	//int placedTick=-1;

	
	public SaplingBlock(int x, int y, int z, GameEngine engine) {
		super("Sapling", x, y, z, Main.saplingTop, Main.sapling, Main.sapling, Main.sapling, Main.sapling, Main.sapling, 0.6f, 0.6f, 1,engine);
		
		this.name = "Sapling";
		//this.fullblock = false;
		
		BlockMeta.put("growTime", 200 + new Random().nextInt(200)+"");
		BlockMeta.put("placedTick", getTick()+"");
		canBePlacedOn.add("Grass");
		refreshRate = 20;
	}
	
	private int placedTick(){
		return Integer.parseInt(BlockMeta.get("placedTick"));
	}
	
	private int growTime(){
		return Integer.parseInt(BlockMeta.get("growTime"));
	}



	@Override
	public boolean tick(long tickCount) {
		ArrayList<Block> tree = new ArrayList<>();
		if(tickCount > placedTick()+growTime()){
			tree.add(new WoodBlock(x, y, z, Engine));
			
			tree.add(new WoodBlock(x, y, z+1, Engine));
		
			tree.add(new WoodBlock(x, y, z+2, Engine));
		
			tree.add(new WoodBlock(x, y, z+3, Engine));

			Random leafRadius = new Random();
			int bigHeight = 1+leafRadius.nextInt(3);
			for(int i=0;i<4;i++){
				
				if(i<bigHeight){
					for(int j=-2;j<=2;j++){
						for(int k=-2;k<=2;k++){

							if(Engine.world.getBlockAt(x+j, y+k, z+2+i) == Block.NOTHING)
								tree.add(new LeafBlock(x+j, y+k, z+2+i, Engine));
						}
						
					}
					
				}else{
					for(int j=-1;j<=1;j++){
						for(int k=-1;k<=1;k++){

							if(Engine.world.getBlockAt(x+j, y+k, z+2+i) == Block.NOTHING)
								tree.add(new LeafBlock(x+j, y+k, z+2+i, Engine));
						}
						
					}
				
				}
			}
			Engine.world.destroyBlock(this, true);
			Engine.world.addBlockNoReplace(tree.get(0), true);
			for(int i=1;i<tree.size();i++) {
				Engine.world.addBlockNoReplace(tree.get(i), true);
			}
			
			return false;
		}
		return true;

		
		
	}
	
	
}
