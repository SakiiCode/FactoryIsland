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
import ml.sakii.factoryisland.blocks.Block;
import ml.sakii.factoryisland.blocks.BlockInventoryInterface;
import ml.sakii.factoryisland.entities.Entity;
import ml.sakii.factoryisland.entities.PlayerMP;
import ml.sakii.factoryisland.items.ItemType;
import ml.sakii.factoryisland.items.PlayerInventory;

public class GameServer extends Thread{

	public HashMap<String,PlayerMP> clients = new HashMap<>(); //ugyanaz mint az Entities csak kizarolag PlayerMP-kre
	
	private boolean serverRunning = true;
	
	private GameEngine Engine;
	
	public long tickCount = 0;

	public TCPListener Listener;
	
	
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
			
			try {
				handleMessage(packet);
			}catch(Exception e) {
				Main.err("(SERVER) Error parsing message: "+ message);
				e.printStackTrace();
			}
			

		}
		Engine.world.saveByShutdown();
		
		for(PlayerMP client : clients.values()){
			sendData("98", client.socket);
		}
		
		Listener.kill();
	}
	
	


	private void handleMessage(Packet packet) {
		String message=packet.message;
		Connection conn = packet.connection;
		String[] part = message.split(",");
		String senderName = (part.length>1) ? part[1] : "";
		switch(part[0]){
			case "00": // LOGIN
				

				if(clients.containsKey(senderName)) {
					sendData("97", conn); // KICK FOR SAME USERNAME
					break;
				}
				
				long ID = new Random().nextLong();
				sendData("00,"+Connection.PROTOCOL_VERSION+","+ID+","+Engine.Tick, conn);

				
				PlayerMP playerE;


				if(!senderName.equals(Config.username)) { // ha nem helyi, akkor elküldi a pályát
				
					ArrayList<Block> Blocks =Engine.world.getWhole(false); 
					int size = Blocks.size();
					for(int i = 0; i<size; i= i+(Main.MP_PACKET_EACH)){
						StringBuilder message2=new StringBuilder();
						StringBuilder message3=new StringBuilder();
						for(int j = 0; j < (Main.MP_PACKET_EACH);j++){
							int index = i+j;
							if(index < size){
								Block b = Blocks.get(index);
								message2.append("01," + b.toString() + ",");
								if(b.BlockMeta.size()>0){
									for(Entry<String,String> metadata : b.BlockMeta.entrySet()){
										message3.append("08," + b.x + "," + b.y + "," + b.z + "," + metadata.getKey() + "," + metadata.getValue() + ",");
									}
								}
							}else{
								break;
							}
						}
						sendData(message2.toString(), conn);
						sendData(message3.toString(), conn);
						
					}
					
					
					Main.log(Math.ceil(size*1.0f / (Main.MP_PACKET_EACH)) + " sent.");
					
					

										
					File playerFile = new File("saves/"+Engine.world.worldName+"/"+senderName+".xml");
					
					if(playerFile.exists()) {
						Vector pos = new Vector().set(Engine.world.loadVector(senderName, new String[] {"x", "y", "z"}));
						float[] other = Engine.world.loadVector(senderName, new String[] { "yaw", "pitch", "health"});
						
						
						playerE = new PlayerMP(senderName, pos, other[0], other[1],(int)other[2], Engine.world.loadInv(senderName, Engine), conn, ID, Engine);

						//force move
						sendData("11,"+pos.x+","+pos.y+","+pos.z+","+other[0]+","+other[1]+","+other[2], conn);
						
						for(Entry<ItemType, Integer> is : playerE.inventory.items.entrySet()) {
							// add to player inv
							sendData("10,"+senderName+","+is.getKey().name+","+is.getValue(), conn);
						}
						
						
	
						
						
					}else {
						Block SpawnBlock = Engine.world.getSpawnBlock();
						Vector pos = new Vector(SpawnBlock.x, SpawnBlock.y, SpawnBlock.z+2.7f);
						
						sendData("11,"+pos.x+","+pos.y+","+pos.z+",-135,0,20", conn);
						

						playerE = new PlayerMP(senderName, pos, -135, 0,20,  new PlayerInventory(senderName, Engine), conn, ID, Engine);
						
						
					}
					
					
					//és az entityket
					for(Entity e : Engine.world.getAllEntities()) {
						//15,className,x,y,z,yaw,pitch,name,ID
						if(!(e instanceof PlayerMP))
							sendData("15,"+e.className+","+e.getPos()+","+e.ViewAngle+","+e.name+","+e.getHealth()+","+e.ID, conn);
					}
					
					//és a blockok inventoryját
					for(Block b : Engine.world.getWhole(false)) {
						if(b instanceof BlockInventoryInterface) {
							for(Entry<ItemType,Integer> entry : ((BlockInventoryInterface) b).getInv().items.entrySet()) {
								sendData("13,"+b.x+","+b.y+","+b.z+","+entry.getKey().name+","+entry.getValue(), conn);
							}
						}
					}

					
				}else {	
					playerE=Main.GAME.PE;
					playerE.socket=conn;
				}


				//es a tobbi jatekost
				for(PlayerMP client : clients.values()){

						sendData(GameClient.constructEntityCreate(playerE), client.socket);

						sendData(GameClient.constructEntityCreate(client), conn);
						

					
				}
				
				

				clients.put(senderName,playerE);

				
				sendData("loaded",conn);



				break;
			
			case "66": // LOGOUT
				Main.log("(SERVER) Logging out user "+senderName+" ...");
				sendData("66",conn);
				dropClient(senderName);
				
				break;
			
			case "04": // MOVE (unused)
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
					player.ViewAngle.yaw=(newYaw);
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

					for(PlayerMP client : clients.values()){
						if(!client.name.equals(senderName)) {
							sendData(message, client.socket);
	
						}
					}
				break;
			case "06": // DELETE BLOCK

					for(PlayerMP client : clients.values()){
						if(!client.name.equals(senderName)) {
							sendData(message, client.socket);
	
						}
					}

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
				for(PlayerMP client : clients.values()){
					sendData(message, client.socket);
				}
				break;
			case "13": // ADD TO BLOCK INVENTORY TODO még elvileg nem megy
				((BlockInventoryInterface)Engine.world.getBlockAt(cInt(part[2]), cInt(part[3]), cInt(part[4]))).getInv().add(Main.Items.get(part[5]), cInt(part[6]), false);
				for(PlayerMP client : clients.values()){
					if(!client.name.equals(senderName)) {
						sendData(("13," + part[2] + "," + part[3] + "," + part[4] + "," + part[5] + "," + part[6]), client.socket);

						Main.log("Block inv insert forwarded to "+client.name);
					}
				}
				break;
			case "14": // SWAP BLOCKS
				for(PlayerMP client : clients.values()){
					sendData(message, client.socket);
					Main.log("item swap forwarded to "+senderName);
				}
				break;
			case "15": // SPAWN ENTITY
				for(PlayerMP client : clients.values()){
						sendData(message, client.socket);
				}
				break;
			case "16": // MOVE ENTITY
				long parsedID = Long.parseLong(part[1]);
				Entity e = Engine.world.getEntity(parsedID);
				if(e!=null) { 
					for(PlayerMP client : clients.values()){
						if(client.socket != conn) {
							sendData(message, client.socket);
	
						}
					}
				}else {
					Main.err("(SERVER) Entity already null ("+parsedID+"): "+message);
					Main.err("(SERVER) Current entities:"+Engine.world.Entities.toString());
				}

				break;
			case "18": // HURT ENTITIY
				for(PlayerMP client : clients.values()){
						sendData(message, client.socket);
				}
				break;
			case "ping":
				sendData("pong", conn);
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
			String target = "UNKNOWN";
			for(PlayerMP client : clients.values()) {
				if(client.socket == socket) {
					target = client.name;
				}
			}
			if(Main.devmode) {
				if(GameClient.ALLCODES.contains(data.split(",")[0])){
					if(!data.substring(0, 2).equals("16"))
					Main.log("(SERVER->"+target+") SENT:      "+data);
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
		if(playerData != null) {
			for(PlayerMP client : clients.values()){
				if(!playerData.name.equals(client.name))
					sendData(("67," + playerData.name), client.socket);
			}
			
			clients.remove(playerData.name);
			Listener.Connections.remove(playerData.socket);
			Main.log(playerData.name+" logged out ("+playerData+")");
		}else {
			Main.err("Client "+ name+" couldn't be removed ("+clients+")");
		}
		
	}
	
	

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
