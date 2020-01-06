package ml.sakii.factoryisland.blocks;

import java.util.Map.Entry;

import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Polygon3D;
import ml.sakii.factoryisland.Surface;


public abstract class Fluid extends Block implements TickListener, LoadListener, MetadataListener, BreakListener{


	//private int tmpHeight;
	private Surface[] textures; //szintenkent
	private int maxHeight;
	public Fluid(String name, int x, int y, int z, Surface[] textures, GameEngine engine) {
		this(name,x,y,z,textures.length,textures,engine);
	}
	
	public Fluid(String name, int x, int y, int z, int height, Surface[] textures, GameEngine engine) {
		super(name,x, y, z,engine);
		//tmpHeight=height;
		this.textures=textures;
		maxHeight=textures.length-1;
		BlockMeta.put("height", height+"");
		heightMap();
		transparent=true;
		solid = false;
		refreshRate = 10;

	}
	
	
	public void setHeight(int newHeight){
		setMetadata("height",""+newHeight, true);
		heightMap();
		
	}
	

	private void heightMap() {
		for(Polygon3D polygon : Polygons){
			polygon.s = textures[getHeight()];
			polygon.recalcLightedColor();
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
	public void onLoad(){
		heightMap();
	}

	
	@Override
	public boolean tick(long tickCount) {

			boolean hasEmptyNearby=false,hasPlusOneNearby=false,hasBiggerNearby=false;
			int biggerheight=0;
			int height = getHeight();
			for(Entry<BlockFace,Block> entry : Engine.world.get6Blocks(this,true).entrySet()){
				BlockFace key = entry.getKey();
				Block value = entry.getValue();
				
				if(key != BlockFace.BOTTOM && key != BlockFace.TOP){
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
					for(Entry<BlockFace, Block> entry : Engine.world.get6Blocks(this, true).entrySet()){
						BlockFace key = entry.getKey();
						Block value = entry.getValue();
						if(key != BlockFace.TOP && key != BlockFace.BOTTOM && value == Block.NOTHING){
							
							
							int biggestyet = 0;
							for(Entry<BlockFace, Block> wentry : Engine.world.get6Blocks(x + key.direction[0], y + key.direction[1], z + key.direction[2], false).entrySet()){
								BlockFace wkey = wentry.getKey();
								Block wvalue = wentry.getValue();
								if(wkey != BlockFace.TOP && wkey != BlockFace.BOTTOM){
									if(wvalue.name.equals(name)){
										
										Fluid wb = (Fluid)wvalue;
										int wotherHeight = wb.getHeight();
										if(wotherHeight > height + 1 && biggestyet < wotherHeight){
											biggestyet=wotherHeight;

										}
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
	public boolean breaked(String username)
	{
		if(getHeight()==maxHeight) {
			return true;
		}
		return false;
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
