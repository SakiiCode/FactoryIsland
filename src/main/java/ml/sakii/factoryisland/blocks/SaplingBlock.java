package ml.sakii.factoryisland.blocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import ml.sakii.factoryisland.AssetLibrary;
import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Surface;
import ml.sakii.factoryisland.blocks.components.TickUpdateComponent;

public class SaplingBlock extends Block {
	

	private static final Surface[] surfaces = new Surface[] {
			AssetLibrary.saplingTop,
			AssetLibrary.sapling,
			AssetLibrary.sapling,
			AssetLibrary.sapling,
			AssetLibrary.sapling,
			AssetLibrary.sapling};
	
	private static final BlockDescriptor descriptor = new BlockDescriptor() {
		private static List<String> canBePlacedOn = Arrays.asList(new String[] {"Grass"});
		
		@Override
		public List<String> getCanBePlacedOn() {
			return canBePlacedOn;
		}
		
		@Override
		public int getRefreshRate() {
			return 20;
		}
		
	};
	
	private TickUpdateComponent tuc;
	
	
	
	public SaplingBlock(int x, int y, int z, GameEngine engine) {
		super("Sapling", x, y, z, 0.6f, 0.6f, 1,engine);
		
		int growTime = 200 + new Random().nextInt(200);
		storeMetadata("growTime", growTime+"");
		storeMetadata("placedTick", getTick()+"");
		
		tuc = new TickUpdateComponent(this, growTime) {
			
			@Override
			public boolean onTick(long tickCount) {
				ArrayList<Block> tree = new ArrayList<>();
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
				Engine.world.destroyBlock(SaplingBlock.this, true);
				Engine.world.addBlockNoReplace(tree.get(0), true);
				for(int i=1;i<tree.size();i++) {
					Engine.world.addBlockNoReplace(tree.get(i), true);
				}
				
				return false;
			}
		};
		
		addComponent(tuc);
	}
	
	@Override
	public Surface[] getSurfaces() {
		return surfaces;
	}
	
	@Override
	public BlockDescriptor getDescriptor() {
		return descriptor;
	}


	
	
}
