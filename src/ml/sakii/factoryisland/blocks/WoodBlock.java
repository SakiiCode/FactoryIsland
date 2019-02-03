package ml.sakii.factoryisland.blocks;

import java.awt.Color;
import java.util.Map.Entry;

import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Main;
import ml.sakii.factoryisland.MyEntry;
import ml.sakii.factoryisland.Polygon3D;
import ml.sakii.factoryisland.Surface;

public class WoodBlock extends Block implements PowerListener, TickListener, BreakListener, LoadListener, MetadataListener{

	public WoodBlock(int x, int y, int z, GameEngine engine) {
		super("Wood", x, y, z,
				new Surface(Main.wood.Texture),
				new Surface(Main.wood.Texture),
				new Surface(Main.wood.Texture),
				new Surface(Main.wood.Texture),
				new Surface(Main.wood.Texture),
				new Surface(Main.wood.Texture),
				engine);
	}

	@Override
	public void onMetadataUpdate(String key, String value)
	{
		if(key.equals("powered")){
			int charge = Integer.parseInt(value);
			if(charge == 0){
				for(Polygon3D p : Polygons){
					p.s.paint = false;
				}
			}else{
				for(Polygon3D p : Polygons){
					p.s.paint = true;
					p.s.p = new Color(Main.fire.c.getRed()/255f,Main.fire.c.getGreen()/255f,Main.fire.c.getBlue()/255f, charge/10f);
				}
			}
		}
		
	}
	
	@Override
	public void onLoad(){
		int charge = getCharge();
		if(charge == 0){
			for(Polygon3D p : Polygons){
				p.s.paint = false;
			}
		}else{
			for(Polygon3D p : Polygons){
				p.s.paint = true;
				p.s.p = new Color(Main.fire.c.getRed()/255f,Main.fire.c.getGreen()/255f,Main.fire.c.getBlue()/255f, charge/10f);
			}
		}
		
	}
	


	@Override
	public boolean tick(long tickCount) {

		spreadPower(getCharge()-1);
		return false;
		
	}

	@Override
	public void addPower(int power, BlockFace relativeFrom) {
		powers.put(relativeFrom, power);
		spreadPower(power-1);
		setMetadata("powered", getCharge()+"");
	}
	
	private void spreadPower(int targetPower){
		if(targetPower>0){
			for(Entry<BlockFace, Block> entry : get6Blocks(this, false).entrySet()){
				Block b = entry.getValue();
				BlockFace face = entry.getKey();
				if(b instanceof PowerListener){
					
					if(b.powers.get(face.getOpposite()) == null  || b.powers.get(face.getOpposite()) < targetPower){//entrySet().contains(new MyEntry<BlockFace, Integer>(face.getOpposite(), targetPower))){
						((PowerListener)b).addPower(targetPower, face.getOpposite());
					}
				}
				
			}
		}
	}

	@Override
	public void removePower(BlockFace relativeFrom) {
		int removedpower = powers.remove(relativeFrom);
		//if(getCharge()>1)
			for(Entry<BlockFace, Block> entry : get6Blocks(this, false).entrySet()){
				Block b = entry.getValue();
				BlockFace face = entry.getKey();
				if(b instanceof PowerListener){
					if(b.powers.entrySet().contains(new MyEntry<>(face.getOpposite(), removedpower-1))){
						((PowerListener)b).removePower(face.getOpposite());
					}
					
				}
				
			}

		setMetadata("powered", getCharge()+"");

		
	}

	@Override
	public boolean breaked(String username) {
		if(getCharge()>1)
			for(Entry<BlockFace, Block> entry : get6Blocks(this, false).entrySet()){
				Block b = entry.getValue();
				BlockFace face = entry.getKey();
				if(b instanceof PowerListener){
					((PowerListener)b).removePower(face.getOpposite());
				}
				
			}
		return false;
	}



	/*@Override
	public void breakedOnServer() {
		if(getCharge()>1)
			for(Entry<BlockFace, Block> entry : get6Blocks(this, false).entrySet()){
				Block b = entry.getValue();
				BlockFace face = entry.getKey();
				if(b instanceof PowerListener){
					((PowerListener)b).removePower(face.getOpposite());
				}
				
			}
		
	}*/

	
}
