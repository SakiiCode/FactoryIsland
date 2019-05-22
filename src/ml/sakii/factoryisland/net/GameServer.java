package ml.sakii.factoryisland.net;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import ml.sakii.factoryisland.Config;
import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Main;
import ml.sakii.factoryisland.Vector;
import ml.sakii.factoryisland.World;
import ml.sakii.factoryisland.blocks.Block;
import ml.sakii.factoryisland.blocks.BlockInventoryInterface;
import ml.sakii.factoryisland.entities.Entity;
import ml.sakii.factoryisland.entities.PlayerEntity;
import ml.sakii.factoryisland.items.ItemType;
import ml.sakii.factoryisland.items.PlayerInventory;

public class GameServer extends Thread{

	public HashMap<String,PlayerMPData> clients = new HashMap<>(); 

	
	private boolean serverRunning = true;
	
	private GameEngine Engine;
	
	public long tickCount = 0;

	public TCPListener Listener;
	public int port;
	public GameServer(GameEngine engine){
		Engine = engine;
		this.setName("GameServer");
		
		Listener = new TCPListener();
		Listener.start();
	}
	
	
	@Override
	public void run(){
		while(serverRunning){
			
			
			Packet packet = Listener.read();
			if(packet == null) {
				try
				{
					Thread.sleep(GameClient.CMDTIME);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				continue;
			}
			

			String message = packet.message;

			
			
			
			if(Main.devmode) {

					Main.log("(SERVER) RECEIVED:  "+message);
			}
			
			
			handleMessage(packet);
			

		}
		
		for(PlayerMPData client : clients.values()){
			sendData("98", client.socket);
		}
		Engine.world.saveByShutdown();
		Listener.kill();
	}
	
	


	private void handleMessage(Packet packet) {
		String message=packet.message;
		Connection socketstream = packet.connection;
		String[] part = message.split(",");
		String senderName = (part.length>1) ? part[1] : "";
		switch(part[0]){
			case "00": // LOGIN
				

				if(clients.containsKey(senderName)) {
					sendData("97", socketstream); // KICK FOR SAME USERNAME
					break;
				}
				
				
				Vector pos=null;
				float yaw;

				if(!senderName.equals(Config.username)) { // ha nem helyi, akkor elk�ldi a p�ly�t
				
					ArrayList<Block> Blocks =Engine.world.getWhole(false); 
					int size = Blocks.size();
					for(int i = 0; i<size; i= i+(Main.MP_PACKET_EACH)){
						String message2 = "";
						String message3 = "";
						for(int j = 0; j < (Main.MP_PACKET_EACH);j++){
							int index = i+j;
							if(index < size){
								Block b = Blocks.get(index);
								message2 += "01," + b.toString() + ",";
								if(b.BlockMeta.size()>0){
									for(Entry<String,String> metadata : b.BlockMeta.entrySet()){
										message3 += "08," + b.x + "," + b.y + "," + b.z + "," + metadata.getKey() + "," + metadata.getValue() + ",";
									}
								}
							}else{
								break;
							}
						}
						sendData(message2, socketstream);
						sendData(message3, socketstream);
						
					}
					
					
					Main.log(Math.ceil(size*1.0f / (Main.MP_PACKET_EACH)) + " sent.");
					
					

					PlayerInventory inv=null;
					
					//�s a pozici�j�t
					File playerFile = new File("saves/"+Engine.world.worldName+"/"+senderName+".xml");
					
					
					if(playerFile.exists()) {
						 pos = Engine.world.loadVector(senderName, "x", "y", "z");
						Vector dir = Engine.world.loadVector(senderName, "yaw", "pitch", "yaw");
						yaw=dir.x;
						sendData("11,"+pos.x+","+pos.y+","+pos.z+","+dir.x+","+dir.y, socketstream);
						
						//�s az inventoryt
						
						inv = Engine.world.loadInv(senderName, null);
						for(Entry<ItemType, Integer> is : inv.items.entrySet()) {
							sendData("10,server,"+is.getKey().name+","+is.getValue(), socketstream);
						}
						
						
						clients.put(senderName,new PlayerMPData(senderName, pos, dir.x, dir.y, inv, socketstream, false));
					}else {
						Block SpawnBlock = Engine.world.getSpawnBlock();
						 pos = new Vector(SpawnBlock.x, SpawnBlock.y, SpawnBlock.z+2.7f);
						 yaw=-135;
						sendData("11,"+pos.x+","+pos.y+","+pos.z+",-135,0", socketstream);
						
						
						
						clients.put(senderName,new PlayerMPData(senderName, pos, -135, 0, new PlayerInventory(Engine), socketstream, false));
					}
					
					//�s az entityket
					for(Entity e : Engine.world.getAllEntities()) {
						//15,className,x,y,z,yaw,pitch,name,ID
						if(!(e instanceof PlayerEntity))
							sendData("15,"+e.className+","+e.getPos()+","+e.ViewAngle+","+e.name+","+e.ID, socketstream);
					}
					
					//�s a blockok inventoryj�t
					for(Block b : Engine.world.getWhole(false)) {
						if(b instanceof BlockInventoryInterface) {
							for(Entry<ItemType,Integer> entry : ((BlockInventoryInterface) b).getInv().items.entrySet()) {
								sendData("13,"+b.x+","+b.y+","+b.z+","+entry.getKey().name+","+entry.getValue(), socketstream);
							}
						}
					}

					
				}else {	// most nyitottuk meg, kell a poz�ci�, de az inventory nem k�z�s a klienssel
					pos=new Vector(Float.parseFloat(part[2]), Float.parseFloat(part[3]), Float.parseFloat(part[4]));
					yaw= Float.parseFloat(part[5]);
					clients.put(senderName,new PlayerMPData(senderName,pos ,yaw, Float.parseFloat(part[6]), new PlayerInventory(Engine), socketstream, true));
				}
				
				sendData("loaded",socketstream);
				
				for(PlayerMPData player : clients.values()){
					if(player.socket != socketstream){
						sendData(("03," + senderName + "," + pos.x + "," + pos.y + "," + pos.z + "," + yaw), player.socket);
						sendData(("03," + player.username + "," + player.position.x + "," + player.position.y + "," + player.position.z + "," + player.aim.yaw), socketstream);
					}
					
				}
				
				


				break;
			
			case "66": // LOGOUT
				Main.log("(SERVER) Logging out user "+senderName+" ...");
				dropClient(senderName);
				
				break;
			
			case "04": // MOVE
				try {
					
					PlayerMPData player = clients.get(senderName);
					if(player == null){
						Main.err("Unknown player: " + senderName + " (all:"+clients.size()+")");
						break;
					}
					
					Vector newPos = new Vector(Float.parseFloat(part[2]), Float.parseFloat(part[3]), Float.parseFloat(part[4]));
					float newYaw = Float.parseFloat(part[5]);
					float newPitch = Float.parseFloat(part[6]);
					player.position.set(newPos);
					player.aim.set(newYaw, newPitch);
					for(PlayerMPData client : clients.values()){
						if(!client.username.equals(senderName))
							sendData(("04," + senderName + "," + newPos.x + "," + newPos.y + "," + newPos.z + "," + newYaw + "," + newPitch), client.socket);
					}
				}catch(Exception e) {
					Main.err("Bad player position: " + e.getMessage());
					Main.err(message);
				}

				break;
				
			
			case "05": // PLACE BLOCK
				Block b1 = Engine.createBlockByName(part[1], cInt(part[2]), cInt(part[3]), cInt(part[4]));
				for(int i=5;i<part.length;i+=2) {
					b1.setMetadata(part[i], part[i+1], false);
				}
				Engine.world.addBlockReplace(b1, false);
				
				//senderID = cInt(part[1]);
				for(PlayerMPData client : clients.values()){
					/*if(!client.username.equals(senderName)) {
						sendData(("05," + b1.x + "," + b1.y + "," + b1.z + "," + part[5]), client.socket);
						Main.log("Block place forwarded to "+client.username);
					}*/
					if(!client.local) {
						sendData(message, client.socket);

					}
				}
				break;
			case "06": // DELETE BLOCK
				/*Block b = Engine.world.getBlockAt(cInt(part[2]), cInt(part[3]), cInt(part[4]));
				if(b!=Block.NOTHING){
					if(b instanceof BreakListener){
						((BreakListener)b).breaked(senderName);//.breakedOnServer();
					}
					for(PlayerMPData data : clients.values()) {
						sendData("06," + cInt(part[2]) + "," + cInt(part[3]) + "," + cInt(part[4]), data.socket);
					}
					//Engine.world.destroyBlock(b);
				}*/
				
				Engine.world.destroyBlock(Engine.world.getBlockAt(cInt(part[1]),cInt(part[2]), cInt(part[3])), false);
				for(PlayerMPData client : clients.values()){
					if(!client.local) {
						sendData(message, client.socket);

					}
				}

				break;
			case "07": // EDIT METADATA
				Engine.world.getBlockAt(cInt(part[1]), cInt(part[2]), cInt(part[3])).setMetadata(part[4], part[5], false);
				for(PlayerMPData client : clients.values()){
					if(!client.local) {
						sendData(message, client.socket);

					}
				}
				break;
			case "10": // ADD TO INVENTORY
				clients.get(part[1]).inventory.add(Main.Items.get(part[2]), cInt(part[3]), false);
				sendData(message,clients.get(senderName).socket);
				break;
			case "13": // ADD TO BLOCK INVENTORY
				((BlockInventoryInterface)Engine.world.getBlockAt(cInt(part[2]), cInt(part[3]), cInt(part[4]))).getInv().add(Main.Items.get(part[5]), cInt(part[6]), false);
				//if(Main.GAME != null && Main.GAME.remoteInventory.getInv().items.size()==0) TODO ez kellhet
				//	Main.GAME.SwitchInventory(true);
				//clients.get(part[1]).inventory.addMore(Main.Items.get(part[2]), cInt(part[3]));
				for(PlayerMPData client : clients.values()){
					if(!client.username.equals(senderName)) {
						sendData(("13," + part[2] + "," + part[3] + "," + part[4] + "," + part[5] + "," + part[6]), client.socket);

						Main.log("Block inv insert forwarded to "+client.username);
					}
				}
				//sendData(message,socketstream);
				break;
			case "14": // SWAP BLOCKS
				boolean addToLocal = Boolean.parseBoolean(part[6]);
				clients.get(part[1]).inventory.add(Main.Items.get(part[5]), addToLocal?1:-1, false);
				((BlockInventoryInterface)Engine.world.getBlockAt(cInt(part[2]), cInt(part[3]), cInt(part[4]))).getInv().add(Main.Items.get(part[5]), addToLocal?-1:1, false);
				//for(PlayerMPData client : clients.values()){
					//if(client.username.equals(senderName)) {
						sendData(message, socketstream);

						//Main.log("item swap forwarded to "+client.username);
					//}
				//}
				break;
			case "15": // SPAWN ENTITY
				/*String className = part[1];
				Vector pos2=new Vector(Float.parseFloat(part[2]), Float.parseFloat(part[3]), Float.parseFloat(part[4]));
				EAngle aim=new EAngle(Float.parseFloat(part[5]), Float.parseFloat(part[6]));
				String name=part[7];
				long ID=Long.parseLong(part[8]);
				Entity e = Entity.createEntity(className, pos2, aim, name, ID); 
				Engine.world.addEntity(e);*/
				for(PlayerMPData client : clients.values()){
					if(!client.local) {
						sendData(message, client.socket);

					}
				}
				break;
			case "16": // MOVE ENTITY
				Entity e = Engine.world.getEntity(Long.parseLong(part[1]));
				if(e!=null) { // lehet h rossz sorrendben j�nnek a parancsok �s m�r meghalt
					e.move(Float.parseFloat(part[2]), Float.parseFloat(part[3]), Float.parseFloat(part[4]), false);
					for(PlayerMPData client : clients.values()){
						if(!client.local) {
							sendData(message, client.socket);
	
						}
					}
				}

				break;
			case "17": // KILL ENTITIY
				Engine.world.killEntity(Long.parseLong(part[1]), false);
				for(PlayerMPData client : clients.values()){
					if(!client.local) {
						sendData(message, client.socket);

					}
				}
				break;
			case "ping":
				sendData("pong", socketstream);
				break;
			default:
				Main.err("(SERVER) Unknown message received: "+message);
				break;
		}
		
	}


	@SuppressWarnings("static-method")
	public void sendData(String data, Connection socket){
		try {
			BufferedWriter outToClient = socket.outputStream;

			outToClient.write(data+GameClient.DELIMETER);
			outToClient.flush();
			if(Main.devmode) {
				if(GameClient.ALLCODES.contains(data.split(",")[0])){
					Main.log("(SERVER) SENT:      "+data);
				}else{
					Main.log("(SERVER) I DUNNO WAT I SENT LOL:  "+data);
				}
			}
		} catch (IOException e) {
			Main.err("(SERVER)" + e.getMessage());
			
			//dropClient(socket);
		}
	}
	
	private void dropClient(String name) {
		PlayerMPData playerData=clients.get(name);
		/*for(PlayerMPData client2 : clients.values()){
			if(client2.socket.equals(socket)) {
				playerData=client2;
				break;
			}
		}*/
		if(playerData != null) {
			for(PlayerMPData client : clients.values()){
				if(!playerData.username.equals(client.username))
					sendData(("67," + playerData.username), client.socket);
			}
			
			World.savePlayer(Engine.world.worldName, playerData.username, playerData.position, playerData.aim, playerData.inventory);
			clients.remove(playerData.username);
			Listener.Connections.remove(playerData.socket);
			Main.log(playerData.username+"Logged out ("+clients+")");
		}else {
			Main.err("Client "+ name+" couldn't have been removed ("+clients+")");
		}
			
		
		
		
		/*for(PlayerMPData mpdata : clients.values()) {
			if(mpdata.socket == socket) {
				clients.remove(mpdata.username);
				break;
			}
		}*/
		
		
		
		/*try {
			socket.socket.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}*/
		
		
	}
	
	/*private void dropClient(String username) {
		dropClient(clients.get(username).socket);
	}*/
	
	

	
	public void kill(){
		serverRunning=false;
	}
	
	



	
	


	private static int cInt(String data){
		try{
			return Integer.parseInt(data); 
		}catch(Exception e){
			return 0;
		}
	}
}
