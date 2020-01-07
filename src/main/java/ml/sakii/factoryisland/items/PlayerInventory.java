package ml.sakii.factoryisland.items;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Main;

public class PlayerInventory {
	public final ConcurrentHashMap<ItemType, Integer> items = new ConcurrentHashMap<>();
	public static final PlayerInventory Creative = new CreativeInventory();
	private final ItemStack SelectedStack = new ItemStack(null,0);
	private int hotbarIndex = -1;
	GameEngine engine;
	boolean activateOnFirst=true;
	String SelectedName="";
	String playerName;
	
	
	public PlayerInventory(String playerName, GameEngine engine) {
		this.engine=engine;
		this.playerName = playerName;
	}
	

	
	
	public ItemType add(ItemType kind, int amount, boolean resend){
		if(resend && engine.client != null) {
			doMultiplayer(kind, amount);

		}else {
			int stack = getStack(kind);
			
			int originalSize = items.size();
			items.put(kind, stack+amount);
			
			if(!Main.headless) {
			if(originalSize==0 && items.size()==1){ //üres volt, nem lett üres
					if(activateOnFirst && Main.GAME != null) {
						if(Main.GAME.remoteInventory != null && Main.GAME.remoteInventory.getInv().items.size()==0) {
							setHotbarIndex(0);
						}else if(Main.GAME.remoteInventory==null) {
							setHotbarIndex(0);	
						}
					}
				}
			}
			
			
			
			if(stack+amount==0) { //a stack kifogyott
				
				if(hotbarIndex==items.size()-1){// ha a vegerol fogy ki csokken eggyel
					setHotbarIndex(hotbarIndex-1);
					items.remove(kind);
				}else {// amugy marad a hotbarindex
					items.remove(kind);
					setHotbarIndex(hotbarIndex);
				}
				
				
				
				if(items.size()==0){ //nem volt üres, üres lett
					//setHotbarIndex(-1);
					if(!Main.headless) {
						if(Main.GAME.remoteInventory != null) {
							Main.GAME.SwapInv();
						}
					}
				}
				
				
				
			}
			
			
		}
		return kind;

	}

	
	public ItemType getSelectedKind() {
		return SelectedStack.kind;
	}
	
	public int getSelectedAmount() {
		return SelectedStack.amount;
	}
	
	public boolean hasSelected() {
		return hotbarIndex>-1;
	}
	
	public int getStack(ItemType kind){

		Integer count = items.get(kind);
		return count==null ? 0 : count;
	}

	public int getHotbarIndex() {
		return hotbarIndex;
	}
	
	public void setHotbarIndex(int hotbarIndex) {
		this.hotbarIndex=hotbarIndex;
		if(hotbarIndex>-1) { //BlockInventorynál ez kell 
			@SuppressWarnings("unchecked")
			Entry<ItemType,Integer> stack =  (Entry<ItemType,Integer>)(items.entrySet().toArray()[hotbarIndex]);
			if(stack != null) {
				SelectedStack.set(stack.getKey(), stack.getValue());
			}
		}else {
			SelectedStack.set(null,0);
		}
	}
	

	public void clear() {
		items.clear();

		setHotbarIndex(-1);
	}
	
	public void wheelUp(){
		if(hotbarIndex>-1){
			if(hotbarIndex == items.size()-1){
				setHotbarIndex(0);
			}else if(hotbarIndex<items.size()-1) {
					setHotbarIndex(hotbarIndex+1);
			}
		}
		
	}
	
	public void wheelDown(){
		if(hotbarIndex>-1){

			if(hotbarIndex == 0){
				setHotbarIndex(items.size()-1);
			}else{
				setHotbarIndex(hotbarIndex-1);
			}
		}
	}
	
	void doMultiplayer(ItemType kind, int amount) {
		engine.client.sendInvPlayerAdd(playerName, kind, amount);	
		Main.log("PlayerInv sent data from client to server");
	}




	public void add(ItemStack[] stacks, boolean resend) {
		for(ItemStack stack : stacks) {
			add(stack.kind,stack.amount,resend);
		}
		
	}
}
