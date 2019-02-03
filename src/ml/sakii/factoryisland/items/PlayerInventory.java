package ml.sakii.factoryisland.items;

import java.util.concurrent.CopyOnWriteArrayList;

import ml.sakii.factoryisland.Config;
import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Main;

public class PlayerInventory {
	public final CopyOnWriteArrayList<ItemStack> items = new CopyOnWriteArrayList<>();
	public static final PlayerInventory Creative = new CreativeInventory();
	public ItemStack SelectedStack = null;
	public int hotbarIndex = -1;
	GameEngine engine;
	boolean activateOnFirst=false;
	
	
	public PlayerInventory(GameEngine engine) {
		this.engine=engine;
	}
	

	
	
	public Item add(Item kind, int amount, boolean resend){
		if(!resend || engine.client == null) {
			ItemStack stack = getStack(kind);
			if(stack == null){
				stack = new ItemStack(kind, amount);
				items.add(stack);
			}else{
				stack.amount +=amount;
			}
			if(items.size()==1 && activateOnFirst){
				hotbarIndex = 0;
				SelectedStack = items.get(0);
			}
			
			if(stack.amount==0) {
				items.remove(stack);
				if(stack==SelectedStack) {
					if(hotbarIndex==items.size()){
						hotbarIndex--;
					}
					if(hotbarIndex>-1){
						SelectedStack = items.get(hotbarIndex);
					}else{
						SelectedStack = null;
					}
				}
				
			}
			
			
		}else if(resend) {
			doMultiplayer(kind.name,amount);
		}
		return kind;

	}
	
	public ItemStack getStack(ItemStack is){
		if(items.contains(is)){
			return items.get(items.indexOf(is));
		}
		return null;
	}
	
	public ItemStack getStack(Item kind){
		for(ItemStack is : items){
			if(is.kind == kind){
				return is;
			}
		}
		return null;
	}

	
	
	public void clear() {
		items.clear();
		SelectedStack=null;
		hotbarIndex=-1;
	}
	
	public void wheelUp(){
		if(hotbarIndex>-1){
			if(hotbarIndex == items.size()-1){
				hotbarIndex = 0;
			}else{
				hotbarIndex++;
			}
			SelectedStack = items.get(hotbarIndex);
		}
		
	}
	
	public void wheelDown(){
		if(hotbarIndex>-1){

			if(hotbarIndex == 0){
				hotbarIndex = items.size()-1;
			}else{
				hotbarIndex--;
			}
			SelectedStack = items.get(hotbarIndex);
		}
	}
	
	void doMultiplayer(String name, int amount) {
		//if(engine != null && engine.client != null) {// && engine.server == null) {
			engine.client.sendData("10,"+Config.username+","+name+","+amount);
			Main.log("PlayerInv sent data from client to server");

		//}
	}
}
