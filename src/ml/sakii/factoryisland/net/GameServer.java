package ml.sakii.factoryisland.net;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;

import ml.sakii.factoryisland.Config;
import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Main;
import ml.sakii.factoryisland.Vector;
import ml.sakii.factoryisland.World;
import ml.sakii.factoryisland.blocks.Block;
import ml.sakii.factoryisland.blocks.BlockInventoryInterface;
import ml.sakii.factoryisland.entities.Entity;
import ml.sakii.factoryisland.entities.PlayerEntity;
import ml.sakii.factoryisland.entities.PlayerMP;
import ml.sakii.factoryisland.items.ItemType;
import ml.sakii.factoryisland.items.PlayerInventory;

public class GameServer extends Thread{

	public HashMap<String,PlayerMP> clients = new HashMap<>(); 
	//public ArrayList<PlayerMP> clients = new ArrayList<>();
	
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

			
			
			
			if(Main.devmode && !message.substring(0, 2).equals("16") && !message.substring(0, 2).equals("04")) {

					Main.log("(SERVER) RECEIVED:  "+message);
			}
			
			handleMessage(packet);
			
			

		}
		Engine.world.saveByShutdown();
		
		for(PlayerMP client : clients.values()){
			sendData("98", client.socket);
		}
		
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
				
				long ID = new Random().nextLong();
				sendData("00,"+Connection.PROTOCOL_VERSION+","+ID, socketstream);
				
				
				Vector pos=null;
				float yaw;
				
				


				if(!senderName.equals(Config.username)) { // ha nem helyi, akkor elküldi a pályát
				
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
					
					//és a pozicióját
					File playerFile = new File("saves/"+Engine.world.worldName+"/"+senderName+".xml");
					
					
					if(playerFile.exists()) {
						pos = new Vector().set(Engine.world.loadVector(senderName, new String[] {"x", "y", "z"}));
						float[] other = Engine.world.loadVector(senderName, new String[] { "yaw", "pitch", "health"});
						
						Vector dir= new Vector().set(other[0], other[1], 0);
						yaw=dir.x;
						
						int health = (int) other[2];
						sendData("11,"+pos.x+","+pos.y+","+pos.z+","+dir.x+","+dir.y+","+health, socketstream);
						
						//és az inventoryt
						
						inv = Engine.world.loadInv(senderName, null);
						for(Entry<ItemType, Integer> is : inv.items.entrySet()) {
							sendData("10,server,"+is.getKey().name+","+is.getValue(), socketstream);
						}
						
						
						//clients.put(senderName,new PlayerMP(senderName, pos, dir.x, dir.y, inv, socketstream, false));
						clients.put(senderName, new PlayerMP(senderName, pos, dir.x, dir.y,health, inv, socketstream, new Random().nextLong(), Engine));
					}else {
						Block SpawnBlock = Engine.world.getSpawnBlock();
						 pos = new Vector(SpawnBlock.x, SpawnBlock.y, SpawnBlock.z+2.7f);
						 yaw=-135;
						sendData("11,"+pos.x+","+pos.y+","+pos.z+",-135,0,20", socketstream);
						
						
						
						//clients.put(senderName,new PlayerMP(senderName, pos, -135, 0, new PlayerInventory(Engine), socketstream, false));
						clients.put(senderName, new PlayerMP(senderName, pos, -135, 0,20, new PlayerInventory(Engine), socketstream, new Random().nextLong(), Engine));
					}
					
					//és az entityket
					for(Entity e : Engine.world.getAllEntities()) {
						//15,className,x,y,z,yaw,pitch,name,ID
						if(!(e instanceof PlayerEntity))
							sendData("15,"+e.className+","+e.getPos()+","+e.ViewAngle+","+e.name+","+e.getHealth()+","+e.ID, socketstream);
					}
					
					//és a blockok inventoryját
					for(Block b : Engine.world.getWhole(false)) {
						if(b instanceof BlockInventoryInterface) {
							for(Entry<ItemType,Integer> entry : ((BlockInventoryInterface) b).getInv().items.entrySet()) {
								sendData("13,"+b.x+","+b.y+","+b.z+","+entry.getKey().name+","+entry.getValue(), socketstream);
							}
						}
					}

					
				}else {	// most nyitottuk meg, kell a pozíció, de az inventory nem közös a klienssel TODO tesztelni
					pos=new Vector(Float.parseFloat(part[2]), Float.parseFloat(part[3]), Float.parseFloat(part[4]));
					yaw= Float.parseFloat(part[5]);
					float pitch =  Float.parseFloat(part[6]);
					int health=(int) Float.parseFloat(part[7]);
					clients.put(senderName,new PlayerMP(senderName,pos ,yaw,pitch,health, new PlayerInventory(Engine), socketstream, ID,Engine));
					if(!Config.creative) {
						clients.get(senderName).inventory.items.putAll(Engine.Inv.items);
					}
				}
				
				sendData("loaded",socketstream);
				
				for(PlayerMP player : clients.values()){
					if(player.socket != socketstream){
						sendData(("03," + senderName + "," + pos.x + "," + pos.y + "," + pos.z + "," + yaw+ "," + ID), player.socket);
						sendData(("03," + player.name + "," + player.getPos().x + "," + player.getPos().y + "," + player.getPos().z + "," + player.yaw + "," + player.ID), socketstream);
					}
					
				}
				
				


				break;
			
			case "66": // LOGOUT
				Main.log("(SERVER) Logging out user "+senderName+" ...");
				sendData("66",socketstream);
				dropClient(senderName);
				
				break;
			
			case "04": // MOVE
				try {
					
					PlayerMP player = clients.get(senderName);
					if(player == null){
						Main.err("Unknown player: " + senderName + " (all:"+clients.size()+")");
						break;
					}
					
					Vector newPos = new Vector(Float.parseFloat(part[2]), Float.parseFloat(part[3]), Float.parseFloat(part[4]));
					float newYaw = Float.parseFloat(part[5]);
					float newPitch = Float.parseFloat(part[6]);
					player.getPos().set(newPos);
					player.yaw=(newYaw);
					for(PlayerMP client : clients.values()){
						if(!client.name.equals(senderName))
							sendData(("04," + senderName + "," + newPos.x + "," + newPos.y + "," + newPos.z + "," + newYaw + "," + newPitch), client.socket);
					}
				}catch(Exception e) {
					Main.err("Bad player position: " + e.getMessage());
					Main.err(message);
				}

				break;
				
			
			case "05": // PLACE BLOCK
				/*Block b1 = Engine.createBlockByName(part[1], cInt(part[2]), cInt(part[3]), cInt(part[4]));
				for(int i=5;i<part.length;i+=2) {
					b1.setMetadata(part[i], part[i+1], false);
				}
				boolean success = Engine.world.addBlockNoReplace(b1, false);//TODO itt replace volt
				*/
				//senderID = cInt(part[1]);
				//if(success)
					for(PlayerMP client : clients.values()){
						/*if(!client.username.equals(senderName)) {
							sendData(("05," + b1.x + "," + b1.y + "," + b1.z + "," + part[5]), client.socket);
							Main.log("Block place forwarded to "+client.username);
						}*/
						//if(!client.local) {
						if(!client.name.equals(senderName)) {
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
				/*Block b = Engine.world.getBlockAt(cInt(part[1]),cInt(part[2]), cInt(part[3]));
				if(b != Block.NOTHING) {
					Engine.world.destroyBlock(b, false);*/
					for(PlayerMP client : clients.values()){
						if(!client.name.equals(senderName)) {
							sendData(message, client.socket);
	
						}
					}
				/*}else {
					Main.err("(SERVER) Client tried to destroy air block:" + message);
				}*/

				break;
			case "07": // EDIT METADATA
				Block bl = Engine.world.getBlockAt(cInt(part[1]), cInt(part[2]), cInt(part[3]));
				if(bl != Block.NOTHING) {
					
					bl.setMetadata(part[4], part[5], false);
				}
				for(PlayerMP client : clients.values()){
					if(!client.name.equals(Config.username)) {
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
				for(PlayerMP client : clients.values()){
					if(!client.name.equals(senderName)) {
						sendData(("13," + part[2] + "," + part[3] + "," + part[4] + "," + part[5] + "," + part[6]), client.socket);

						Main.log("Block inv insert forwarded to "+client.name);
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
				for(PlayerMP client : clients.values()){
					if(!client.name.equals(Config.username)) {
						sendData(message, client.socket);

					}
				}
				break;
			case "16": // MOVE ENTITY
				Entity e = Engine.world.getEntity(Long.parseLong(part[1]));
				if(e!=null) { // lehet h rossz sorrendben jönnek a parancsok és már meghalt
					e.move(Float.parseFloat(part[2]), Float.parseFloat(part[3]), Float.parseFloat(part[4]), false);
					for(PlayerMP client : clients.values()){
						if(!client.name.equals(Config.username)) {
							sendData(message, client.socket);
	
						}
					}
				}

				break;
			/*case "17": // KILL ENTITIY
				//Engine.world.killEntity(Long.parseLong(part[1]), false);
				for(PlayerMP client : clients.values()){
					//if(!client.name.equals(Config.username)) {
						sendData(message, client.socket);

					//}
				}
				break;*/
			case "18": // HURT ENTITIY
				Engine.world.getEntity(Long.parseLong(part[1])).hurt(Integer.parseInt(part[2]),false);
				for(PlayerMP client : clients.values()){
					if(!client.name.equals(Config.username)) {
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


	public String sendData(String data, Connection socket){
		String error=null;
		try {
			BufferedWriter outToClient = socket.outputStream;

			outToClient.write(data);
			outToClient.newLine();
			outToClient.flush();
			if(Main.devmode) {
				if(GameClient.ALLCODES.contains(data.split(",")[0])){
					if(!data.substring(0, 2).equals("16"))
					Main.log("(SERVER) SENT:      "+data);
				}else{
					Main.log("(SERVER) I DUNNO WAT I SENT LOL:  "+data);
				}
			}
			
		} catch (IOException e) {
			Main.err("(SERVER) " + e.getMessage() + "("+data+")");
			error=e.getMessage();
		}
		return error;
	}
	
	private void dropClient(String name) {
		PlayerMP playerData=clients.get(name);
		/*for(PlayerMPData client2 : clients.values()){
			if(client2.socket.equals(socket)) {
				playerData=client2;
				break;
			}
		}*/
		if(playerData != null) {
			for(PlayerMP client : clients.values()){
				if(!playerData.name.equals(client.name))
					sendData(("67," + playerData.name), client.socket);
			}
			
			World.savePlayer(Engine.world.worldName, playerData.name, playerData.getPos(), playerData.ViewAngle, playerData.inventory, playerData.getHealth());
			clients.remove(playerData.name);
			Listener.Connections.remove(playerData.socket);
			Main.log(playerData.name+"Logged out ("+clients+")");
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
		}catch(@SuppressWarnings("unused") Exception e){
			Main.err("Incompatible int conversion: "+data);
			return 0;
		}
	}
}
