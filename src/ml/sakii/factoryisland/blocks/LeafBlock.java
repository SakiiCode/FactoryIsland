package ml.sakii.factoryisland.blocks;

import java.util.Random;

import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Main;
import ml.sakii.factoryisland.entities.Entity;
import ml.sakii.factoryisland.entities.PlayerMP;

public class LeafBlock extends Block implements BreakListener{
	
	public LeafBlock(int x, int y, int z, GameEngine engine) {
		super("Leaf", x, y, z, Main.leaf,
				Main.leaf,
				Main.leaf,
				Main.leaf,
				Main.leaf,
				Main.leaf,
				engine);
		//returnOnBreak=false;
		
		
		
	}

	@Override
	public boolean breaked(String username) {
		if(username == null) {
			return false;
		}
		boolean giveSapling = new Random().nextInt(10) == 1;
		//if(giveSapling){
			
			//if(Engine.client==null) {
				//Engine.Inv.add(Main.Items.get("Sapling"), 1, true);
			/*}else {
				Engine.client.sendData("10,"+username+","+Main.Items.get("Sapling").name+","+1);
			}*/
		//}else{
			//if(Engine.client==null) {
				//Engine.Inv.add(Main.Items.get("Leaf"), 1, true);
			/*}else {
				Engine.client.sendData("10,"+username+","+Main.Items.get("Leaf").name+","+1);
			}*/

		//}
		for(Entity e : Engine.world.getAllEntities()) {
			if(e.name.equals(username) && e instanceof PlayerMP) {
				String item =  giveSapling ? "Sapling":"Leaf";
				((PlayerMP)e).inventory.add(Main.Items.get(item), 1, true);

			}
		}

		return false;
		
	}

	/*@Override
	public void breakedOnServer() {
		
	}*/

}
