package ml.sakii.factoryisland;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ml.sakii.factoryisland.blocks.Block;
import ml.sakii.factoryisland.blocks.BlockFace;
import ml.sakii.factoryisland.blocks.BlockInventoryInterface;
import ml.sakii.factoryisland.blocks.SignalConsumer;
import ml.sakii.factoryisland.blocks.SignalPropagator;
import ml.sakii.factoryisland.blocks.SphereBlock;
import ml.sakii.factoryisland.blocks.TextureListener;
import ml.sakii.factoryisland.blocks.TickListener;
import ml.sakii.factoryisland.blocks.DayNightListener;
import ml.sakii.factoryisland.blocks.WaterBlock;
import ml.sakii.factoryisland.entities.Alien;
import ml.sakii.factoryisland.entities.Entity;
import ml.sakii.factoryisland.entities.PlayerMP;
import ml.sakii.factoryisland.items.PlayerInventory;
import ml.sakii.factoryisland.items.ItemType;

public class World {

	final int CHUNK_WIDTH = 10;
	int MAP_RADIUS = 5;
	private static final int MAP_VERSION=1;
	public String worldName=""; //""=REMOTE MAP
	long seed;
	private int loadedVersion=MAP_VERSION;
	private static final float BLOCK_RANGE = 0.2f;
	
	private GameEngine Engine;
	private ConcurrentHashMap<Point3D, Block> Blocks = new ConcurrentHashMap<>(10000);
	private Game game;
	String success="OK";
	public ConcurrentHashMap<Long, Entity> Entities = new ConcurrentHashMap<>();
	
	private int worldTop,worldBottom;
	int lightCalcRuns=0;
	public boolean loading=true;
	
	public World(String worldName, GameEngine engine, Game game, boolean existing, Consumer<String> update) {
		Engine = engine;
		this.game = game;
		this.worldName = worldName;
		
		
		if(existing) {
			success = loadWorld(engine, update);
			if(success==null) success="";
		}
		
	}

	@SuppressWarnings({ "null" })
	private String loadWorld(GameEngine engine, Consumer<String> update) {
		
		XMLInputFactory factory = XMLInputFactory.newInstance();
		XMLEventReader eventReader;
		File f = new File("saves/" + worldName + "/map.xml");
		int totalBlocks=0;
		try(FileInputStream fis = new FileInputStream(f);){
			
			byte[] data = new byte[(int) f.length()];
			fis.read(data);
			
			String str = new String(data, "UTF-8");
			String counted = "<block ";
			totalBlocks = str.length() - str.replace(counted, "").length();
			totalBlocks /= counted.length();
		} catch (IOException e)
		{
			e.printStackTrace();
			return e.getMessage();
		}
		
		
        try(FileReader reader =  new FileReader(f))
		{
			eventReader = factory.createXMLEventReader(reader);
			
	 
	        boolean bSeed=false,bMetadata=false,bPower=false,bTick=false,bInventory=false,bVersion=false;
	        String curMeta=null,curInv=null,curPower=null;
	        Block curBlock=null;
	        while(eventReader.hasNext()) {
	            XMLEvent event;
				try
				{
					event = eventReader.nextEvent();
				} catch (XMLStreamException e)
				{
					e.printStackTrace();
					return "Error parsing map file: "+e.getMessage();
				}
	               
	            switch(event.getEventType()) {
	               
	               case XMLStreamConstants.START_ELEMENT:
	            	   StartElement startElement = event.asStartElement();
	                   String qName = startElement.getName().getLocalPart();
	                   
	                   if (qName.equalsIgnoreCase("seed")) {
	                	   bSeed=true;
	                   }else if (qName.equalsIgnoreCase("version")) {
	                	   bVersion=true;
	                   }else if (qName.equalsIgnoreCase("blocks")) {
	                	   update.accept("Loading blocks...");
	                   }else if (qName.equalsIgnoreCase("block")) {
	                	   //boolean nem kell mert curBlock van helyette
	                       Iterator<Attribute> attributes = startElement.getAttributes();
	                       String name = attributes.next().getValue();
	                       int x = Integer.parseInt(attributes.next().getValue());
	                       int y = Integer.parseInt(attributes.next().getValue());
	                       int z = Integer.parseInt(attributes.next().getValue());
	                       
	                       curBlock=Engine.createBlockByName(name, x, y, z);
	                       if(curBlock == Block.NOTHING && !name.toLowerCase().startsWith("test")) {
			           			Main.err("Could not create block: "+ name);
			           			return "Could not create block: "+ name;
			           		}
	                       
	                   }else if (qName.equalsIgnoreCase("metadata")) {
	                	   bMetadata=true;                	   
	                   }else if (qName.equalsIgnoreCase("power")) {
	                	   bPower=true;
	                   }else if (qName.equalsIgnoreCase("inventory")) {
	                	   bInventory=true;
	                   }else if (qName.equalsIgnoreCase("tick")) {
	                	   bTick=true;
	                   }else if(qName.equalsIgnoreCase("entities")) {
	                	   update.accept("Loading entities...");
	                   }else if (qName.equalsIgnoreCase("entity")) {
	                	   //boolean nem kell mert sosem lesz leszarmazott tagje
	                       Iterator<Attribute> attributes = startElement.getAttributes();
	                       //ez fura sorrendben van beolvasva
	                       String aim = attributes.next().getValue();
	                       String className = attributes.next().getValue();
	                       String pos = attributes.next().getValue();
	                       
	                       
	                       String name = attributes.next().getValue();
	                       String health=attributes.next().getValue();
	                       String id = attributes.next().getValue();
	                       
	                       
	                       
	                       Entity e = Entity.createEntity(className,
	       						Vector.parseVector(pos),
	       						EAngle.parseEAngle(aim),
	       						name,
	       						Integer.parseInt(health),
	       						Long.parseLong(id),
	       						engine);
	       				if(e != null) addEntity(e, false);
	                       
	                   }else if(bMetadata) {
	                	   curMeta=qName;
	                   }else if(bPower) {
	                	   curPower=qName;
	                   }else if(bInventory) {
	                	   curInv=qName;
	                   }else if(!qName.equalsIgnoreCase("world")){
	                	   Main.err("Unknown tag in map file: "+qName);
	                	   
	                   }
	                   
	                
	                   break;
	               case XMLStreamConstants.CHARACTERS:
	            	   Characters characters = event.asCharacters();
	            	   String data = characters.getData().trim();
	            	   if(data.isBlank()) {
	            		   break;
	            	   }
	            	   
	            	   if(bMetadata) {
	            		   if(curBlock!=null && curMeta != null) {
	            			   curBlock.BlockMeta.put(curMeta,data);
	            		   }else {
	            			   return "No current block/metadata tag while parsing metadata";
	            		   }
	            	   }else if(bTick) {
	            		   engine.Tick=Long.parseLong(data);
	            	   }else if(bSeed) {
	            		   this.seed=Long.parseLong(data);
	            	   }else if(bVersion) {
	            		   this.loadedVersion=Integer.parseInt(data);
	            		   if(this.loadedVersion!=MAP_VERSION) {
	            			   return "Incompatible map file";
	            		   }
	            	   }else if(curMeta != null) {
	            		   curBlock.BlockMeta.put(curMeta, data);
	            	   }else if(curPower != null) {
	            		   if(curBlock instanceof SignalPropagator curWire) {
	            			   curWire.powers.put(BlockFace.valueOf(curPower), Integer.parseInt(data));
	            		   }else if(curBlock instanceof SignalConsumer curConsumer) {
	            			   curConsumer.getSignals().put(BlockFace.valueOf(curPower), Integer.parseInt(data));
	            		   }else {
	            			   Main.err("Power on non-wire block: "+curBlock);
	            		   }
	            	   }else if(curInv != null) {
	            		   ((BlockInventoryInterface) curBlock).getInv().add(Main.Items.get(curInv), Integer.parseInt(data), false);
	            	   }
	            	   
	            	   
	            	   break;
	            	   
	               case XMLStreamConstants.END_ELEMENT:
	            	   EndElement endElement = event.asEndElement();
	                   
	            	   if(endElement.getName().getLocalPart().equalsIgnoreCase("block")) {
	            		   if(curBlock!=null) {
	            			   addBlockNoReplace(curBlock,true);
	            			   curBlock=null;
	            			   update.accept("Loading blocks... "+(int)(this.Blocks.size()*100f/totalBlocks)+"%");
	            		   }else {
	            			   return "Closing tag of empty block";
	            		   }
	                   
	                }else if(endElement.getName().getLocalPart().equalsIgnoreCase("metadata")) {
	                	curMeta=null;
	                	bMetadata=false;
	                }
	                else if(endElement.getName().getLocalPart().equalsIgnoreCase("power")) {
	                	curPower=null;
	                	bPower=false;
	                }
	                else if(endElement.getName().getLocalPart().equalsIgnoreCase("inventory")) {
	                	curInv=null;
	                	bInventory=false;
	                }
	                else if(endElement.getName().getLocalPart().equalsIgnoreCase("seed")) {
	                	bSeed=false;
	                }else if(endElement.getName().getLocalPart().equalsIgnoreCase("tick")) {
	                	bTick=false;
	                }else if(endElement.getName().getLocalPart().equalsIgnoreCase("version")) {
	                	bVersion=false;
	                }
	                break;
	               default: Main.err("Unknown XML syntax: " + event.getEventType());
	            	   
	            }
	            
	        }
	        
	        
	        
			
		
		} catch (Exception e)
		{
			e.printStackTrace();
			
			return e.getMessage();
		}
        
        return "OK";
		
	}
	
	public static PlayerInventory loadInv(String username,String worldName, PlayerInventory output) {
		


		File file = new File("saves/" + worldName + "/"+username+".xml");
		if (!file.exists()) {
			return output;
		}

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		Document document;
		try {
			db = dbf.newDocumentBuilder();
			document = db.parse(file);
		} catch (Exception e) {
			e.printStackTrace();
			return output;
		}
		
		Node players = document.getElementsByTagName(username).item(0);
		if(players == null) {
			Main.err("No data of "+username+" in player file!");
			return output;
		}
		NodeList stacks = players.getChildNodes();
		for (int i = 0; i < stacks.getLength(); i++) {
			Node stack = stacks.item(i);
			output.add(Main.Items.get(stack.getNodeName()),
					Integer.parseInt(stack.getTextContent()), false);
		}

		
		return output;
		
		
	}


	
	void parsePE(String username, PlayerMP result) {
		result.getPos().set(loadVector(username, new String[] {"x","y","z"}));
		float[] other = loadVector(username, new String[] {"yaw", "pitch", "health"});
		result.ViewAngle.set(other[0],other[1]);
		result.setHealth((int) other[2]);
		result.name = username;
	}
	
	public float[] loadVector(String username, String params[]) {
		float[] output = new float[params.length];
		File file = new File("saves/" + worldName + "/"+username+".xml");
		if (!file.exists()) {
			return null;
		}

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		Document document;
		try {
			db = dbf.newDocumentBuilder();
			document = db.parse(file);
		} catch (Exception e) {
			e.printStackTrace();
			return output;
		}
		
		Node players = document.getElementsByTagName(username).item(0);
		if(players == null) {
			Main.err("No data of "+username+" in player file!");
			return output;
		}

		NamedNodeMap nnm = players.getAttributes();
		
		for(int i=0;i<params.length;i++) {
			if(nnm.getNamedItem(params[i]) != null) {
				output[i] = Float.parseFloat(nnm.getNamedItem(params[i]).getNodeValue());
			}
		}
		
		
		
		return output;
	}
	
	public Entity getEntity(long ID){
		return Entities.get(ID);
	}
	
	public Collection<Entity> getAllEntities(){
		return Entities.values();
	}
	
	
 	public Block getBlockAt(int x, int y, int z) {
		
		Point3D p = new Point3D(x, y, z);
		return getBlockAtP(p);

	}
 	
 	public Block getBlockAtP(Point3D p) {
		Block b = Blocks.get(p);

		return (b == null) ? Block.NOTHING : b ;
 	}

	
	
	public boolean addBlockNoReplace(Block b, boolean resend) {
		if(getBlockAt(b.x, b.y, b.z) == Block.NOTHING) {
			if(Engine.client != null && resend) {
				Engine.client.sendBlockPlace(Config.username,b);
			}else {
				ReplaceBlock(b);
			}
			return true;
		}
		return false;
	}
	
	private void ReplaceBlock(Block b) {
		HashSet<Point3D> sources=new HashSet<>();
		HashMap<Point3D,HashMap<Polygon3D,Integer>> removeResults=new HashMap<>();
		if(!loading) {
			
			for(Block nearby : get6Blocks(b, false).values()) {
				for(Object3D obj : nearby.Objects) {
					if(obj instanceof Polygon3D poly) {
						for(Point3D source : new HashSet<>(poly.getSources())) {// TODO concurrentmodificationexception?
							HashMap<Polygon3D,Integer> removeResult = new HashMap<>();
							sources.add(source); // kikapcsolja az osszes fenyforrast es elmenti oket
							removeLightIntoMap(source,removeResult);
							removeResults.put(source, removeResult);
						}
					}
				}
			}
		}
		
		
		Blocks.put(b.pos, b);
		

		
		for (Entry<BlockFace, Block> entry : get6Blocks(b, false).entrySet()) {
			Block block = entry.getValue();
			if (block instanceof TickListener) {
				Engine.TickableBlocks.add(block.pos);
			}
			
			
		}
		
		

		
		if (b instanceof TickListener) {
			Engine.TickableBlocks.add(b.pos);
		}
		
		if(b instanceof DayNightListener dnl) {
			Engine.DayNightBlocks.add(dnl);
		}
		
		if (b instanceof TextureListener && game != null) {
			game.TextureBlocks.add((TextureListener)b);
		}
		
		//TODO dinamikusan
		if(b instanceof SphereBlock sb) {
			game.Spheres.add(sb.getSphere());
		}
		
		filterAdjecentBlocks(b);
		
		recalcAO(b.pos);
		
		
		if(game != null) {
			game.Objects.addAll(b.Objects);
		}
		

		
		if(!loading) {
			for(Point3D source : sources) { //elterjeszti az elmentett a fenyforrasokat
				reAddLight(source,removeResults.get(source));
			}
			
			if(b.lightLevel>0) { //ha ad ki fenyt akkor elterjeszti
				addLight(b.pos);
			}
		}
		
		if(b.z>worldTop) {
			worldTop=b.z;
		}
		
		if(b.z<worldBottom) {
			worldBottom=b.z;
		}
	}


	public void destroyBlock(Block b, boolean resend) {
		if(Engine.client != null && resend) {
			Engine.client.sendBlockDestroy(Config.username,b);
		}else {
		
			if (getBlockAtP(b.pos) == Block.NOTHING) {
				Main.err("Attempted to destroy air block: "+b.pos);
			}
	
			if(b.lightLevel>0)
				removeLight(b.pos);
			
			Blocks.remove(b.pos);
			
			for (Entry<BlockFace, Block> entry : get6Blocks(b, false).entrySet()) {
				Block bu = entry.getValue();
				if (bu instanceof TickListener) {
					Engine.TickableBlocks.add(bu.pos);
				}
				
			}
			
			
			

			if (b instanceof TickListener) {
				Engine.TickableBlocks.remove(b.pos);
			}
			
			
			if (b instanceof TextureListener tl && game != null) {
				game.TextureBlocks.remove(tl);
			}
			
			if(b instanceof DayNightListener dnl) {
				Engine.DayNightBlocks.remove(dnl);
			}
	
			
			//TODO dinamikusan
			if(b instanceof SphereBlock sb) {
				game.Spheres.remove(sb.getSphere());
			}
			
			
			filterAdjecentBlocks(b);
	
			recalcAO(b.pos);
			
			if(game != null) {
				game.Objects.removeAll(b.Objects);
			}
			

			
			
			HashSet<Point3D> sources = new HashSet<>();
			for(Object3D obj : b.Objects) { // kiuteskor eleg ujraszamolni az erintett forrasokat
				if(obj instanceof Polygon3D poly) {
					for(Point3D source : poly.getSources()) {
						if(!source.equals(b.pos))
							sources.add(source);
					}
				}
			}
			
			for(Point3D source:sources) {
				addLight(source);		 //valojaban csak az uj blokkokhoz adodik hozza
			}
			
			
			
			if(Blocks.size()==0 && (Engine.isLocalMP() || Engine.isSingleplayer())) {
				addBlockNoReplace(new WaterBlock(0,0,0,Engine), true);
			}
		}
	}
	
	private void recalcAO(Point3D pos) {
		Point3D tmp = new Point3D();
		for(Point3D point1 : get6BlocksP(pos)) {
			for(Point3D point2 : get6BlocksP(point1)) {
				Block b1 = getBlockAtP(point2);
				if(b1 != Block.NOTHING) {
					b1.recalcOcclusions(this, tmp);
				}
				for(Point3D point3 : get6BlocksP(point2)) {
					Block b2 = getBlockAtP(point3);
					if(b2 != Block.NOTHING) {
						b2.recalcOcclusions(this, tmp);
					}
					
					
				}
			}
		}
	}
	
	public void addEntity(Entity e, boolean resend) {
		if(resend && Engine.client != null) { 
			Engine.client.sendEntityCreate(e);
		}else {
			Entities.put(e.ID, e);
			
			
			if(game != null) {
				if(e != game.PE)
				game.Objects.addAll(e.Objects);
			}
		}
		
	
	}
	
	// true ha tulelte
	public boolean hurtEntity(long ID, int points, boolean resend) {
		if(resend && Engine.client != null) {
			Engine.client.sendEntityHurt(ID, points);
			return true;
		}
		
		Entity e = Entities.get(ID);
		
		if(e == null) {
			Main.err("Entity already null: " + ID);
			return true;
		}
		e.setHealth(Math.min(Math.max(e.getHealth()-points,0), e.maxHealth));
		
		
		if(e.getHealth() > 0) {
			return true;
		}


		if(game != null) {
			if(e == game.PE) {
				game.notifyDeath();
			}else {
				game.Objects.removeAll(e.Objects);
			}
		}
		
		Entities.remove(ID);
		
		return false;
	
	}
	

	boolean walk(Vector direction, float coefficient, Entity entity, float FPS, boolean resend)
	{
		boolean success=true;
		float targetX, targetY;
		float nextX = entity.getPos().x + direction.x * coefficient / FPS;
		float nextY = entity.getPos().y + direction.y * coefficient / FPS;
		Point3D coords = entity.tmpPoint;
		Block[] blocks6X = getCollidingBlocks(nextX+Math.copySign(World.BLOCK_RANGE, direction.x), entity.getPos().y, entity.getPos().z, entity, coords);
		Block[] blocks6Y = getCollidingBlocks(entity.getPos().x, nextY+Math.copySign(World.BLOCK_RANGE, direction.y), entity.getPos().z, entity, coords);
		Block nextBlockX1 = blocks6X[0];// TOP
		Block nextBlockX2 = blocks6X[1];// NONE
		Block nextBlockX3 = blocks6X[2];// BOTTOM

		Block nextBlockY1 = blocks6Y[0];// TOP
		Block nextBlockY2 = blocks6Y[1];// NONE
		Block nextBlockY3 = blocks6Y[2];// BOTTOM

		if (!nextBlockX1.solid && !nextBlockX2.solid && !nextBlockX3.solid)
		{
			targetX = nextX;
		}else {
			int bx;
			if(nextBlockX1.solid) {
				bx=nextBlockX1.x;
			}else if(nextBlockX2.solid) {
				bx=nextBlockX2.x;
			}else{
				bx=nextBlockX3.x;
			}
			
			
			if(direction.x<0) {
				targetX=bx+1+World.BLOCK_RANGE;
			}else {
				targetX=bx-World.BLOCK_RANGE;


				
			}
			success=false;
		}
		if (!nextBlockY1.solid && !nextBlockY2.solid && !nextBlockY3.solid)
		{
			targetY = nextY;
		}else {
			
			int by;
			if(nextBlockY1.solid) {
				by=nextBlockY1.y;
			}else if(nextBlockY2.solid) {
				by=nextBlockY2.y;
			}else{
				by=nextBlockY3.y;
			}
			
			if(direction.y<0) {
				targetY=by+1+World.BLOCK_RANGE;
			}else {
				targetY=by-World.BLOCK_RANGE;


				
			}
			
			success=false;
		}

		entity.move(targetX, targetY, entity.getPos().z, resend);
		return success;


	}
	
	private Block[] getCollidingBlocks(float x, float y, float z, Entity entity, Point3D p)
	{
		Block[] result = new Block[3];
		int dx = (int) Math.floor(x);
		int dy = (int) Math.floor(y);
		int dz1 = (int) Math.floor(z);
		int dz2 = (int) Math.floor(z - 1f * entity.VerticalVector.z);
		int dz3 = (int) Math.floor(z - 1.699f * entity.VerticalVector.z);

		p.set(dx, dy, dz1);
		result[0]=getBlockAtP(p);
		
		p.set(dx, dy, dz2);
		result[1]=getBlockAtP(p);
		
		p.set(dx, dy, dz3);
		result[2]=getBlockAtP(p);
		
		return result;

	}
	
	public HashMap<BlockFace, Block> get6Blocks(Block center, boolean includeNothing) {
		return get6Blocks(new Point3D().set(center.x, center.y, center.z), includeNothing); //masolni kell point3d-t mert felulirja

	}

	public HashMap<BlockFace, Block> get6Blocks(float xf, float yf, float zf, boolean includeNothing) {
		return get6Blocks(new Point3D().set(xf, yf, zf), includeNothing);
		
	}
	
	public static ArrayList<Point3D> get6BlocksP(Point3D p){
		ArrayList<Point3D> result = new ArrayList<>(6);
		int x = p.x;
		int y = p.y;
		int z = p.z;
		result.add(new Point3D(x, y, z + 1));
		result.add(new Point3D(x, y, z - 1));
		result.add(new Point3D(x - 1, y, z));
		result.add(new Point3D(x + 1, y, z));
		result.add(new Point3D(x, y - 1, z));
		result.add(new Point3D(x, y + 1, z));
		return result;

	}
	
	public HashMap<BlockFace, Block> get6Blocks(Point3D p, boolean includeNothing){
		int x = p.x;
		int y = p.y;
		int z = p.z;
		HashMap<BlockFace, Block> result = new HashMap<>();

		p.set(x, y, z + 1);
		Block top = getBlockAtP(p);
		if (top != Block.NOTHING || includeNothing) {
			result.put(BlockFace.TOP, top);
		}

		p.set(x, y, z - 1);
		Block bottom = getBlockAtP(p);
		if (bottom != Block.NOTHING || includeNothing) {
			result.put(BlockFace.BOTTOM, bottom);
		}

		p.set(x - 1, y, z);
		Block west = getBlockAtP(p);
		if (west != Block.NOTHING || includeNothing) {
			result.put(BlockFace.WEST, west);
		}

		p.set(x + 1, y, z);
		Block east = getBlockAtP(p);
		if (east != Block.NOTHING || includeNothing) {
			result.put(BlockFace.EAST, east);
		}

		p.set(x, y - 1, z);
		Block south = getBlockAtP(p);
		if (south != Block.NOTHING || includeNothing) {
			result.put(BlockFace.SOUTH, south);
		}

		p.set(x, y + 1, z);
		Block north = getBlockAtP(p);
		if (north != Block.NOTHING || includeNothing) {
			result.put(BlockFace.NORTH, north);
		}

		return result;

		
		
	}
	
	private final static Point3D[] deltas = new Point3D[8];
	
	static {
		deltas[0] = new Point3D(-1,-1,-1);
		deltas[1] = new Point3D(-1,-1,1);
		deltas[2] = new Point3D(-1,1,-1);
		deltas[3] = new Point3D(-1,1,1);
		deltas[4] = new Point3D(1,-1,-1);
		deltas[5] = new Point3D(1,-1,1);
		deltas[6] = new Point3D(1,1,-1);
		deltas[7] = new Point3D(1,1,1);
	}
	
	public HashMap<Point3D, Block> get4Blocks(Point3D p, BlockFace face, boolean includeNothing){
		
		
		
		Point3D[] selected;
		switch(face) {
		case TOP:
			selected = new Point3D[]{deltas[1],deltas[3],deltas[5],deltas[7]};
			return getBlocksByDelta(p,selected,face,includeNothing);
		case BOTTOM:
			selected = new Point3D[]{deltas[0],deltas[2],deltas[4],deltas[6]};
			return getBlocksByDelta(p,selected,face,includeNothing);
		case NORTH:
			selected = new Point3D[]{deltas[2],deltas[3],deltas[6],deltas[7]};
			return getBlocksByDelta(p,selected,face,includeNothing);
		case SOUTH:
			selected = new Point3D[]{deltas[0],deltas[1],deltas[4],deltas[5]};
			return getBlocksByDelta(p,selected,face,includeNothing);
		case EAST:
			selected = new Point3D[]{deltas[4],deltas[5],deltas[6],deltas[7]};
			return getBlocksByDelta(p,selected,face,includeNothing);
		case WEST:
			selected = new Point3D[]{deltas[0],deltas[1],deltas[2],deltas[3]};
			return getBlocksByDelta(p,selected,face,includeNothing);
		default:
			return null;
		}
		
		
	}
	
	private HashMap<Point3D, Block> getBlocksByDelta(Point3D pos, Point3D[] deltas, BlockFace face, boolean includeNothing){
		HashMap<Point3D, Block> result = new HashMap<>();
		Point3D tmp = new Point3D();
		for(Point3D delta : deltas) {
			Block b = getBlockAtP(tmp.set(pos).add(delta));
			if(b != Block.NOTHING || includeNothing) {
				result.put(delta.cpy().add(face.getOpposite()), b);
			}
		}
		return result;
	}
	
	
	//BFS
	private void modifyLight(LinkedHashMap<Point3D,Integer> queue , boolean add, HashSet<Point3D> discovered, HashMap<Polygon3D, Integer> result) {
		if(queue.isEmpty()) return;
		
		//TODO memoriaoptimalizalas

		Point3D coord = new Point3D().set(queue.keySet().toArray(new Point3D[0])[0]);
		int intensity = queue.get(coord);
		queue.remove(coord);
		
		if(intensity <= 0) return;
		
		applyIntensityToNearby(new Point3D().set(coord),intensity, add, result);
		
		for(Entry<BlockFace, Block> entry : get6Blocks(new Point3D().set(coord), true).entrySet()) {
			Block b = entry.getValue();
			BlockFace face = entry.getKey();

			if(b == Block.NOTHING || b.transparent) {
				//TODO memoriaoptimalizalas
				Point3D nextCoord = new Point3D().set(coord).add(face);
				if(!discovered.contains(nextCoord)) {
					discovered.add(nextCoord);
					queue.put(nextCoord, intensity-1);
				}
			}
		}
		
		
		modifyLight(queue,add,discovered,result);
		

	}
	
	private void applyIntensityToNearby(Point3D coord, int intensity, boolean add, HashMap<Polygon3D, Integer> result) {
		HashMap<BlockFace, Block> nearby = get6Blocks(coord, false);
		for(Entry<BlockFace, Block> entry : nearby.entrySet()) {
			Block b = entry.getValue();
			BlockFace face = entry.getKey();
			
			
			
			for(Entry<Polygon3D, BlockFace> polys :  b.HitboxPolygons.entrySet()) {
				BlockFace polyface = polys.getValue(); 
				Polygon3D poly = polys.getKey();
				if(polyface == face.getOpposite()) {
					
					
					if(add) {
							result.put(poly, intensity);
					}else {
						result.put(poly, null);
					}
					
				}				
				
			}
			
		}
		

	}
	
	
	private void reAddLight(Point3D source, HashMap<Polygon3D,Integer> removedResults) {

		
		//add
		
		Block sourceBlock = getBlockAt(source.x, source.y, source.z);
		int intensity = sourceBlock.lightLevel;

		
		LinkedHashMap<Point3D, Integer> queue = new LinkedHashMap<>();
		queue.put(source, intensity);
		
		
		
		HashSet<Point3D> discovered = new HashSet<>();
		discovered.add(source);
		
		modifyLight(queue , true, discovered , removedResults);

		for(Entry<Polygon3D, Integer> pass : removedResults.entrySet()) {
			Integer val = pass.getValue();
			if(val==null) {
				pass.getKey().removeSource(source);
			}else {
				pass.getKey().addSource(source, pass.getValue());
			}
			lightCalcRuns++;

		}
		
		for(Polygon3D p : sourceBlock.HitboxPolygons.keySet()) {
			p.addSource(source, intensity);
		}
	}
	
	void addLight(Point3D source) {
		lightCalcRuns=0;
		Block sourceBlock = getBlockAt(source.x, source.y, source.z);
		int intensity = sourceBlock.lightLevel;
		for(Polygon3D p : sourceBlock.HitboxPolygons.keySet()) {
			p.addSource(source, intensity);
		}
		
		
		HashMap<Polygon3D,Integer> results = new HashMap<>();
		
		//BFS
		LinkedHashMap<Point3D, Integer> queue = new LinkedHashMap<>();
		queue.put(source, intensity);
		
		
		
		HashSet<Point3D> discovered = new HashSet<>();
		discovered.add(source);
		
		modifyLight(queue , true, discovered , results);

		for(Entry<Polygon3D, Integer> pass : results.entrySet()) {
			pass.getKey().addSource(source, pass.getValue());
			lightCalcRuns++;

		}
		
	}
	
	private void removeLightIntoMap(Point3D source,HashMap<Polygon3D,Integer> results) {
		int intensity = getBlockAt(source.x, source.y, source.z).lightLevel;
		LinkedHashMap<Point3D, Integer> queue = new LinkedHashMap<>();
		queue.put(source, intensity);
		
		HashSet<Point3D> discovered = new HashSet<>();
		discovered.add(source);
		
		modifyLight(queue , false, discovered , results);
	}
	
	
	private void removeLight(Point3D source) {
		lightCalcRuns=0;

		
		//BFS
		HashMap<Polygon3D,Integer> results = new HashMap<>();

		removeLightIntoMap(source,results);
		
		
		for(Entry<Polygon3D, Integer> pass : results.entrySet()) {
			pass.getKey().removeSource(source);
			lightCalcRuns++;

		}
		

	}
	

	int getTop(int x, int y) {
		TreeSet<Block> blockColumn = new TreeSet<>((arg0, arg1) -> Integer.compare(arg0.z, arg1.z));

		for (Entry<Point3D, Block> entry : Blocks.entrySet()) {
			Point3D pos = entry.getKey();
			Block b = entry.getValue();

			if (pos.x == x && pos.y == y) {
				blockColumn.add(b);
			}
		}
		if(blockColumn.isEmpty()) {
			return 0;
		}
		return blockColumn.last().z;
	}

	public Collection<Block> getWhole() {
		return this.Blocks.values();
	}
	
	//TODO cachelni
	public void getSurface(Collection<Block> Blocks) {
		for (Block b : this.Blocks.values()) {
			for (Object3D obj : b.Objects) {
				if (obj instanceof Polygon3D poly && poly.adjecentFilter) {
					Blocks.add(b);
					break;
				}
			}
		}
	}
	
	

	public Block getSpawnBlock() {

		for(Block b : Blocks.values()) {
			if(b.name.equals("ChestModule") && getBlockAt(b.x, b.y, b.z+1)==Block.NOTHING) {
				return b;
			}

		}

		for(Block b : Blocks.values()) {
			if(getBlockAt(b.x, b.y, b.z + 1) == Block.NOTHING &&
					getBlockAt(b.x, b.y, b.z + 2) == Block.NOTHING) {
				return b;
			}

		}
		Main.err("No spawnblock!");
		return Block.NOTHING;

	}

	public int getSize() {
		return Blocks.size();
	}

	Block getBlockUnderEntity(boolean inverse, boolean under, Entity entity) {
		
		Point3D feetPoint=entity.feetPoint;
		Point3D tmpPoint=entity.tmpPoint;
		TreeSet<Point3D> playerColumn = entity.playerColumn;
		
		playerColumn.clear();
		Vector entityPos = entity.getPos();

		int x=(int) Math.floor(entityPos.x) ;
		int y= (int) Math.floor(entityPos.y);
		feetPoint.set(entityPos.x, entityPos.y, entityPos.z);
				
		for(int i=x-1;i<=x+1;i++) {
			for(int j=y-1;j<=y+1;j++) {
				for(int k=worldBottom;k<=worldTop;k++) {
					
					tmpPoint.set(i,j,k);
					Block b = getBlockAtP(tmpPoint);
					if(b == Block.NOTHING) {
						continue;
					}
					
					if (b.x == x && b.y == y && b.solid) {
						playerColumn.add(b.pos);
					}else if((b.x-BLOCK_RANGE < entityPos.x && entityPos.x < b.x+1+BLOCK_RANGE)
							&&  b.y == y && b.solid) {
							
						playerColumn.add(b.pos);
					}else if((b.y-BLOCK_RANGE < entityPos.y && entityPos.y < b.y+1+BLOCK_RANGE)
							&&  b.x == x && b.solid) {
							
						playerColumn.add(b.pos);
					}
					
					
				}
			}
			

			

		}
		
		
		if (playerColumn.isEmpty()) {
			return Block.NOTHING;
		}
		
		Point3D result;
		
		
		if(under) {
			if ((entity.VerticalVector.z == 1 && !inverse) || (entity.VerticalVector.z == -1 && inverse)) {
				result = playerColumn.floor(feetPoint);
	
			} else {
				result = playerColumn.ceiling(feetPoint);
	
			}
		}else {
			if ((entity.VerticalVector.z == 1 && !inverse) || (entity.VerticalVector.z == -1 && inverse)) {
				result = playerColumn.ceiling(feetPoint);
	
			} else {
				result = playerColumn.floor(feetPoint);
	
			}			
		}

		return result == null ? Block.NOTHING : getBlockAtP(result);


	}

	public void saveByShutdown() {

		saveWorld(worldName, getWhole(), Engine.Tick, seed, getAllEntities(), loadedVersion);
		
	}

	private static void saveWorld(String worldName, Collection<Block> Blocks, long tickCount, long seed, Collection<Entity> entities, int loadedVersion) {
		File saves = new File("saves");
		File mods = new File("mods");
		File wname = new File("saves/" + worldName);

		File file = new File("saves/" + worldName + "/map.xml");
		try {
			saves.mkdir();
			mods.mkdir();
			wname.mkdir();

			file.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		Document document;
		try {
			db = dbf.newDocumentBuilder();
			document = db.newDocument();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		Element root = document.createElement("world");

		Element seedE = document.createElement("seed");
		seedE.setTextContent(seed+"");
		root.appendChild(seedE);
		
		Element versionE = document.createElement("version");
		versionE.setTextContent(loadedVersion+"");
		root.appendChild(versionE);
		
		Element blocks = document.createElement("blocks");
		for (Block b : Blocks) {
			if(b==Block.NOTHING) {
				Main.err("Nothing block on map!");
				continue;
			}
			Element BlockElement = document.createElement("block");
			BlockElement.setAttribute("x", "" + b.x);
			BlockElement.setAttribute("y", "" + b.y);
			BlockElement.setAttribute("z", "" + b.z);
			BlockElement.setAttribute("name", "" + b.name);

			Element Metadata = document.createElement("metadata");
			for (Entry<String, String> entry : b.BlockMeta.entrySet()) {
				Element data = document.createElement(entry.getKey());
				data.setTextContent(entry.getValue());
				Metadata.appendChild(data);
			}
			BlockElement.appendChild(Metadata);

			
			
			Element Powers = document.createElement("power");
			
			if(b instanceof SignalPropagator wire) {
				for (Entry<BlockFace, Integer> entry : wire.powers.entrySet()) {
					Element power = document.createElement(entry.getKey().name());
					power.setTextContent(entry.getValue().toString());
					Powers.appendChild(power);
				}				
			}else if(b instanceof SignalConsumer consumer) {
				for (Entry<BlockFace, Integer> entry : consumer.getSignals().entrySet()) {
					Element power = document.createElement(entry.getKey().name());
					power.setTextContent(entry.getValue().toString());
					Powers.appendChild(power);
				}				
			}
			
			
			BlockElement.appendChild(Powers);
			
			if(b instanceof BlockInventoryInterface bii) {
				Element Inv = document.createElement("inventory");
				for (Entry<ItemType, Integer> entry : bii.getInv().items.entrySet()) {
					Element stack = document.createElement(entry.getKey().name);
					stack.setTextContent(entry.getValue()+"");
					Inv.appendChild(stack);
				}
				BlockElement.appendChild(Inv);
			}
			
			
			
			
			blocks.appendChild(BlockElement);

		}

		root.appendChild(blocks);

		Element tick = document.createElement("tick");
		tick.setTextContent(tickCount + "");
		root.appendChild(tick);
		
		Element entitiesNode = document.createElement("entities");
		for(Entity e : entities) {
			if(e instanceof PlayerMP p) {
				if(p.inventory == PlayerInventory.Creative) {
					savePlayer(worldName, p.name, p.getPos(), p.ViewAngle, loadInv(p.name,worldName, new PlayerInventory(p.name,null)), p.getHealth());
				}else {
					savePlayer(worldName, p.name, p.getPos(), p.ViewAngle, p.inventory, p.getHealth());
				}
			}else {
				Element entity = document.createElement("entity");
				entity.setAttribute("aim", e.ViewAngle.toString());
				entity.setAttribute("classname", e.className);
				entity.setAttribute("id", e.ID+"");
				entity.setAttribute("name", e.name);
				entity.setAttribute("pos", e.getPos().toString());
				entity.setAttribute("health", e.getHealth()+"");
				
				entitiesNode.appendChild(entity);
			}
		}
		
		root.appendChild(entitiesNode);

		
		document.appendChild(root);

		Main.log("Saved: " + file.getPath());

		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(file);
			transformer.transform(source, result);
		} catch (TransformerException e) {
			e.printStackTrace();
			return;
		}

	}
	
	public static void savePlayer(String worldName, String username, Vector position, EAngle direction, PlayerInventory inventory, int health) {
		Main.log("Saving "+username+"...");
		File saves = new File("saves");
		File mods = new File("mods");
		File wname = new File("saves/" + worldName);
		File file = new File("saves/" + worldName + "/"+username+".xml");
		try {
			saves.mkdir();
			mods.mkdir();
			wname.mkdir();
			file.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		Document document;
		try {
			db = dbf.newDocumentBuilder();
			document = db.newDocument();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		Element root = document.createElement(username);
		
		for (Entry<ItemType, Integer> is : inventory.items.entrySet()) {
			Element stack = document.createElement(is.getKey().name);
			stack.setTextContent(is.getValue() + "");
			root.appendChild(stack);
		}
		root.setAttribute("x", ""+position.x);
		root.setAttribute("y", ""+position.y);
		root.setAttribute("z", ""+position.z);
		root.setAttribute("yaw", ""+direction.yaw);
		root.setAttribute("pitch", ""+direction.pitch);
		root.setAttribute("health", health+"");

		
		document.appendChild(root);

		
		Main.log("Saved player: " + file.getPath());

		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(file);
			transformer.transform(source, result);
		} catch (TransformerException e) {
			e.printStackTrace();
			return;
		}

	}

	int getAlienCount() {
		int count=0;
		for(Entity e : Entities.values()) {
			if(e instanceof Alien) {
				count++;
			}
		}
		return count;
	}
	
	private void filterAdjecentBlocks(Block bl) {
		filterBlock(bl);
		for (Block b : get6Blocks(bl, false).values()) {
			filterBlock(b);
		}
	}

	private void filterBlock(Block bl) {
		HashMap<BlockFace, Block> blocks6 = get6Blocks(bl, true);
		for (Entry<BlockFace, Block> entry : blocks6.entrySet()) {
			BlockFace key = entry.getKey();
			Block other = entry.getValue();
			if(!bl.HitboxPolygons.containsValue(key)) {
				Main.err("Unknown side in filterBlock("+bl+") "+key);
				return;
			}
			
			for (Entry<Polygon3D, BlockFace> entry2 : bl.HitboxPolygons.entrySet()) {
				if(entry2.getValue() == key) {
					Polygon3D side=entry2.getKey();
					if(!bl.fullblock || !other.fullblock || (!bl.transparent && other.transparent)) {
						side.adjecentFilter=true;
					}else {
						side.adjecentFilter=false;
					}
				}
					
			}

				
				
		}

	}


}
