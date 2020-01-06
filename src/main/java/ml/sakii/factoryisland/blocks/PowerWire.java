package ml.sakii.factoryisland.blocks;

import java.awt.Color;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Main;
import ml.sakii.factoryisland.Point3D;
import ml.sakii.factoryisland.Polygon3D;

public abstract class PowerWire extends Block implements PowerListener, MetadataListener, LoadListener, BreakListener, TickListener{
	
	
	public HashMap<BlockFace, Integer> powers = new HashMap<>();
	
	
	public PowerWire(int x, int y, int z, GameEngine engine)
	{
		
		super(x, y, z,engine);
		init();
	}

	public PowerWire(String name, int x, int y, int z, GameEngine engine)
	{
		super(name, x, y, z,  engine);
		init();
	}
	
	
	public PowerWire(String name, int x, int y, int z, float xscale, float yscale, float zscale, GameEngine engine)
	{
		super(name, x, y, z, xscale, yscale, zscale, engine);
		init();
	}

	private void init() {
		BlockMeta.put("powered", "0");
	}

	

	
	public int getCharge()
	{
		if (powers.isEmpty())
		{
			return 0;
		}
		return powersSorted().lastEntry().getKey();
	}
	
	TreeMap<Integer, BlockFace> powersSorted()
	{
		TreeMap<Integer, BlockFace> result = new TreeMap<>();
		for (Entry<BlockFace, Integer> entry : powers.entrySet())
		{
			result.put(entry.getValue(), entry.getKey());
		}

		return result;

	}
	

	@Override
	public void addPower(int power, BlockFace relativeFrom) {
		if(powers.get(relativeFrom) == null  || powers.get(relativeFrom) < power){
			powers.put(relativeFrom, power);
			if(power>1){
				spreadPower(power-1,true);
			}
			setMetadata("powered", getCharge()+"", true);
		}
	}
	
	
	private void spreadPower(int targetPower, boolean add){ 
		for(Entry<BlockFace, Block> entry : Engine.world.get6Blocks(new Point3D().set(pos), false).entrySet()){
			Block b = entry.getValue();
			BlockFace face = entry.getKey();
			if(b instanceof PowerListener){ 
				PowerListener pw = (PowerListener)b;
				if(add) {
					pw.addPower(targetPower, face.getOpposite());
				}else {
					pw.removePower(face.getOpposite());
				}
			}
		}
	}
	
	@Override
	public void removePower(BlockFace relativeFrom) {
		if(powers.containsKey(relativeFrom)) {
			powers.remove(relativeFrom);
			spreadPower(0,false);
	
			setMetadata("powered", getCharge()+"", true);
		}
		
	}
	

	@Override
	public boolean onMetadataUpdate(String key, String value)
	{
		if(key.equals("powered")){
			BlockMeta.put(key, value);
			recalcPaints();
			
		}
		
		return false;
		
	}
	
	private void recalcPaints() {
		int charge = Integer.parseInt(BlockMeta.get("powered"));

		for(Polygon3D p : Polygons){
			if(charge == 0){
				p.s.paint = false;
			}else{
				p.s.p = new Color(Main.fire.c.getRed()/255f,Main.fire.c.getGreen()/255f,Main.fire.c.getBlue()/255f, Math.max(0,Math.min(10, charge))/10f);
				p.s.paint = true;
			}
		}
	}
	
	@Override
	public void onLoad(){
		recalcPaints();
		
	}
	
	@Override
	public boolean breaked(String username) {
		if(getCharge()>1) {
			spreadPower(0,false);
		}
		return true;
	}
	
	@Override
	public boolean tick(long tickCount) {// mellette lerakas miatt kell
		int charge = getCharge();
		if(charge>1) {
			spreadPower(getCharge()-1, true);
		}
		return false;
		
	}


}
