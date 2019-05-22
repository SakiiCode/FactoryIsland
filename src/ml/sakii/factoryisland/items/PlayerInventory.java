package ml.sakii.factoryisland.items;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import ml.sakii.factoryisland.Config;
import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Main;

public class PlayerInventory {
	//public final CopyOnWriteArrayList<ItemStack> items = new CopyOnWriteArrayList<>();
	//public final Map<ItemType, Integer> items = Collections.synchronizedMap(new LinkedHashMap<ItemType, Integer>());
	public final ConcurrentHashMap<ItemType, Integer> items = new ConcurrentHashMap<>();
	public static final PlayerInventory Creative = new CreativeInventory();
	//public Entry<ItemType, Integer> SelectedStack = null;
	private ItemStack SelectedStack = new ItemStack();
	public int hotbarIndex = -1;
	GameEngine engine;
	boolean activateOnFirst=true;
	
	
	public PlayerInventory(GameEngine engine) {
		this.engine=engine;
	}
	

	
	
	public ItemType add(ItemType kind, int amount, boolean resend){
		if(!resend || engine.client == null) {
			int stack = getStack(kind);
			
			int originalSize = items.size();
			//if(stack == 0){ // hozzáad 1-et
				/*stack = new ItemStack(kind, amount);
				items.add(stack);*/
			//	items.put(kind, amount);
			//}else{
				//stack.amount +=amount;
				items.put(kind, stack+amount);
			//}
			
			
			if(originalSize==0 && items.size()==1){ //üres volt, nem lett üres
				if(activateOnFirst && Main.GAME != null) {
					if(Main.GAME.remoteInventory != null && Main.GAME.remoteInventory.getInv().items.size()==0) {
						hotbarIndex = 0;
					}else if(Main.GAME.remoteInventory==null) {
						hotbarIndex = 0;	
					}
					//SelectedStack = items.entrySet().toArray(new Entry<ItemType, Integer>[0])[0];
				}
			}
			
			
			
			if(stack+amount==0) { //a stack kifogyott
				
				//ItemStack selected = getSelectedStack(); 
				//if(selected != null && kind==selected.kind) {
					if(hotbarIndex==items.size()-1){
						hotbarIndex--;
					}
					/*if(hotbarIndex>-1){
						SelectedStack = items.get(hotbarIndex);
					}else{
						SelectedStack = null;
					}*/
				//}
				
				items.remove(kind);
				
				if(items.size()==0){ //nem volt üres, üres lett
					hotbarIndex=-1;
					if(Main.GAME.remoteInventory != null) {
						Main.GAME.SwapInv();
					}
				}
				
				
				
			}
			
			
		}else if(resend) {
			doMultiplayer(kind.name,amount);
		}
		return kind;

	}
	
	/*public ItemStack getStack(ItemStack is){
		if(items.contains(is)){
			return items.get(items.indexOf(is));
		}
		return null;
	}*/
	
	public ItemStack getSelectedStack(){
		if(hotbarIndex>-1) { //BlockInventorynál ez kell 
			@SuppressWarnings("unchecked")
			Entry<ItemType,Integer> stack =  (Entry<ItemType,Integer>)(items.entrySet().toArray()[hotbarIndex]);
			if(stack != null) {
				SelectedStack.set(stack.getKey(), stack.getValue());
				return SelectedStack;//new ItemStack(stack.getKey(), stack.getValue());
			}
		}
		return null;
	}
	
	public int getStack(ItemType kind){
		/*for(ItemStack is : items){
			if(is.kind == kind){
				return is;
			}
		}*/
		Integer count = items.get(kind);
		return count==null ? 0 : count;
	}

	
	
	public void clear() {
		items.clear();
		//SelectedStack=null;
		hotbarIndex=-1;
	}
	
	public void wheelUp(){
		if(hotbarIndex>-1){
			if(hotbarIndex == items.size()-1){
				hotbarIndex = 0;
			}else if(hotbarIndex<items.size()-1) {
					hotbarIndex++;
			}
			//SelectedStack = items.get(hotbarIndex);
		}
		
	}
	
	public void wheelDown(){
		if(hotbarIndex>-1){

			if(hotbarIndex == 0){
				hotbarIndex = items.size()-1;
			}else{
				hotbarIndex--;
			}
			//SelectedStack = items.get(hotbarIndex);
		}
	}
	
	void doMultiplayer(String name, int amount) {
		//if(engine != null && engine.client != null) {// && engine.server == null) {
			engine.client.sendData("10,"+Config.username+","+name+","+amount);
			Main.log("PlayerInv sent data from client to server");

		//}
	}
}
