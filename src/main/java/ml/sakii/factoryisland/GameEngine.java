package ml.sakii.factoryisland;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Random;
import java.util.function.Consumer;

import javax.swing.JOptionPane;
import ml.sakii.factoryisland.blocks.Block;
import ml.sakii.factoryisland.blocks.BlockFace;
import ml.sakii.factoryisland.blocks.GrassBlock;
import ml.sakii.factoryisland.blocks.ChestModuleBlock;
import ml.sakii.factoryisland.blocks.DayNightListener;
import ml.sakii.factoryisland.blocks.Fluid;
import ml.sakii.factoryisland.blocks.TankModuleBlock;
import ml.sakii.factoryisland.blocks.ModBlock;
import ml.sakii.factoryisland.blocks.SandBlock;
import ml.sakii.factoryisland.blocks.SaplingBlock;
import ml.sakii.factoryisland.blocks.StoneBlock;
import ml.sakii.factoryisland.blocks.TickListener;
import ml.sakii.factoryisland.blocks.WaterBlock;
import ml.sakii.factoryisland.blocks.WorldGenListener;
import ml.sakii.factoryisland.entities.Alien;
import ml.sakii.factoryisland.entities.Entity;
import ml.sakii.factoryisland.entities.PlayerMP;
import ml.sakii.factoryisland.items.ItemType;
import ml.sakii.factoryisland.net.GameClient;
import ml.sakii.factoryisland.net.GameServer;

public class GameEngine{
	
	
	public World world;
	public final ArrayList<Point3D> TickableBlocks = new ArrayList<>();
	final ArrayList<DayNightListener> DayNightBlocks = new ArrayList<>();
	public long Tick=0;
	
	
	public GameClient client;
	public GameServer server;
	
	private final Vector previousPos=new Vector();
	private final EAngle previousAim=new EAngle();
	private final HashSet<Block> surface = new HashSet<>();

	public Game game;
	

	String error="OK";

	public GameEngine(String location, Game game, long seed, LoadMethod loadmethod, WorldType type, int size, Consumer<String> update) throws Exception {
		this.game=game;
		
		switch(loadmethod) {
		case GENERATE:
			update.accept( "Generating world " + location);
			long startTime = System.currentTimeMillis();
			
			world = new World(location, this, game, false, update);
			if(!world.success.equals("OK")) {
				throw new Exception(world.success);
			}
			generateTerrain(seed, type, size, update);	
			

			long finishTime = System.currentTimeMillis();
			update.accept("Worldgen done in " + (finishTime-startTime)/1000.0f + " seconds, loading...");
			world.loading=false;
			break;
		case MULTIPLAYER:
			world = new World(location, this, game, false, update);
			if(!world.success.equals("OK")) {
				throw new Exception(world.success);
			}
			break;
		case BENCHMARK:
		case EXISTING:
			update.accept("Loading world "+location+"...");
			world = new World(location, this, game, true, update);
			if(!world.success.equals("OK")) {
				error = (world.success);
				return;
			}
			update.accept("Map loading done: " + world.getSize() + " block loaded.");
			world.loading=false;
		}
		
		
				
	}
	
	public void performTick() {

    	if(server != null || client==null) {
    		
    		ArrayList<Point3D> current = new ArrayList<>(TickableBlocks);
			TickableBlocks.clear();
			for(Point3D p : current) {
				
				Block bl = world.getBlockAtP(p);
				if(bl != Block.NOTHING) {
					
					if(Tick % bl.getRefreshRate() == 0){
						if(bl instanceof TickListener b) {
							
							if(b.tick(Tick)) {
								
								
								TickableBlocks.add(p);
							}
						
						}else {
							Main.err("Attempted to tick non-tickable block:"+bl);
						}
					}else {
						TickableBlocks.add(p);
					}
				}else if(Main.verbose){
					// Air block ticked
					Main.err("Attempted to tick air block:"+p);
				}
				
			}
			
			

			if(world.getAlienCount() < 6 && Math.random() < 0.004) {
				
				surface.clear();
				world.getSurface(surface);
				
				if(surface.size()>0) {
					int Min = 0;
					Vector pos = null;
					Block b;
						int index = Min + (int)(Math.random() * ((surface.size()-1 - Min) + 1));
						b = surface.toArray(new Block[] {})[index];
						
							
							
						for(Entry<Polygon3D,BlockFace> entry : b.HitboxPolygons.entrySet()) {
							if(entry.getValue() == BlockFace.TOP || entry.getValue() == BlockFace.BOTTOM) {
								if(entry.getKey().getLight()<3 && game.Objects.contains(entry.getKey())) {
									pos=entry.getKey().centroid;
								}
								break;
							}
		
						}
							
					if(pos!=null) {
						Alien newAlien = new Alien(new Vector().set(Vector.PLAYER).multiply(Math.signum(pos.z-0.5f)).add(pos), new EAngle(40,0),"",10,new Random().nextLong(), GameEngine.this);
						world.addEntity(newAlien, true);
					}
				}
				
			}
    	
		
			if(Tick % (2*Globals.TICKSPEED) == 0) {
				for(Entity entity : new ArrayList<>(world.getAllEntities())) { // hurtEntity miatt ConcurrentModificationException
					if(entity instanceof Alien alien) {
						Vector alienPos = alien.getPos();
						
						if(alienPos.getLength()>56) {
							world.hurtEntity(alien.ID, alien.getHealth(), true);
						}else if(!alien.locked){
							double random = Math.random();
							if(random < 0.4) {
								alien.target.set(alienPos);
							}else if(random>0.4){
								Random rnd = new Random();
								alien.target.set(rnd.nextInt(20)-10, rnd.nextInt(20)-10, alienPos.z).add(alienPos);
							}

						}
						for(Entity data : new ArrayList<>(world.getAllEntities())) { // hurtEntity miatt ConcurrentModificationException
							if(data instanceof PlayerMP && data != PlayerMP.ServerPerson) {
								
								if(data.getPos().distance(alien.getPos())<0.2) {
									world.hurtEntity(data.ID, 3, true);
								}
							}
						}
					}
					
					
				}
			}
			
			if(Tick % Globals.AUTOSAVE_INTERVAL == 0) {
				Main.log("Autosave started...");
				world.saveByShutdown();
				Main.log("Autosave finished.");
			}
			
			if(Tick % Globals.ENTITYSYNCRATE == 0 && server != null) {
				StringBuilder data = new StringBuilder();
				data.append("16");
				int counter = 0;
				for(Entity e : world.getAllEntities()) {
					if(!(e instanceof PlayerMP)) {
						data.append(",");
						data.append(e);
						counter++;
					}
				}
				if(counter>0) {
					for(PlayerMP client : server.clients.values()) {
						if(client != PlayerMP.ServerPerson) {
							server.sendData(data.toString(),client.socket);
						}
					}
				}
			}
			
			//ezek előre ki lettek számolva: sin(x/TICKS_PER_DAY*2*PI)=3/14 és -3/14
			for(DayNightListener dnl : DayNightBlocks) {
				Block b =(Block)dnl; 
				if(b.z>=0 && Tick % Globals.TICKS_PER_DAY == 2475) {
					dnl.onDay();
				}else if(b.z>=0 && Tick % Globals.TICKS_PER_DAY == 33525) {
					dnl.onNight();
				}else if(b.z<0 && Tick % Globals.TICKS_PER_DAY == 38745) {
					dnl.onDay();
				}else if(b.z<0 && Tick % Globals.TICKS_PER_DAY == 69525) {
					dnl.onNight();
				}

				
			}
			
			if(Tick % Globals.TICKS_PER_DAY == 2475) {
				Main.log("Day on top");
			}else if(Tick % Globals.TICKS_PER_DAY == 33525) {
				Main.log("Night on top");
			}else if(Tick % Globals.TICKS_PER_DAY == 38745) {
				Main.log("Day on bottom");
			}else if(Tick % Globals.TICKS_PER_DAY == 69525) {
				Main.log("Night on bottom");
			}

			


    	} //vege a szerveroldali dolgoknak



		if(Tick % Globals.ENTITYSYNCRATE == 0) {
			
			if(game != null && client != null){

					if( game.PE.getPos().distance(previousPos)>0 || Math.abs(previousAim.yaw-game.PE.ViewAngle.yaw)>0) {
						client.sendPlayerPos();

						previousPos.set(game.PE.getPos());
						previousAim.set(game.PE.ViewAngle);
					}
				
			}
			

		}
		
		if(Tick % (Globals.TICKS_PER_DAY/Globals.LIGHT_UPDATES_PER_DAY) == 0) {
			game.updateSkyLight();
		}
		Tick++;
	}

	
				
	public void performPhysics(float FPS){
		/*physics1 = physics2;
		physics2 = System.currentTimeMillis();

		if (physics1 == 0L)
		{
			actualphysicsfps = 1f/Globals.PHYSICS_FPS;
		} else
		{
			actualphysicsfps = 1000f / (physics2 - physics1);
		}*/
		for(Entity entity : world.getAllEntities()) {
		//world.getAllEntities().parallelStream().forEach(entity-> {
			if(entity instanceof Alien alien) {
				Vector closest = null;
				
				
				Vector alienPos = alien.getPos();
				

				
				float dst=Float.MAX_VALUE;
				
				for(Entity e : world.Entities.values()) {
					if(e instanceof PlayerMP && e != PlayerMP.ServerPerson) {
						PlayerMP player = (PlayerMP)e;
						//min kiválasztás
						float distance = player.getPos().distance(alienPos);
						if(distance<10 && distance < dst) {
							dst=distance;
							closest=player.getPos();
						}
					}
					
				}
				
				
				if(closest!=null) {
					alien.target.set(closest);
					alien.locked=true;
				}else {
					alien.locked=false;
				}
			
				alien.target.z=alienPos.z;
				
				Vector aim = alien.aim.set(alien.target).substract(alienPos);
				if(aim.getLength()>0.2f) {
					aim.normalize();
						if(!world.walk(aim, 2, alien, FPS, false)){ // false mert a sync mashol van
							alien.jump();
						}
					alien.ViewAngle.yaw=(float) Math.toDegrees(Math.atan2(aim.y, aim.x));
					alien.update();
				}
				
			}

			if(!(entity instanceof PlayerMP)) {
				Block under = world.getBlockUnderEntity(false, true, entity);
				if (!entity.flying && under == Block.NOTHING)
				{
					entity.fly(true);
				} else if(entity.flying && under != Block.NOTHING)
				{
					entity.fly(false);
				}
				if (!entity.flying)
				{
			
					doGravity(entity, FPS);
					entity.update();
				
				}
			}
		
		
		} // minden entity kód vége
	
	}
		
	public boolean isDay(int z) {
		long realTime = Tick % Globals.TICKS_PER_DAY;
		float timePercent = (realTime*1f/Globals.TICKS_PER_DAY);
		double light = Math.sin(2*Math.PI*timePercent);
		
		
		if(z>=0) {
			return light > 3f/14f;
		}else {
			return  light < -3f/14f;
		}
	}

	void doGravity(Entity entity, float physicsFPS) {
		Vector entityPos = entity.getPos();
		Vector VerticalVector = entity.VerticalVector;

		if (!world.getBlockAt(entityPos.x, entityPos.y, entityPos.z - ((1.7f + Globals.GravityAcceleration / physicsFPS) * Math.signum(VerticalVector.z))).isSolid())
		{
			entity.GravityVelocity -= Globals.GravityAcceleration / physicsFPS;
		}
	
		float resultant = (entity.JumpVelocity + entity.GravityVelocity);
		if (Math.abs(entity.JumpVelocity) + Math.abs(entity.GravityVelocity) != 0f)
		{
			float JumpDistance = resultant / physicsFPS * Math.signum(VerticalVector.z);
			if (resultant < 0)
			{// lefelé esik önmagához képest
				Block under = world.getBlockUnderEntity(false, true, entity);
				
				if (VerticalVector.z > 0)
				{
	
					if (under != Block.NOTHING && under.z + 1 >= entityPos.z - 1.7f + JumpDistance)
					{ // beleesne egy blokkba felülrõl
						entityPos.z = under.z + 2.7f;
						entity.JumpVelocity = 0;
						entity.GravityVelocity = 0;
					} else
					{
						entityPos.z += JumpDistance;
					}
				} else
				{
					if (under != Block.NOTHING && under.z <= entityPos.z + 1.7f + JumpDistance)
					{ // beleesne egy blokkba alulról
						entityPos.z = under.z - 1.7f;
						entity.JumpVelocity = 0;
						entity.GravityVelocity = 0;
					} else
					{
						entityPos.z += JumpDistance;
					}
				}
			} else if (resultant > 0)
			{ // felfelé ugrik  önmagához képest
				Block above = world.getBlockUnderEntity(false, false, entity);
	
				if (VerticalVector.z > 0)
				{
					if (above != Block.NOTHING && above.z <= entityPos.z + JumpDistance)
					{ // belefejelne egy blokkba alulról
						entityPos.z = above.z - 0.01f;
						entity.JumpVelocity = 0;
						entity.GravityVelocity = 0;
					} else
					{
						entityPos.z += JumpDistance;
					}
				} else
				{
					if (above != Block.NOTHING && above.z + 1 >= entityPos.z + JumpDistance)
					{ // belefejelne egy blokkba felülrõl
							entityPos.z = above.z + 1.01f;
							entity.JumpVelocity = 0;
							entity.GravityVelocity = 0;
					} else
					{
							entityPos.z += JumpDistance;
					}
	
				}
			}
	
		}
	
		if (entity.JumpVelocity > 0)
		{
			entity.JumpVelocity = Math.max(0, entity.JumpVelocity - Globals.GravityAcceleration / physicsFPS);
		}
	}
	
   
	
	private void generateTerrain(long seed, WorldType type, int radius, Consumer<String> update) {
		switch(type) {
		case FLAT: generateTerrainFlat(seed, radius, update); break;
		case SPHERE: generateTerrainSphere(seed, radius, update); break;
		}
	}
	
	private void generateTerrainSphere(long seed, int radius, Consumer<String> update) {
		
		world.seed = seed;

		
		Main.log("Seed:" + seed);
		//int radius = world.MAP_RADIUS;
		int diameter = radius*2;
		double total = diameter*diameter*diameter;
		for(int x=-radius;x<radius;x++) {
			for(int y=-radius;y<radius;y++) {
				for(int z=-radius;z<radius;z++) {
					if(x*x+y*y+z*z<=radius*radius) {
						world.addBlockNoReplace(new StoneBlock(x,y,z,this), false);
					}
					
				}
				
				double x2 = (x+radius)*diameter*diameter;
				double y2 = (y+radius)*diameter;
				//double z2 = z+radius;
				
				double percent =Math.round((x2+y2)/total*100); 
				update.accept("Generating sphere - "+percent+"%");
				
				
			}
		}
		Tick=3000;
	}
	
	
	
	private void generateTerrainFlat(long seed, int mapRadius, Consumer<String> update) {
		
		world.seed = seed;

		
		Random RandomGen = new Random(seed);
		Main.log("Seed:" + seed);
		

		
		
		int sealevel = 0;
		
		
		
		update.accept("- Adding pond...");
		int top = world.getTop(0, 0);// == 0 ? world.CHUNK_HEIGHT/2 : world.getTop(0, 0).z;

		for(int i = -1; i <= 1; i++)
			for(int j = -1; j <= 1; j++)
				
				world.addBlockNoReplace(new WaterBlock(i, j, top, this), false);
		
		
		
		update.accept("- Creating earth above sea level...");
		System.gc();

		int hillCount = Main.small? RandomGen.nextInt(3)+2 : RandomGen.nextInt(8)+8;
		Point3D tmpPoint=new Point3D();
		for(int i = 0;i<hillCount;i++){
			int z = RandomGen.nextInt(3)+3;
			int x = RandomGen.nextInt(world.CHUNK_WIDTH*mapRadius)-world.CHUNK_WIDTH*mapRadius/2;
			int y = RandomGen.nextInt(world.CHUNK_WIDTH*mapRadius)-world.CHUNK_WIDTH*mapRadius/2;
			float slope = RandomGen.nextFloat();

			for(int j=sealevel;j<sealevel+z;j++){
				
				int j2 = j-sealevel;
				float radius = slope*(z-j2)*(z-j2);
				for(float k=-radius;k<radius;k++){
					for(float l=-radius;l<radius;l++){
						int offsetX = (int) (x+k);
						int offsetY = (int) (y+l);
						tmpPoint.set(offsetX, offsetY, j);
						if(world.getBlockAtP(tmpPoint) == Block.NOTHING) {
							world.addBlockNoReplace(new StoneBlock(offsetX,offsetY,j,this), false);
						}
					}
				}
			}
		}
		update.accept("- Creating earth under sea level...");
		System.gc();
		hillCount = Main.small? RandomGen.nextInt(3)+2 : RandomGen.nextInt(8)+8;

		for(int i = 0;i<hillCount;i++){
			int z = RandomGen.nextInt(3)+3;
			int x = RandomGen.nextInt(world.CHUNK_WIDTH*mapRadius)-world.CHUNK_WIDTH*mapRadius/2;
			int y = RandomGen.nextInt(world.CHUNK_WIDTH*mapRadius)-world.CHUNK_WIDTH*mapRadius/2;
			float slope = RandomGen.nextFloat();

			for(int j=sealevel;j>sealevel-z;j--){
				
				int j2 = -j+sealevel;
				float radius = slope*(z-j2)*(z-j2);
				for(float k=-radius;k<radius;k++){
					for(float l=-radius;l<radius;l++){
						int offsetX = (int) (x+k);
						int offsetY = (int) (y+l);
						tmpPoint.set(offsetX, offsetY, j);
						if(world.getBlockAtP(tmpPoint) == Block.NOTHING) {
							world.addBlockNoReplace(new StoneBlock(offsetX,offsetY,j,this), false);
						}
					}
				}
			}
		}
		System.gc();

		
		update.accept("- Sprinkling sand and grass...");
		
		for(Block b : world.getWhole()){
			if(!b.name.equals("Water") && !b.name.equals("Old")){
				if(isNearWater(b)){
					world.destroyBlock(b, false);
					world.addBlockNoReplace(new GrassBlock(b.x, b.y, b.z,this), false);
				}
				if(world.getBlockAt(b.x, b.y, b.z+1) instanceof WaterBlock){
					world.destroyBlock(b, false);
					world.addBlockNoReplace(new SandBlock(b.x, b.y, b.z,this), false);
				}

			}
			
		}

		update.accept("- Planting tree ...");
		
		ArrayList<Block> grasses = new ArrayList<>();
		for(Block b : world.getWhole()){
			if(b instanceof GrassBlock){
				grasses.add(b);
			}
		}
		

		if(grasses.size()>0) {
			int index = RandomGen.nextInt(grasses.size());
			Block grass = grasses.get(index);
			Block replace = world.getBlockAt(grass.x, grass.y, grass.z+1);
			if(replace != Block.NOTHING) {
				world.destroyBlock(replace, false);
			}
			world.addBlockNoReplace(new SaplingBlock(grass.x, grass.y, grass.z+1, this), false);
		}else {
			Main.err("No grass found!!");
			Block replace = world.getBlockAt(0,0,1);
			if(replace != Block.NOTHING) {
				world.destroyBlock(replace, false);
			}
			world.addBlockNoReplace(new SaplingBlock(0, 0, 1, this), false);
		}
		
		update.accept("- Lunar module landing ...");
		
		int lmbx = RandomGen.nextInt(60)-30;
		int lmby = RandomGen.nextInt(60)-30;
		int lmbz = world.getTop(lmbx, lmby);
		Block[] lunarModule = new Block[24];
		lunarModule[0] = new TankModuleBlock(lmbx, lmby, lmbz+1, this);
		lunarModule[5] = new TankModuleBlock(lmbx, lmby, lmbz+2, this);
		lunarModule[1] = new TankModuleBlock(lmbx+1, lmby+1, lmbz+1, this);
		lunarModule[2] = new TankModuleBlock(lmbx-1, lmby-1, lmbz+1, this);
		lunarModule[3] = new TankModuleBlock(lmbx+1, lmby-1, lmbz+1, this);
		lunarModule[4] = new TankModuleBlock(lmbx-1, lmby+1, lmbz+1, this);
		
		
		int lmindex=6;
		for(int z=3;z<=4;z++) {
			for(int x=-1;x<=1;x++) {
				for(int y=-1;y<=1;y++) {
					lunarModule[lmindex] = new ChestModuleBlock(lmbx+x, lmby+y, lmbz+z, this);
					((ChestModuleBlock)lunarModule[lmindex]).getInv().add(Main.Items.get("TestPowerWire"), 1+(int)(Math.random()*20), true);
					((ChestModuleBlock)lunarModule[lmindex]).getInv().add(Main.Items.get("Silicon"), 1+(int)(Math.random()*10), true);
					((ChestModuleBlock)lunarModule[lmindex]).getInv().add(Main.Items.get("TestPowerConsumer"), 1+(int)(Math.random()*10), true);
					((ChestModuleBlock)lunarModule[lmindex]).getInv().add(Main.Items.get("Drill"), 1+(int)(Math.random()*10), true);
					((ChestModuleBlock)lunarModule[lmindex]).getInv().add(Main.Items.get("WaterMill"), 1+(int)(Math.random()*10), true);
					((ChestModuleBlock)lunarModule[lmindex]).getInv().add(Main.Items.get("Water"), 1+(int)(Math.random()*10), true);
					((ChestModuleBlock)lunarModule[lmindex]).getInv().add(Main.Items.get("Sapling"), 1+(int)(Math.random()*10), true);
					lmindex++;
				}
			}
		}
		for(int i=0;i<24;i++) {
			Block replace = world.getBlockAtP(lunarModule[i].pos);
			if(replace != Block.NOTHING) {
				world.destroyBlock(replace, false);
			}
			world.addBlockNoReplace(lunarModule[i], true);
		}
		Tick=3000;
		
	}
	
	void afterGen() {
		Main.log("- Executing post-worldgen instructions...");
		if(Main.ModRegistry.size()>0) {
			
			for(String mod : Main.ModRegistry) {
				ModBlock block = new ModBlock(mod, 0, 0, 0, null);
				block.generateWorld();
			}
		}
		for(Block b : world.getWhole()) {
			if(b instanceof WorldGenListener wgl) {
				wgl.generateWorld();
			}
		}
		world.saveByShutdown();
	}
	
	private boolean isNearWater(Block b){
		for(Block b2 : world.get6Blocks(b, false).values()){
			if(b2 instanceof WaterBlock){
				return true;
			}
		}
		return false;
	}
	
	public Block createBlockByName(String name, int x, int y, int z) {

		if(Main.ModRegistry.contains(name)) {
			return createApiBlock(name, x, y, z);
		}
		ItemType item = Main.Items.get(name);
		String className = item.className;
		return createBlock(className, x, y, z, this);
	}
	
	
	
	public Fluid createFluid(String name, int x, int y, int z, int height) {
		Fluid result=null;
		try {
			String className = Main.Items.get(name).className;
			Class<?> fluidClass = Class.forName(className);
			if(!fluidClass.getName().equals(Fluid.class.getName()) && Fluid.class.isAssignableFrom(fluidClass)) {
				
				Constructor<?> ctor = fluidClass.getConstructor(int.class, int.class, int.class,int.class, GameEngine.class);
				result = (Fluid)(ctor.newInstance(new Object[] { x ,y , z,height, this}));
				
			}else {
				Main.err("Invalid class name when creating fluid:"+className);
			}
		}catch(Exception e) {
			Main.err("Could not create fluid: "+ name+","+x+","+y+","+z+","+height);
			e.printStackTrace();
		}
		return result;
		
	}
	
	Block createBlockByClass(String className, int x, int y, int z) {
		if(Main.ModRegistry.contains(className)) {
			return createApiBlock(className, x, y, z);
		}
		return createBlock(className, x, y, z, this);
	}
	
	private static Block createBlock(String className, int x, int y, int z, GameEngine engine) {
		Block result = Block.NOTHING;
		try{
			Class<?> blockClass = Class.forName(className);
			
			if(!blockClass.getName().equals(Block.class.getName())){
				if(Block.class.isAssignableFrom(blockClass)){
					Constructor<?> ctor = blockClass.getConstructor(int.class, int.class, int.class, GameEngine.class);
					result = (Block)(ctor.newInstance(new Object[] { x ,y , z, engine}));
				}else {
					Main.err("Could create block: "+className+" is not an instance of " + Block.class.getName());
				}
				
			}else {
				Main.err("Could create block: Do not use ml.sakii.factoryisland.blocks.Block to place!");
			}
			
		}catch(Exception e){
			Main.err("Could not create block: "+ className);
			e.printStackTrace();
		}
		return result;
	}
	
	private Block createApiBlock(String name, int x, int y, int z) {

		return new ModBlock(name, x, y, z, this);
	}
	
	double getTimePercent() {
		
		double hours=Tick%Globals.TICKS_PER_DAY;
		double skyLightF=(hours/Globals.TICKS_PER_DAY);
		return skyLightF;
	}
	
	static double getTimePercent(long Tick) {
		double hours=Tick%Globals.TICKS_PER_DAY;
		double skyLightF=(hours/Globals.TICKS_PER_DAY);
		return skyLightF;
	}
	
	String startServer() {
		String error=null;
		if (client == null && server == null)
		{
			server = new GameServer(this);
			server.start();
			if(server.Listener.acceptThread.success.equals("OK")) {
				
				Main.log("Server started");

				error = startClient("localhost", server.Listener.acceptThread.port, game);
				if (error == null)
				{
					if(!Main.headless) {
						GUIManager.showMessageDialog("Server opened to LAN at port " + server.Listener.acceptThread.port, "Press SPACE to continue", JOptionPane.INFORMATION_MESSAGE);
					}
				}
				
				
			}else {
				error = server.Listener.acceptThread.success;
			}
		}
		return error;
	}
	
	String startClient(String IP, int port, Game game)
	{
		client = new GameClient(game, this);	
		String error = client.connect(IP, port);
		if (error == null)
		{
			client.start();
			Main.log("Client started");
		}

		return error;

	}
	

	public void disconnect(String error) {
		if(error !=null) {
			Main.err(error);
			if(!Main.headless) {
				GUIManager.showMessageDialog(error, "Disconnected", JOptionPane.ERROR_MESSAGE);
			}
		}
		//ticker.stop();
		//stopPhysics();
		if (client != null)
		{
				Main.log("disconnecting from multiplayer");
				if(error != null) {
					client.kill(false);
				}else {
					client.kill(true);
				}
				

			if (server != null)
			{
				server.kill();

			}


		} else
		{
			world.saveByShutdown();
		}
	}
	
	public boolean isSingleplayer() {
		return client == null && server == null;
	}
	public boolean isLocalMP() {
		return client != null && server != null;
	}
	public boolean isRemoteMP() {
		return client != null && server == null;
	}
}
