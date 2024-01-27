package ml.sakii.factoryisland.blocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import ml.sakii.factoryisland.Game;
import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Main;
import ml.sakii.factoryisland.Object3D;
import ml.sakii.factoryisland.Polygon3D;
import ml.sakii.factoryisland.Surface;
import ml.sakii.factoryisland.blocks.components.BreakComponent;
import ml.sakii.factoryisland.blocks.components.TickUpdateComponent;
import ml.sakii.factoryisland.blocks.components.WorldLoadComponent;
import ml.sakii.factoryisland.items.ItemStack;


public abstract class Fluid extends Block implements MetadataListener{
	
	protected TickUpdateComponent tuc;
	protected BreakComponent bc;
	protected WorldLoadComponent wlc;
	
	public Fluid(String name, int x, int y, int z, int height, GameEngine engine) {
		super(name,x, y, z,engine);
		storeMetadata("height", height+"");
		
		if(engine != null) {
			heightMap();
		}
		
		tuc = new TickUpdateComponent(this, getDescriptor().getRefreshRate()) {
			@Override
			public boolean onTick(long tick) {
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
							Engine.world.destroyBlock(Fluid.this, true);

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
		};
		
		addComponent(tuc);
		
		bc = new BreakComponent(this) {
			
			@Override
			public List<ItemStack> onBreak() {
				
				if(getHeight()!=getMaxHeight()) {
					return new ArrayList<>();
				}
				
				ArrayList<ItemStack> result = new ArrayList<>();
				result.add(new ItemStack(Main.Items.get(Fluid.this.name), 1));
				return result;
			}
		};
		
		addComponent(bc);
		
		wlc = new WorldLoadComponent(this) {
			
			@Override
			public void onLoad(Game game) {
				heightMap();
			}
		};
		
		addComponent(wlc);

	}
	
	@Override
	public abstract BlockDescriptor getDescriptor();
	
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
		storeMetadata(key, value);
		if(key.equals("height")){
			heightMap();
		}
		
		return true;
	}


	
	protected abstract Surface[] getTextures();  //szintenkent

	private int getMaxHeight() {
		return getTextures().length-1;
	}
	
	public int getHeight()
	{
		String h =getMetadata("height"); 
		if(h==null) {
			return 1;
		}
		return Integer.parseInt(h);
	}

	
}
