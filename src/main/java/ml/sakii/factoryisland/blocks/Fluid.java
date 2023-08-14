package ml.sakii.factoryisland.blocks;

import java.util.HashMap;
import java.util.Map.Entry;

import ml.sakii.factoryisland.Game;
import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Object3D;
import ml.sakii.factoryisland.Polygon3D;
import ml.sakii.factoryisland.Surface;
import ml.sakii.factoryisland.items.ItemStack;


public abstract class Fluid extends Block implements TickListener, LoadListener, MetadataListener, BreakListener{

	private static final BlockDescriptor descriptor =new BlockDescriptor() {
		@Override
		public boolean isSolid() {
			return false;
		}
		
		@Override
		public boolean isTransparent() {
			return true;
		}
		
		@Override
		public int getRefreshRate() {
			return 10;
		}
	};
	
	public Fluid(String name, int x, int y, int z, int height, GameEngine engine) {
		super(name,x, y, z,engine);
		BlockMeta.put("height", height+"");
		
		if(engine != null) {
			heightMap();
		}

	}
	
	@Override
	public BlockDescriptor getDescriptor() {
		return descriptor; 
	}
	
	public void setHeight(int newHeight){
		setMetadata("height",""+newHeight, true);
		heightMap();
		
	}
	

	private void heightMap() {
		Surface[] textures = getTextures();
		for(Object3D obj : Objects){
			if(obj instanceof Polygon3D polygon) {
				polygon.s = textures[getHeight()];
				polygon.recalcLightedColor();
			}
		}
	}

	@Override
	public boolean onMetadataUpdate(String key, String value)
	{
		BlockMeta.put(key, value);
		if(key.equals("height")){
			heightMap();
		}
		
		return true;
	}


	
	@Override
	public void onLoad(Game game){
		heightMap();
	}
	
	protected abstract Surface[] getTextures();  //szintenkent

	private int getMaxHeight() {
		return getTextures().length-1;
	}
	
	@Override
	public boolean tick(long tickCount) {

			boolean hasEmptyNearby=false,hasPlusOneNearby=false,hasBiggerNearby=false;
			int biggerheight=0;
			int height = getHeight();
			int maxHeight = getMaxHeight();
			for(Block value : Engine.world.get4Blocks(x,y,z).values()){
				if(value == Block.NOTHING){
					hasEmptyNearby = true;
				}else if(value.name.equals(name)){
					Fluid wb = (Fluid)value;
					int otherHeight = wb.getHeight();
					
					if(otherHeight == height+1){
						hasPlusOneNearby=true;
					}else if(otherHeight > height + 1){
						if(biggerheight<otherHeight){
							biggerheight=otherHeight;
							
							hasBiggerNearby=true;
						}
						
					}
				
				}
				
			}
			
			
			if(hasBiggerNearby){
				if(height != maxHeight){
					setHeight(biggerheight-1);
				
				}
			}else if(!hasPlusOneNearby){
				if(height != maxHeight){
					if(height == 1){
						Engine.world.destroyBlock(this, true);

						return false;
					}
					setHeight(height-1);
				}
				
			}
			
			height=getHeight();
			
			if(hasEmptyNearby){
				
				
				if(getHeight() > 1){
					HashMap<BlockFace,Block> adjacentBlocks = Engine.world.get4Blocks(pos.x,pos.y,pos.z);
					for(Entry<BlockFace, Block> entry : adjacentBlocks.entrySet()){
						BlockFace key = entry.getKey();
						Block value = entry.getValue();
						if(value == Block.NOTHING){
							
							
							int biggestyet = 0;
							for(Block wvalue : Engine.world.get4Blocks(x+key.direction[0],y+key.direction[1],z+key.direction[2]).values()){
								if(wvalue.name.equals(name)){
									
									Fluid wb = (Fluid)wvalue;
									int wotherHeight = wb.getHeight();
									if(wotherHeight > height + 1 && biggestyet < wotherHeight){
										biggestyet=wotherHeight;

									}
								}
							}
							
							int height2 = biggestyet==0 ? height-1 : biggestyet-1;
							
							Block child1 = Engine.createFluid(name,x + key.direction[0], y + key.direction[1], z + key.direction[2], height2);
							
							Engine.world.addBlockNoReplace(child1, true);

						}
					}
				}
				
			}
			
			
			
			return false;
			

			
	}


	@Override
	public ItemStack[] breaked(String username)
	{
		if(getHeight()==getMaxHeight()) {
			return null;
		}
		return new ItemStack[] {};
	}
	public int getHeight()
	{
		String h =BlockMeta.get("height"); 
		if(h==null) {
			return 1;
		}
		return Integer.parseInt(h);
	}

	
}
