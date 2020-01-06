package ml.sakii.factoryisland.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;

import ml.sakii.factoryisland.Config;
import ml.sakii.factoryisland.EAngle;
import ml.sakii.factoryisland.Game;
import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Main;
import ml.sakii.factoryisland.Vector;
import ml.sakii.factoryisland.World;
import ml.sakii.factoryisland.blocks.Block;
import ml.sakii.factoryisland.blocks.BlockInventoryInterface;
import ml.sakii.factoryisland.blocks.BreakListener;
import ml.sakii.factoryisland.entities.Entity;
import ml.sakii.factoryisland.entities.PlayerMP;
import ml.sakii.factoryisland.items.ItemType;


public class GameClient extends Thread{

	static final Set<String> ALLCODES = new HashSet<>(Arrays.asList(
			"00", "01", "02", "03","04", "05", "06", "07", "08","10","11", "14","15","16","17","97", "13", "66", "67" ,"98","18", "loaded", "ping", "pong"));
	
	static final long CMDTIME = 20; 
	
	private boolean connected = true;

	public int packetCount, blockcount;
	
	
	private GameEngine Engine;
	private Game game;
	Socket socket; // headles servernek tudnia kell
	private BufferedWriter outputStream;
	private BufferedReader inputStream;

	long pingTime;
	private boolean terrainLoaded=false;
	
	
	
	public GameClient(Game game, GameEngine Engine){
		this.Engine = Engine;
		this.game = game;
		this.setName("GameClient");

		
	}
	
	public String connect(String ipAddress, int port) {
		try {
			Main.log("Connecting... "+ipAddress+":"+port);
			socket = new Socket(InetAddress.getByName(ipAddress), port);
			outputStream = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			sendData("ping");
			sendData(("00,"+Config.username +","+Config.creative));

		
			while(!terrainLoaded) {
			
			
				String message;
				try {
					message = inputStream.readLine().trim();
				} catch (IOException e) {
					return e.getMessage();
				}
	
				if(message == null || message.isEmpty())
					continue;
				
	
				
				if(Main.devmode && !message.substring(0, 2).equals("16") && !message.substring(0, 2).equals("04")) {
						Main.log("(CLIENT:"+Config.username+") RECEIVED:  "+message);
				}
				
				String error = handleMessage(message);
				if(error != null) {
					return error;
				}
			}
			
			return null;
		} catch (Exception e) {
			return e.getMessage();
		}
		
	}

	
	@Override
	public void run(){

		while(connected){
			

			
			String message=null;
			try {
				if(inputStream.ready()) {
					message = inputStream.readLine().trim();
				}else {
					
				}
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			}

			if(message == null || message.isEmpty()) {
				try
				{
					Thread.sleep(CMDTIME);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				continue;
			}
			

			
			if(Main.devmode && !message.substring(0, 2).equals("16")) {
					Main.log("(CLIENT:"+Config.username+") RECEIVED:  "+message);
			}
			

			String error = handleMessage(message);
			if(error != null) {
				disconnect(error);
				break;
			}
			
			
			
		}
		
		exit();
		
		

	}
	
	private String handleMessage(String message) {
		String[] part = message.split(",");
		String otherName = (part.length>1) ? part[1] : "";
		switch(part[0]){
		
		case "00": // PROTOCOL VERSION CHECK
			if(!part[1].equals(""+Connection.PROTOCOL_VERSION)) {
				return "Incompatible server version: "+part[1] + "(current:"+Connection.PROTOCOL_VERSION+")";
			}
			Engine.world.Entities.remove(game.PE.ID);
			game.PE.ID = Long.parseLong(part[2]);
			Engine.world.Entities.put(game.PE.ID, game.PE);
			Engine.Tick=Long.parseLong(part[3]);
			break;
		
		case "01": // DOWNLOAD BLOCKS
			
			
			for(int i = 0;i<part.length;i=i+5){
				Block b = Engine.createBlockByName(part[i+1], cInt(part[i+2]), cInt(part[i+3]), cInt(part[i+4]));
				if(b != Block.NOTHING && b != null){
					Engine.world.addBlockNoReplace(b, false);
					blockcount++;
				}else{
					Main.log("Could not parse received values as a block: " + part[i+1]+","+cInt(part[i+2])+","+cInt(part[i+3])+","+cInt(part[i+4]));
				}
			}
			packetCount++;
			break;
			
		case "08": // DOWNLOAD METADATA
			
			for(int i = 0;i<part.length;i=i+6){
				
				Engine.world.getBlockAt(cInt(part[i+1]), cInt(part[i+2]), cInt(part[i+3])).BlockMeta.put(part[i+4], part[i+5]);
			}
			
			
			break;
		
		case "loaded":
			terrainLoaded = true;
			Engine.world.loading=false;
			Main.log("Terrain download from server completed ("+blockcount+" blocks / "+packetCount+" packets)");
			
			
			break;

		case "67": // DELETE PLAYER
			
			
			for(Entity e : Engine.world.getAllEntities()) {
				if(e.name.equals(otherName)) {
					if(Engine.server != null) {
						PlayerMP playerData = (PlayerMP)e;
						World.savePlayer(Engine.world.worldName, playerData.name, playerData.getPos(), playerData.ViewAngle, playerData.inventory, playerData.getHealth());
					}
					Engine.world.Entities.remove(e.ID);
					if(!Main.headless) {
						game.Objects.removeAll(e.Objects);
					}
				}
			}

			
			break;
		
		case "05": // PLACE BLOCK
			receiveBlockPlace(part);
			break;
		case "06": // DELETE BLOCK
			receiveBlockDestroy(part);
			break;
		case "07": // EDIT METADATA
			receiveMetadataEdit(part);
			break;
		case "10": // ADD TO PLAYER INVENTORY
			receiveInvPlayerAdd(part);
			break;
		case "13": // ADD TO BLOCK INVENTORY
			receiveInvBlockAdd(part);
			break;
		case "14": // SWAP BLOCKS 14,Sakii,1,2,3,Stone,true (add to local)
			receiveInvSwap(part);
			break;
		case "98": // SERVER CLOSED
			if(Engine.server == null) {
				return "Server closed";
			}
			break;
		case "97": // SAME USERNAME
			return "Someone with the same username already logged in";
		case "11": //FORCE MOVE ON CONNECT
			game.PE.move(Float.parseFloat(part[1]), Float.parseFloat(part[2]), Float.parseFloat(part[3]), false);
			game.PE.ViewAngle.yaw = Float.parseFloat(part[4]);
			game.PE.ViewAngle.pitch = Float.parseFloat(part[5]);
			game.PE.setHealth((int)Float.parseFloat(part[6]));
			game.moved=true;
			break;
		case "15": // SPAWN ENTITY 15,className,x,y,z,yaw,pitch,name,health,ID
			receiveEntityCreate(part);
			break;
		case "16": // MOVE ENTITY
			receiveEntityMove(part);
			break;
		case "18": // HURT ENTITY
			receiveEntityHurt(part);
			break;
		case "pong":
			Main.log("ping time: " + (System.currentTimeMillis()-pingTime) + " ms");
			break;
		case "66": // CONFIRM DISCONNECT
			terrainLoaded=true;
			connected=false;			
			break;
		default:
			Main.err("(CLIENT) Unknown message received: "+message);
			break;
		}
		return null;
		
	}
	
	
	public void sendBlockPlace(String username, Block b) {
		StringBuilder sb = new StringBuilder();
		sb.append("05,"+username+","+b.name+","+b.x+","+b.y+","+b.z);
		for(Entry<String,String> entry : b.BlockMeta.entrySet()) {
			sb.append(",");
			sb.append(entry.getKey()+","+entry.getValue());
		}
		sendData(sb.toString());
		
		
	}
	
	void receiveBlockPlace(String[] part) {
		Block b = Engine.createBlockByName(part[2], cInt(part[3]), cInt(part[4]), cInt(part[5]));
		for(int i=6;i<part.length;i+=2) {
			b.setMetadata(part[i], part[i+1], false);
		}
		Engine.world.addBlockNoReplace(b,false);

	}
	
	public void sendBlockDestroy(String username, Block b) {
		sendData(("06,"+username+"," + b.x + "," + b.y	+ "," + b.z));

	}
	
	public void receiveBlockDestroy(String[] part) {
		Block b = Engine.world.getBlockAt(cInt(part[2]),cInt(part[3]), cInt(part[4]));
		if(b instanceof BreakListener) {
			((BreakListener) b).breaked(part[1]);
		}
		Engine.world.destroyBlock(b, false);

	}
	
	public void sendMetadataEdit(Block b, String key, String value) {
		sendData(("07," + b.x + "," + b.y + "," + b.z + ","	+ key + "," + value));
	}
	
	void receiveMetadataEdit(String[] part) {
		Block bl = Engine.world.getBlockAt(cInt(part[1]), cInt(part[2]), cInt(part[3]));
		if(bl != Block.NOTHING) {
			bl.setMetadata(part[4], part[5], false);
		}else {
			Main.err("Metadata edit on empty block");
		}
	}
	
	public static String constructEntityCreate(Entity e) {
		return "15,"+e.className+","+e.getPos()+","+e.ViewAngle.yaw+","+e.ViewAngle.pitch+","+e.name+","+e.getHealth()+","+e.ID;
	}
	
	public void sendEntityCreate(Entity e) {
		sendData(constructEntityCreate(e));
	}
	
	void receiveEntityCreate(String[] part) {
		
		String className = part[1];
		Vector pos=new Vector(Float.parseFloat(part[2]), Float.parseFloat(part[3]), Float.parseFloat(part[4]));
		EAngle aim=new EAngle(Float.parseFloat(part[5]), Float.parseFloat(part[6]));
		String name=part[7];
		int health = Integer.parseInt(part[8]);
		long ID=Long.parseLong(part[9]);
		if(game != null && name.equals(game.PE.name)) { // respawnnal ne uj entityt hozzon letre
			Engine.world.addEntity(game.PE, false);
			Main.log("(CLIENT:"+Config.username+") Ignored entity create on respawn ("+game.PE.name+"->"+name+")");
		}else {
			Entity e = Entity.createEntity(className, pos, aim, name,health, ID, Engine); 
			Engine.world.addEntity(e, false);
		}

	}
	
	public static String constructEntityMove(long ID, float x, float y, float z, float yaw, float pitch) {
		return "16,"+ID+","+x+","+y+","+z+","+yaw+","+pitch; 
	}
	
	
	public void sendEntityMove(long ID, float x, float y, float z, float yaw, float pitch) {
		sendData(constructEntityMove(ID,x, y, z, yaw, pitch)); 
	}
	
	void receiveEntityMove(String[] part) {
		for(int i=1;i<part.length;i+=6) {
			long parsedID = Long.parseLong(part[i]);
			Entity en = Engine.world.getEntity(parsedID);
			if(en!=null) {
				en.move(Float.parseFloat(part[i+1]), Float.parseFloat(part[i+2]), Float.parseFloat(part[i+3]), false);
				en.ViewAngle.yaw = Float.parseFloat(part[i+4]);
				en.ViewAngle.pitch = Float.parseFloat(part[i+5]);
				en.update();
			}else {
				Main.err("(CLIENT:"+Config.username+") Entity already null ("+parsedID+"): "+part);
			}
		}
	}
	
	public void sendEntityHurt(long ID, int points) {
		sendData("18,"+ID+","+points);

	}
	
	void receiveEntityHurt(String[] part) {
		Engine.world.hurtEntity(Long.parseLong(part[1]),Integer.parseInt(part[2]),false);
	}
	

	
	public void sendInvSwap(String username, Block b, String itemName, boolean addToLocal) {
		 // 14,Sakii,1,2,3,Stone,true (add to local)
		sendData("14," + username + "," + b.x + "," + b.y + "," + b.z + "," + itemName + "," + addToLocal);
	}
	
	void receiveInvSwap(String[] part) {
		boolean addToLocal = Boolean.parseBoolean(part[6]);
		for(Entity e : Engine.world.getAllEntities()) {
			if(e.name.equals(part[1]) && e instanceof PlayerMP) {
				((PlayerMP)e).inventory.add(Main.Items.get(part[5]), addToLocal ? 1 : -1, false);
				break;
			}
		}
		((BlockInventoryInterface)Engine.world.getBlockAt(cInt(part[2]), cInt(part[3]), cInt(part[4]))).getInv().add(Main.Items.get(part[5]), addToLocal ? -1 : 1, false);
	}
	
	public void sendInvBlockAdd(String username, Block b, String name, int amount) {
		sendData("13,"+username+","+b.x+","+b.y+","+b.z+","+name+","+amount);

	}
	
	void receiveInvBlockAdd(String[] part) {
		((BlockInventoryInterface)Engine.world.getBlockAt(cInt(part[2]), cInt(part[3]), cInt(part[4]))).getInv().add(Main.Items.get(part[5]), cInt(part[6]), false);
	}
	
	public void sendInvPlayerAdd(String username, ItemType kind, int amount) {
		sendData("10,"+username+","+kind.name+","+amount);
	}
	
	void receiveInvPlayerAdd(String[] part) {
		for(Entity e : Engine.world.getAllEntities()) {
			if(e.name.equals(part[1]) && e instanceof PlayerMP) {
				((PlayerMP)e).inventory.add(Main.Items.get(part[2]), cInt(part[3]), false);
				break;
			}
		}
	}
	
	public void sendPlayerPos() {
		Vector pos = game.PE.getPos();
		EAngle aim = game.PE.ViewAngle;
		sendEntityMove(game.PE.ID, pos.x, pos.y, pos.z, aim.yaw, aim.pitch);
	}
	
	public void sendData(String data){
		if(data.isEmpty()) {
			Main.log("empty message from client");
			return;
		}
		try {
			if(data.equals("ping")) {
				pingTime = System.currentTimeMillis();
			}

			outputStream.write(data);
			outputStream.newLine();
			outputStream.flush();
			
			if(Main.devmode) {
				String code = data.split(",")[0];
				if(ALLCODES.contains(code)){
					if(!code.equals("16")) {
						Main.log("(CLIENT:"+Config.username+") SENT:      "+data);
					}
				}else if(!data.isEmpty()){
					Main.log("(CLIENT:"+Config.username+") I DUNNO WAT I SENT LOL:  "+data);
				}
			}

		} catch (IOException e) {
			disconnect(e.getMessage());
		}
		
		
		
	}
	
	private void disconnect(String error) {
		if(Main.headless) {
			Engine.disconnect(error);
		}else {
			game.disconnect(error);
		}
	}
	
	public void kill(boolean send66){
		if(send66) {
			Main.log("sending 66");
	
			sendData(("66," + Config.username));
			
		}else {
		
			terrainLoaded=true;
			connected=false;
		}
		try {
			this.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
	private void exit() {
		try {
			inputStream.close();
			outputStream.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Main.log("connecting closed");
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
