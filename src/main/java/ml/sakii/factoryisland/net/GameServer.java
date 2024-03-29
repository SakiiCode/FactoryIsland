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
import ml.sakii.factoryisland.Globals;
import ml.sakii.factoryisland.Main;
import ml.sakii.factoryisland.Vector;
import ml.sakii.factoryisland.World;
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

			
			
			
			if(Main.verbose && !message.substring(0, 2).equals("16") && !message.substring(0, 2).equals("04")) {

					Main.log("(SERVER) RECEIVED:  "+message);
			}
			
			try {
				handleMessage(packet);
			}catch(Exception e) {
				Main.err("(SERVER) Error parsing message: "+ message);
				e.printStackTrace();
			}
			

		}
		Engine.world.saveByShutdown(true);
		
		for(PlayerMP client : clients.values()){ // helyi jatekos ilyenkor mar ki van lepve
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
				
					ArrayList<Block> Blocks = new ArrayList<>();
					Blocks.addAll(Engine.world.getWhole()); 
					int size = Blocks.size();
					for(int i = 0; i<size; i= i+(Globals.MP_PACKET_EACH)){
						StringBuilder message2=new StringBuilder();
						StringBuilder message3=new StringBuilder();
						for(int j = 0; j < (Globals.MP_PACKET_EACH);j++){
							int index = i+j;
							if(index < size){
								Block b = Blocks.get(index);
								message2.append("01," + b.toString() + ",");
									for(Entry<String,String> metadata : b.getAllMetadata()){
										message3.append("08," + b.x + "," + b.y + "," + b.z + "," + metadata.getKey() + "," + metadata.getValue() + ",");
									}
							}else{
								break;
							}
						}
						sendData(message2.toString(), conn);
						sendData(message3.toString(), conn);
						
					}
					
					
					Main.log(Math.ceil(size*1.0f / (Globals.MP_PACKET_EACH)) + " sent.");
					
					

										
					File playerFile = new File("saves/"+Engine.world.worldName+"/"+senderName+".xml");
					
					if(playerFile.exists()) {
						Vector pos = new Vector().set(Engine.world.loadVector(senderName, new String[] {"x", "y", "z"}));
						float[] other = Engine.world.loadVector(senderName, new String[] { "yaw", "pitch", "health"});

						//force move
						sendData("11,"+pos.x+","+pos.y+","+pos.z+","+other[0]+","+other[1]+","+other[2], conn);
						
						Boolean creative = Boolean.parseBoolean(part[2]);
						
						
						playerE = new PlayerMP(senderName, pos, other[0], other[1],(int)other[2], creative ? PlayerInventory.Creative : World.loadInv(senderName, Engine.world.worldName, new PlayerInventory(senderName,Engine)), conn, ID, Engine);


						
						
						
						if(!creative) {
							for(Entry<ItemType, Integer> is : playerE.inventory.items.entrySet()) {
								// add to player inv
								sendData("10,"+senderName+","+is.getKey().name+","+is.getValue(), conn);
							}
						}
						
						
	
						
						
					}else {
						Block SpawnBlock = Engine.world.getSpawnBlock();
						Vector pos = new Vector(SpawnBlock.x, SpawnBlock.y, SpawnBlock.z+2.7f);
						
						sendData("11,"+pos.x+","+pos.y+","+pos.z+",-135,0,20", conn);
						

						playerE = new PlayerMP(senderName, pos, -135, 0,20,  new PlayerInventory(senderName, Engine), conn, ID, Engine);
						
						
					}
					
					
					//és az entityket
					for(Entity e : Engine.world.getAllEntities()) {
						//15,className,x,y,z,yaw,pitch,name,health,ID
						if(!(e instanceof PlayerMP))
							sendData("15,"+e.className+","+e.getPos()+","+e.ViewAngle+","+e.name+","+e.getHealth()+","+e.ID, conn);
					}
					
					//és a blokkok inventoryját
					for(Block b : Engine.world.getWhole()) {
						if(b instanceof BlockInventoryInterface bii) {
							for(Entry<ItemType,Integer> entry : bii.getInv().items.entrySet()) {
								sendData("13,SERVER,"+b.x+","+b.y+","+b.z+","+entry.getKey().name+","+entry.getValue(), conn);
							}
						}
					}

					
				}else {	// helyi
					if(Main.headless) {
						playerE=PlayerMP.ServerPerson;
					}else {
						playerE=Engine.game.PE;
					}
					playerE.socket=conn;
				}

				//es a tobbi jatekost, headlessnel ez eloszor ures, utana a headlesst nem kuldjuk tovabb
				for(PlayerMP client : clients.values()){
					sendData(GameClient.constructEntityCreate(playerE), client.socket);
					if(client != PlayerMP.ServerPerson) {
						sendData(GameClient.constructEntityCreate(client), conn);
					}
				}
				
				//server gameclient-jenek el kell kuldeni a jatekos meglevo inventoryjat, de korabban nem lehet, mert ott meg nem spawnolt
				if(Main.headless && playerE != PlayerMP.ServerPerson) {
					for(Entry<ItemType, Integer> is : playerE.inventory.items.entrySet()) {
						// add to player inv
						sendData("10,"+senderName+","+is.getKey().name+","+is.getValue(), PlayerMP.ServerPerson.socket);	
					}
				}
				
				clients.put(senderName,playerE);

				
				sendData("loaded",conn);



				break;
			
			case "66": // LOGOUT
				Main.log("(SERVER) Logging out user "+senderName+" ...");
				sendData("66",conn);
				dropClient(senderName);
				
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
			case "ping":
				sendData("pong", conn);
				break;
			default: // everything else
				for(PlayerMP client : clients.values()){
					sendData(message, client.socket);
				}
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
			if(Main.verbose) {
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
		try {
			this.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	

}
