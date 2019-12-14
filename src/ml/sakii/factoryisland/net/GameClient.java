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
import ml.sakii.factoryisland.Main;
import ml.sakii.factoryisland.Vector;
import ml.sakii.factoryisland.blocks.Block;
import ml.sakii.factoryisland.blocks.BlockInventoryInterface;
import ml.sakii.factoryisland.entities.Entity;
import ml.sakii.factoryisland.entities.PlayerMP;


public class GameClient extends Thread{

	/*public static final Set<String> CODES = new HashSet<>(Arrays.asList(
			"00", "02", "05", "06", "07", "66", "67" ,"98", "01", "08", "03","10","11","12", "loaded", "ping", "pong"));*/
	
	static final Set<String> ALLCODES = new HashSet<>(Arrays.asList(
			"00", "01", "02", "03","04", "05", "06", "07", "08","10","11", "14","15","16","17","97", "13", "66", "67" ,"98", "loaded", "ping", "pong"));
	
	//static final String DELIMETER = "\r\n";
	static final long CMDTIME = 20; 
	
	private boolean connected = true;

	public int packetCount, blockcount;
	
	
	private Game game;
	private Socket socket;
	private BufferedWriter outputStream;
	private BufferedReader inputStream;

	long pingTime;
	private boolean terrainLoaded=false;
	
	private final Object lock = new Object();
	
	
	public GameClient(Game game){
		this.game = game;
		this.setName("GameClient");

		
	}
	
	public String connect(String ipAddress, int port, boolean sendPos) {
		try {
			Main.log("Connecting... "+ipAddress+":"+port);
			socket = new Socket(InetAddress.getByName(ipAddress), port);
			outputStream = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			sendData("ping");
			if(sendPos) {
				sendData(("00,"+Config.username +","+ game.PE.getPos() +","+ Math.toRadians(game.PE.ViewAngle.yaw)+","+ Math.toRadians(game.PE.ViewAngle.pitch) + "," + game.PE.getHealth()));
			}else {
				sendData("00,"+Config.username);	
			}
			
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
				game.disconnect(error);
				break;
			}
			
			
			
		}
		
		

	}
	
	private String handleMessage(String message) {
		String[] part = message.split(",");
		String otherName = (part.length>1) ? part[1] : "";
		long ID;
		switch(part[0]){
		
		case "00": // PROTOCOL VERSION CHECK
			if(!part[1].equals(""+Connection.PROTOCOL_VERSION)) {
				return "Incompatible server version: "+part[1] + "(current:"+Connection.PROTOCOL_VERSION+")";
			}
			game.PE.ID = Long.parseLong(part[2]);
			
			break;
		
		case "01": // DOWNLOAD BLOCKS
			
			
			for(int i = 0;i<part.length;i=i+5){
				Block b = game.Engine.createBlockByName(part[i+1], cInt(part[i+2]), cInt(part[i+3]), cInt(part[i+4]));
				if(b != Block.NOTHING && b != null){
					game.Engine.world.addBlockNoReplace(b, false);
					//toBeDeleted.add(entry);
					blockcount++;
				}else{
					Main.log("Could not parse received values as a block: " + part[i+1]+","+cInt(part[i+2])+","+cInt(part[i+3])+","+cInt(part[i+4]));
				}
			}
			packetCount++;
			break;
			
		case "08": // DOWNLOAD METADATA
			
			for(int i = 0;i<part.length;i=i+6){
				
				game.Engine.world.getBlockAt(cInt(part[i+1]), cInt(part[i+2]), cInt(part[i+3])).BlockMeta.put(part[i+4], part[i+5]);
			}
			
			
			break;
		
		case "loaded":
			terrainLoaded = true;
			Main.log("Terrain download from server completed ("+blockcount+" blocks / "+packetCount+" packets)");
			
			
			break;
		case "03": // ADD A PLAYER 03,x,y,z,yaw,ID
			
				Vector ViewFrom = new Vector(Float.parseFloat(part[2]), Float.parseFloat(part[3]), Float.parseFloat(part[4]));
				ID = Long.parseLong(part[6]);
				PlayerMP newPlayer = new PlayerMP(ViewFrom, new EAngle(Float.parseFloat(part[5]), 0), part[1],ID, game.Engine);
				game.playerList.put(part[1], newPlayer);
				game.Objects.addAll(newPlayer.Objects);
				//game.Objects.add(newPlayer.title);
				//game.Entities.add(newPlayer);
				//game.Texts.add(new Text3D(part[1], ViewFrom[0], ViewFrom[1], ViewFrom[2]));
				Main.log("(CLIENT:" + Config.username + ") Player "+part[1]+" added at "+ViewFrom);
			
			break;
		
		case "04": // MOVE A PLAYER
			
			PlayerMP otherPlayer = game.playerList.get(otherName);
			if(otherPlayer!=null) {
				otherPlayer.ViewFrom.x = Float.parseFloat(part[2]);
				otherPlayer.ViewFrom.y = Float.parseFloat(part[3]);
				otherPlayer.ViewFrom.z = Float.parseFloat(part[4]);
				otherPlayer.yaw = Float.parseFloat(part[5]);
				otherPlayer.update();
			}
			
			break;
		
		case "67": // DELETE PLAYER
			
			game.Objects.removeAll(game.playerList.get(otherName).Objects);
			//game.Entities.remove(game.playerList.get(otherName));
			game.playerList.remove(otherName);
			
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
		case "10": // ADD TO INVENTORY
			//int size0 = game.Engine.Inv.items.size();
			game.Engine.Inv.add(Main.Items.get(part[2]), cInt(part[3]), false);
			/*if(game.Engine.Inv.items.size()==0) TODO ezek a switchinventoryk kellhetnek
				game.SwitchInventory(false);
			if(game.Engine.Inv.items.size()==1 && size0==0)
				game.SwitchInventory(true);*/
			break;
		case "13": // ADD TO BLOCK INVENTORY
			((BlockInventoryInterface)game.Engine.world.getBlockAt(cInt(part[1]), cInt(part[2]), cInt(part[3]))).getInv().add(Main.Items.get(part[4]), cInt(part[5]), false);
			/*if(game.remoteInventory.getInv().items.size()==0)
				game.SwitchInventory(true);*/
			break;
		case "14": // SWAP BLOCKS 14,Sakii,1,2,3,Stone,true (add to local)
			boolean addToLocal = Boolean.parseBoolean(part[6]);
			game.Engine.Inv.add(Main.Items.get(part[5]), addToLocal ? 1 : -1, false);
			((BlockInventoryInterface)game.Engine.world.getBlockAt(cInt(part[2]), cInt(part[3]), cInt(part[4]))).getInv().add(Main.Items.get(part[5]), addToLocal ? -1 : 1, false);
			
			/*if(game.remoteInventory.getInv().items.size()==0) {
				game.SwitchInventory(true);
			}else if(game.Engine.Inv.items.size()==0) {
				game.SwitchInventory(false);
			}*/
			break;

		case "98": // SERVER CLOSED
			if(game.Engine.server == null) {
				//JOptionPane.showMessageDialog(Main.Frame.getContentPane(), "Server closed", "Disconnected", JOptionPane.ERROR_MESSAGE);
				//game.disconnect();
				return "Server closed";
			}
			
			break;
		case "97": // SAME USERNAME
				//JOptionPane.showInternalMessageDialog(Main.Frame.getContentPane(), "Someone with the same username already logged in", "Disconnected", JOptionPane.ERROR_MESSAGE);
				//game.disconnect();
				return "Someone with the same username already logged in";
			//break;
		case "11": //FORCE MOVE
			game.PE.move(Float.parseFloat(part[1]), Float.parseFloat(part[2]), Float.parseFloat(part[3]), false);
			game.PE.ViewAngle.yaw = Float.parseFloat(part[4]);
			game.PE.ViewAngle.pitch = Float.parseFloat(part[5]);
			game.moved=true;
			break;
		case "15": // SPAWN ENTITY 15,className,x,y,z,yaw,pitch,name,health,ID
			receiveEntityCreate(part);
			break;
		case "16": // MOVE ENTITY
			receiveEntityMove(part);
			break;
		/*case "17": // KILL ENTITIY
			receiveEntityKill(part);
			break;*/
			break;
		case "18":
			game.Engine.world.getEntity(Long.parseLong(part[1])).hurt(Integer.parseInt(part[2]),false);
		case "pong":
			Main.log("ping time: " + (System.currentTimeMillis()-pingTime) + " ms");
			break;
		case "66":
			synchronized(lock) {
				lock.notifyAll();
				
			}
			exit();
			
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
		Block b = game.Engine.createBlockByName(part[2], cInt(part[3]), cInt(part[4]), cInt(part[5]));
		for(int i=6;i<part.length;i+=2) {
			b.setMetadata(part[i], part[i+1], false);
		}
		game.Engine.world.addBlockNoReplace(b,false);

	}
	
	public void sendBlockDestroy(String username, Block b) {
		sendData(("06,"+username+"," + b.x + "," + b.y	+ "," + b.z));

	}
	
	public void receiveBlockDestroy(String[] part) {
		game.Engine.world.destroyBlock(game.Engine.world.getBlockAt(cInt(part[2]),cInt(part[3]), cInt(part[4])), false);

	}
	
	public void sendMetadataEdit(Block b, String key, String value) {
		sendData(("07," + b.x + "," + b.y + "," + b.z + ","
				+ key + "," + value));
	}
	
	void receiveMetadataEdit(String[] part) {
		Block bl = game.Engine.world.getBlockAt(cInt(part[1]), cInt(part[2]), cInt(part[3]));
		if(bl != Block.NOTHING) {
			bl.setMetadata(part[4], part[5], false);
		}
	}
	
	public void sendEntityCreate(Entity e) {
		sendData("15,"+e.className+","+e.getPos()+","+e.ViewAngle.yaw+","+e.ViewAngle.pitch+","+e.name+","+e.ID);
	}
	
	void receiveEntityCreate(String[] part) {
		String className = part[1];
		Vector pos=new Vector(Float.parseFloat(part[2]), Float.parseFloat(part[3]), Float.parseFloat(part[4]));
		EAngle aim=new EAngle(Float.parseFloat(part[5]), Float.parseFloat(part[6]));
		String name=part[7];
		int health = Integer.parseInt(part[8]);
		long ID=Long.parseLong(part[9]);
		
		Entity e = Entity.createEntity(className, pos, aim, name,health, ID, game.Engine); 
		game.Engine.world.addEntity(e);
	}
	
	public void sendEntityMove(Entity e) {
		sendData("16,"+e.ID+","+e.getPos().x+","+e.getPos().y+","+e.getPos().z); 
	}
	
	void receiveEntityMove(String[] part) {
		for(int i=1;i<part.length;i+=4) {
			Entity en = game.Engine.world.getEntity(Long.parseLong(part[i]));
			if(en!=null) {
				en.move(Float.parseFloat(part[i+1]), Float.parseFloat(part[i+2]), Float.parseFloat(part[i+3]), false);
				en.update();
			}
		}
	}
	
	public void sendEntityHurt(long ID, int points) {
		sendData("18,"+ID+","+points);

	}
	
	void receiveEntityHurt(String[] part) {
		
	}
	
	public void sendEntityKill(long ID) {
		sendData("17,"+ID);
	}
	
	void receiveEntityKill(String[] part) {
		game.Engine.world.killEntity(Long.parseLong(part[1]), false);
	}
	
	
	public void sendInvSwap(String username, Block b, String itemName, boolean addToLocal) {
		 // 14,Sakii,1,2,3,Stone,true (add to local)
		sendData(
				"14," + username + "," + b.x + "," + b.y
						+ "," + b.z + "," + itemName + "," + addToLocal);
	}
	
	public void sendInvBlockAdd(String username, Block b, String name, int amount) {
		sendData("13,"+username+","+b.x+","+b.y+","+b.z+","+name+","+amount);

	}
	
	public void sendInvPlayerAdd(String username, String name, int amount) {
		sendData("10,"+username+","+name+","+amount);
	}
	
	public void sendPlayerPos() {
		sendEntityMove(game.PE);
		//sendData("04," + username + "," + x + "," + y + "," + z + "," + yaw +"," + pitch);
	}
	
	private void sendData(String data){
		if(data.isEmpty()) {
			Main.log("empty message from client");
			return;
		}
		try {
			if(data.equals("ping")) {
				pingTime = System.currentTimeMillis();
			}
			
			//OutputStreamWriter outToClient = ;

			//outToClient.write(data+"\r\n");
			outputStream.write(data);
			outputStream.newLine();
			outputStream.flush();
			//outToClient.flush();
			
			if(Main.devmode) {
				if(ALLCODES.contains(data.split(",")[0])){
					Main.log("(CLIENT:"+Config.username+") SENT:      "+data);
				}else if(!data.isEmpty()){
					Main.log("(CLIENT:"+Config.username+") I DUNNO WAT I SENT LOL:  "+data);
				}
			}
			/*for(int i=2;i<5;i++) {
				Main.err(Thread.currentThread().getStackTrace()[i].toString());
			}*/
		} catch (IOException e) {
			game.disconnect(e.getMessage());
		}
		
		
		
	}
	
	public void kill(){
		
		Main.log("sending 66");

		sendData(("66," + Config.username));
		
		synchronized (lock) {
			
			try
			{
				lock.wait();
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		
		terrainLoaded=true;
		connected=false;
		
		
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
