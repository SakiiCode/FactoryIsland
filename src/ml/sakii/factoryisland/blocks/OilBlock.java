package ml.sakii.factoryisland.blocks;

import java.util.Map.Entry;

import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Main;
import ml.sakii.factoryisland.Polygon3D;

public class OilBlock extends Block implements TickListener, LoadListener, MetadataListener, BreakListener{

	//private final Color paint = new Color(180, 200, 255);
	//Color heightmapped = paint;


	public OilBlock(int x, int y, int z, GameEngine engine) {
		super("Oil",x, y, z,Main.oils[3], Main.oils[3], Main.oils[3], Main.oils[3], Main.oils[3], Main.oils[3],engine);

		BlockMeta.put("height", "3");
		refreshRate = 20;


	}

	public OilBlock(int x, int y, int z, int height, GameEngine engine) {
		super("Oil",x, y, z,Main.oils[height], Main.oils[height], Main.oils[height], Main.oils[height], Main.oils[height], Main.oils[height],engine);
		BlockMeta.put("height", height+"");
		refreshRate = 20;

	}



	public void setHeight(int newHeight){
		setMetadata("height",""+newHeight, true);
		heightMap();

	}


	private void heightMap() {

		for(Polygon3D polygon : Polygons){
			polygon.s = Main.oils[getHeight()];
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
			for(Entry<BlockFace,Block> entry : get6Blocks(this,true).entrySet()){
				BlockFace key = entry.getKey();
				Block value = entry.getValue();

				if(key != BlockFace.BOTTOM && key != BlockFace.TOP){
					if(value == Block.NOTHING){
						hasEmptyNearby = true;
					}else if(value.name.equals("Oil")){
						OilBlock ob = (OilBlock)value;
						int otherHeight = Integer.parseInt(ob.BlockMeta.get("height"));

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
				if(getHeight() != 3){
					setHeight(biggerheight-1);

				}
			}else if(!hasPlusOneNearby){
				if(getHeight() != 3){

					if(getHeight() == 1){
						//deleteBlock(this);
						Engine.world.destroyBlock(Engine.world.getBlockAt(this.x, this.y, this.z), true);
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


							OldBlock temp = new OldBlock(x + key.direction[0], y + key.direction[1], z + key.direction[2], Engine);
							/*OilBlock oil1 = new OilBlock(x + key.direction[0], y + key.direction[1], z + key.direction[2], getHeight()-1,game, server);
							if(game==null){
								oil1.server = server;
							}*/
							int biggestyet = 0;
							for(Entry<BlockFace, Block> oentry : get6Blocks(temp, false).entrySet()){
								BlockFace okey = oentry.getKey();
								Block ovalue = oentry.getValue();
								if(okey != BlockFace.TOP && okey != BlockFace.BOTTOM){
									if(ovalue.name.equals("Oil")){
										OilBlock ob = (OilBlock)ovalue;
										int wotherHeight = Integer.parseInt(ob.BlockMeta.get("height"));
										if(wotherHeight > getHeight() + 1 && biggestyet < wotherHeight){
											biggestyet=wotherHeight;
											//oil1.setHeight(wotherHeight - 1);
											//break;
										}
									}
								}
							}

							OilBlock oil1;
							if(biggestyet==0){
								oil1 = new OilBlock(x + key.direction[0], y + key.direction[1], z + key.direction[2], getHeight()-1, Engine);

							}else{
								oil1 = new OilBlock(x + key.direction[0], y + key.direction[1], z + key.direction[2], biggestyet-1, Engine);
							}
							Engine.world.addBlockNoReplace(oil1,true);
						}
					}
				}

			}

			return false;



	}

	@Override
	public boolean breaked(String username)
	{
		if(getHeight()==3) {
			return true;
		}
		return false;
	}



}
