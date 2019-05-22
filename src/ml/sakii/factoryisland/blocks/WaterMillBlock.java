package ml.sakii.factoryisland.blocks;


import java.awt.Color;
import java.util.Map.Entry;

import ml.sakii.factoryisland.Color4;
import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Main;

public class WaterMillBlock extends SimpleMachine implements TickListener{
	

	public WaterMillBlock(int x, int y, int z, GameEngine engine){
		super("WaterMill", x, y, z,  
				Main.wmSideColor,Main.wmGradientBeginColor,Main.wmPoweredColor, new Color4(Color.BLUE), engine);
		BlockMeta.put("active", "0");

	}
	
	
	
	@Override
	public boolean onMetadataUpdate(String key, String value) {
		BlockMeta.put(key, value);
		
		if(key.equals("active")){
			if(Integer.parseInt(value)>0){
				for(int i=0;i<6;i++){
					if(i != getTarget().id){
						Polygons.get(i).s.c = Main.wmPoweredColor;
					}
				}
			}else{
				for(int i=0;i<6;i++){
					if(i != getTarget().id){
						Polygons.get(i).s.c = Main.wmSideColor;
					}
				}
			}
		}
		
		return true;
	}


	@Override
	public void onLoad(){
		super.onLoad();
		
			if(Integer.parseInt(BlockMeta.get("active"))>0){
				for(int i=0;i<6;i++){
					if(i != getTarget().id){
						Polygons.get(i).s.c = Main.wmPoweredColor;
					}
				}
			}else{
				for(int i=0;i<6;i++){
					if(i != getTarget().id){
						Polygons.get(i).s.c = Main.wmSideColor;
					}
				}
			}
	}
	
	@Override
	public boolean tick(long tickCount) {
			doPower();
			return false;
		
	}
	

	
	private void doPower(){
		BlockFace target=getTarget();
		Block tBlock =Engine.world.getBlockAt(x+target.direction[0], y+target.direction[1], z+target.direction[2]); 
		if(tBlock instanceof WaterBlock){
			WaterBlock targetBlock = (WaterBlock)tBlock;
			if(targetBlock.getHeight() < 4){
				setMetadata("active", "1", true);
				for(Entry<BlockFace, Block> e : get6Blocks(this, false).entrySet()){
					BlockFace face = e.getKey();
					Block b = e.getValue();
					
					if(face != target && b instanceof PowerListener && !b.powers.containsKey(face.getOpposite())){
						PowerListener pl = (PowerListener)(b);
						pl.addPower(10, e.getKey().getOpposite());

						
					}
				}
				//this.addPower(10, BlockFace.NONE);
			}else if(targetBlock.getHeight() == 4){

				setMetadata("active", "0", true);
				//setMetadata("powered", "0");
				for(Entry<BlockFace, Block> e : get6Blocks(this, false).entrySet()){
					BlockFace face = e.getKey();
					Block b = e.getValue();
					
					if(face != target && b instanceof PowerListener && b.powers.containsKey(face.getOpposite())){
						PowerListener pl = (PowerListener)(b);
						pl.removePower(e.getKey().getOpposite());
					}
				}
				//this.removePower(BlockFace.NONE);
			}
			

			/*Block oppositeBlock =getBlockAt(x-target.direction[0], y-target.direction[1], z-target.direction[2]);
			if(oppositeBlock instanceof PowerListener){
				boolean oppositePowered = Boolean.parseBoolean(oppositeBlock.BlockMeta.get("powered"));
				if(getState() && !oppositePowered){
					//((PowerListener)oppositeBlock).powerApplied();
					oppositeBlock.setMetadata("powered", "true");
					Main.log("sending power to oppositeblock");
				}else if(!getState() && oppositePowered){
					oppositeBlock.setMetadata("powered", "false");
					//((PowerListener)oppositeBlock).powerStopped();
				}
			
			}*/
			
		}else{
			setMetadata("active", "0", true);
			for(Entry<BlockFace, Block> e : get6Blocks(this, false).entrySet()){
				BlockFace face = e.getKey();
				Block b = e.getValue();
				
				if(face != target && b instanceof PowerListener && b.powers.containsKey(face.getOpposite())){
					PowerListener pl = (PowerListener)(b);
					pl.removePower(e.getKey().getOpposite());

					
				}
			}
		}
		
	}

	/*@Override
	public void addPower(int power, BlockFace relativeFrom){
		powers.put(relativeFrom, power);
		setMetadata("powered", ""+getCharge());
	}

	@Override
	public void removePower(BlockFace relativeFrom) {
		powers.remove(relativeFrom);
		setMetadata("powered", ""+getCharge());
	}
*/

}
