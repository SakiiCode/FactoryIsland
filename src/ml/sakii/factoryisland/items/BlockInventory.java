package ml.sakii.factoryisland.items;

import ml.sakii.factoryisland.Config;
import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Main;
import ml.sakii.factoryisland.blocks.Block;

public class BlockInventory extends PlayerInventory {

	Block b;
	

	public BlockInventory(Block b, GameEngine engine) {
		super(engine);
		this.b=b;
		activateOnFirst=false;
	}
	
	@Override
	void doMultiplayer(String name, int amount) {
		//engine.client.sendData("13,"+Config.username+","+b.x+","+b.y+","+b.z+","+name+","+amount);
		engine.client.sendInvBlockAdd(Config.username, b, name, amount);
		Main.log("BlockInv sent data from client to server");
	}

		
}
