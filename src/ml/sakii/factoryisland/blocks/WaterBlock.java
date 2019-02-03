package ml.sakii.factoryisland.blocks;

import java.util.Map.Entry;

import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Main;
import ml.sakii.factoryisland.Polygon3D;

public class WaterBlock extends Block implements TickListener, LoadListener, MetadataListener{

	
	//private final Color paint = new Color(180, 200, 255);
	//Color heightmapped = paint;
	
	
	public WaterBlock(int x, int y, int z, GameEngine engine) {
		super("Water",x, y, z,Main.waters[4], Main.waters[4], Main.waters[4], Main.waters[4], Main.waters[4], Main.waters[4],engine);

		BlockMeta.put("height", "4");
		transparent=true;
		solid = false;
		refreshRate = 10;
	}
	
	public WaterBlock(int x, int y, int z, int height, GameEngine engine) {
		super("Water",x, y, z,Main.waters[height], Main.waters[height], Main.waters[height], Main.waters[height], Main.waters[height], Main.waters[height],engine);

		BlockMeta.put("height", height+"");
		transparent=true;
		solid = false;
		refreshRate = 10;

	}
	
	

	public void setHeight(int newHeight){
		setMetadata("height",""+newHeight);
		heightMap();
		
	}
	

	private void heightMap() {
		for(Polygon3D polygon : Polygons){
			polygon.s = Main.waters[getHeight()];
			polygon.recalcLightedColor();
		}
	}

	@Override
	public void onMetadataUpdate(String key, String value)
	{
		if(key.equals("height")){
			heightMap();
		}
	}

/*	
	@Override
	public void setMetadata(String key, String value){
		
			super.setMetadata(key, value);
		}else {
			BlockMeta.put(key, value);
			super.setMetadata(key, value);
		}
		
		
	}*/
	
	@Override
	public void onLoad(){
		heightMap();
	}

	
	@Override
	public boolean tick(long tickCount) {

			boolean hasEmptyNearby=false,hasPlusOneNearby=false,hasBiggerNearby=false;
			int biggerheight=0;
			for(Entry<BlockFace,Block> entry : get6Blocks(this,true).entrySet()){
				BlockFace key = entry.getKey();
				Block value = entry.getValue();
				
				if(key != BlockFace.BOTTOM && key != BlockFace.TOP){
					if(value == Block.NOTHING){
						hasEmptyNearby = true;
					}else if(value instanceof WaterBlock){
						//WaterBlock wb = (WaterBlock)value;
						int otherHeight = value.getHeight();
						
						if(otherHeight == getHeight()+1){
							hasPlusOneNearby=true;
						}else if(otherHeight > getHeight() + 1){
							if(biggerheight<otherHeight){
								biggerheight=otherHeight;
								
								hasBiggerNearby=true;
							}
							
						}
						
						
					}
					
					
				}
				
			}
			
			
			if(hasBiggerNearby){
				if(getHeight() != 4){
					setHeight(biggerheight-1);
				
				}
			}else if(!hasPlusOneNearby){
				if(getHeight() != 4){
					if(getHeight() == 1){
						deleteBlock(this);
						return false;
					}
					setHeight(getHeight()-1);
				}
				
			}
			
			if(hasEmptyNearby){
				
				
				if(getHeight() > 1){
					for(Entry<BlockFace, Block> entry : get6Blocks(this, true).entrySet()){
						BlockFace key = entry.getKey();
						Block value = entry.getValue();
						if(key != BlockFace.TOP && key != BlockFace.BOTTOM && value == Block.NOTHING){
							
							//WaterBlock water1 = new WaterBlock(x + key.direction[0], y + key.direction[1], z + key.direction[2], getHeight()-1,game, server);
							
							OldBlock temp = new OldBlock(x + key.direction[0], y + key.direction[1], z + key.direction[2], Engine);
							int biggestyet = 0;
							for(Entry<BlockFace, Block> wentry : get6Blocks(temp, false).entrySet()){
								BlockFace wkey = wentry.getKey();
								Block wvalue = wentry.getValue();
								if(wkey != BlockFace.TOP && wkey != BlockFace.BOTTOM){
									if(wvalue.name.equals("Water")){
										WaterBlock wb = (WaterBlock)wvalue;
										int wotherHeight = Integer.parseInt(wb.BlockMeta.get("height"));
										if(wotherHeight > getHeight() + 1 && biggestyet < wotherHeight){
											biggestyet=wotherHeight;
											//water1.setHeight(wotherHeight - 1);
											//water1.BlockMeta.put("height", ""+(wotherHeight - 1));
											//break;
										}
									}
								}
							}
							WaterBlock water1;
							if(biggestyet==0){
								water1 = new WaterBlock(x + key.direction[0], y + key.direction[1], z + key.direction[2], getHeight()-1,Engine);
								
							}else{
								water1 = new WaterBlock(x + key.direction[0], y + key.direction[1], z + key.direction[2], biggestyet-1,Engine);
							}
							addBlock(water1, false);

						}
					}
				}
				
			}
			
			
			
			return false;
			

			
	}


	
	/*@Override
	public void updateTexture(double x3, double y3, double z3){
		super.updateTexture(x3, y3, z3);
		for(Polygon3D p : Polygons){
			p.faceFilter = true;
		}
	}*/
}
