package ml.sakii.factoryisland;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JLabel;
import javax.swing.Timer;

import ml.sakii.factoryisland.blocks.Block;
import ml.sakii.factoryisland.blocks.GrassBlock;
import ml.sakii.factoryisland.blocks.LoadListener;
import ml.sakii.factoryisland.blocks.ChestModuleBlock;
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
import ml.sakii.factoryisland.entities.PlayerEntity;
import ml.sakii.factoryisland.items.PlayerInventory;
import ml.sakii.factoryisland.items.Item;
import ml.sakii.factoryisland.net.GameClient;
import ml.sakii.factoryisland.net.GameServer;
import ml.sakii.factoryisland.net.PlayerMPData;

public class GameEngine{
	
	
	public World world;
	public CopyOnWriteArrayList<TickListener> TickableBlocks = new CopyOnWriteArrayList<>();
	public long Tick;
	//public CopyOnWriteArrayList<Entity> Entities = new CopyOnWriteArrayList<>();
	Timer ticker;
	
	//PhysicsThread physics=null;
	
	Timer physics;
	int physicsFPS=30;
	public PlayerInventory Inv = new PlayerInventory(this);
	
	
	public GameClient client;
	public GameServer server;
	
	float[] previousPos = new float[5];
	float[] currentPos = new float[5];


	int aliens=0;

	long physics1, physics2;
	float actualphysicsfps;
	
	/*TimerTask task = new TimerTask() {
        
			@Override
             public void run() {*/
	ActionListener physicsPerformer = new ActionListener() {

		
		@Override
		public void actionPerformed(ActionEvent e)

		{
			physics1 = physics2;
				physics2 = System.currentTimeMillis();

				if (physics1 == 0L)
				{
					actualphysicsfps = 60;
					//measurement = (float) CalcAverageTick(FPS);
				} else
				{
					actualphysicsfps = 1000f / (physics2 - physics1);
					//measurement = (float) CalcAverageTick(FPS);
				}
				
				world.getAllEntities().parallelStream().forEach(entity-> /*);
				for(Entity entity : world.getAllEntities()) */{
					if(entity instanceof Alien && entity.VerticalVector.equals(Main.GAME.PE.VerticalVector)) {
						Alien alien = (Alien)entity;
						Vector closest = null;
						
						if(server==null) {
							if(Main.GAME.PE.getPos().distance(alien.getPos())<10)
								closest = Main.GAME.PE.getPos();
						}else {
							float dst=Float.MAX_VALUE;
							
							for(PlayerMPData player : server.clients.values()) {
								float distance = player.position.distance(alien.getPos());
								if(distance<10 && distance < dst) {
									dst=distance;
									closest=player.position;
								}
							}
						}
						
						if(closest!=null) {
							alien.target.set(closest);
							//Main.log("closest:"+new Vector().set(closest).substract(alien.ViewFrom));
						}else {
							//	Main.log("random:"+new Vector().set(alien.target).substract(alien.ViewFrom));
						}
					
						alien.target.z=alien.getPos().z;
						
						Vector aim = new Vector().set(alien.target).substract(alien.getPos());
						if(aim.getLength()>0.2f) {
							aim.normalize();
							
							if(!Game.move(aim, 2, alien, physicsFPS, world)){
								alien.jump();
							}
							
							alien.ViewAngle.yaw=(float) Math.atan2(aim.x, aim.y);
							alien.update();
						}
						
					}
				
					if(!(entity instanceof PlayerEntity)) {
						if (world.getBlockUnderEntity(false, true, entity) == Block.NOTHING)
						{
							entity.fly(true);
						} else
						{
							entity.fly(false);
						}
						if (!entity.flying)
						{
					
							doGravity(entity, world, physicsFPS);
						
						
						}
					}
				
				
				}); // minden entity kï¿½d vï¿½ge
        	
			}


        
        };
	
	
	
	public GameEngine(String location, Game game, long seed, LoadMethod loadmethod, JLabel statusLabel) {
		init();
		
		switch(loadmethod) {
		case GENERATE:
			Main.log("Generating world " + location);
			statusLabel.setText("Generating world " + location);
			long startTime = System.currentTimeMillis();
			
			world = new World(location, this, game, false, statusLabel);
			
			generateTerrain(seed, statusLabel);	
			

			long finishTime = System.currentTimeMillis();
			Main.log("Done. World generation took: " + (finishTime-startTime)/1000.0f + " seconds.");
			statusLabel.setText("<html><body>Done. World generation took: " + (finishTime-startTime)/1000.0f + " seconds.<br>Loading...</body></html>");

			break;
		case MULTIPLAYER:
			world = new World(location, this, game, false, statusLabel);
			break;
		case EXISTING:
			Main.log("Loading world "+location+"...");
			statusLabel.setText("Loading world "+location+"...");
			world = new World(location, this, game, true, statusLabel);

			String str ="Map loading done: " + world.getSize() + " block loaded."; 
			Main.log(str);
			statusLabel.setText(str);
			break;
		}
		
		for(Block b : world.getWhole(false)) {
			if(b instanceof LoadListener) {
				((LoadListener)b).onLoad();
			}
		}
				
	}



	
	private void init() {
		
		ActionListener tickPerformer = new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent evt) {
                //...Perform a task...
        		Tick++;

            	if(server != null || (client==null && server==null)) {
            		
					ArrayList<TickListener> removed = new ArrayList<>(TickableBlocks);
					for(TickListener b : removed){
						if(Tick % ((Block)b).refreshRate == 0){
							if(!b.tick(Tick)){
								TickableBlocks.remove(b);
							}
						}
							
						
					}
					
					if(Math.random() < 0.004 && world.getAlienCount() < 6 && world.SpawnableSurface.size()>0) {
						int Min = 0;
						int Max = world.SpawnableSurface.size()-1;
						int index = Min + (int)(Math.random() * ((Max - Min) + 1));
						Vector pos = world.SpawnableSurface.get(index);
						Alien newAlien = new Alien(pos, new EAngle(40,0),"",new Random().nextLong(), GameEngine.this);
						//world.Entities.add(newAlien);
						world.addEntity(newAlien);
						
					}
            	
				
					if(Tick % (2f/Main.TICKSPEED) == 0) {
						for(Entity entity : world.getAllEntities()) {
							if(entity instanceof Alien) {
								Alien alien = ((Alien) entity);
								
								if(alien.getPos().distance(new Vector())>56) {
									world.killEntity(alien.ID);
								}else {
									double random = Math.random();
									//float dst = entity.getPos().distance(alien.target); 
									if(random < 0.4) {
										alien.target.set(entity.getPos());
									}else if(random>0.4){
										Random rnd = new Random();
										alien.target.set(new Vector(rnd.nextInt(20)-10, rnd.nextInt(20)-10, entity.getPos().z).add(entity.getPos()));
									}
									//Main.log("target:"+(new Vector().set(alien.target).substract(entity.getPos())));
									//Main.log("--------------------------------");
								}
							}
						}
					}
            	}
				
				if(Main.GAME != null && client != null && (Tick % (1/*/Main.TICKSPEED*/) == 0)){
					currentPos[0]=r(Main.GAME.PE.getPos().x,2);
					currentPos[1]=r(Main.GAME.PE.getPos().y,2);
					currentPos[2]=r(Main.GAME.PE.getPos().z,2);
					currentPos[3]=r(Main.GAME.PE.ViewAngle.yaw,3);
					currentPos[4]=r(Main.GAME.PE.ViewAngle.pitch,3);
					for(int i=0;i<5;i++) {
						if(previousPos[i] != currentPos[i]) {
							client.sendData("04," + Config.username + "," + 
									r(Main.GAME.PE.getPos().x,2) + "," + 
									r(Main.GAME.PE.getPos().y, 2) + "," + 
									r(Main.GAME.PE.getPos().z,2) + "," + 
									r(Main.GAME.PE.ViewAngle.yaw,3) +"," +  
									r(Main.GAME.PE.ViewAngle.pitch,3) );
							previousPos[0]=currentPos[0];
							previousPos[1]=currentPos[1];
							previousPos[2]=currentPos[2];
							previousPos[3]=currentPos[3];
							previousPos[4]=currentPos[4];
							break;
						}
					}
					
				}
				
            }};
            
        ticker = new Timer((int)(1000*Main.TICKSPEED) , tickPerformer);
        
        //physics = new Timer((int) (1000f/Main.PHYSICS_FPS) , serverMain); 
         
        
        
        physics = new Timer(1000/physicsFPS, physicsPerformer);
        
       // startPhysics();
		//physics = new java.util.Timer();

        
	}
	
	static void doGravity(Entity entity, World world, int physicsFPS) {
		//float FPS=Main.PHYSICS_FPS;
		Vector VerticalVector = entity.VerticalVector;
		if (!world.getBlockAtF(entity.getPos().x, entity.getPos().y,
				entity.getPos().z - ((1.7f + World.GravityAcceleration / physicsFPS) * VerticalVector.z)).solid)
		{
			entity.GravityVelocity -= World.GravityAcceleration / physicsFPS;
		}
	
		float resultant = (entity.JumpVelocity + entity.GravityVelocity);
		if (Math.abs(entity.JumpVelocity) + Math.abs(entity.GravityVelocity) != 0f)
		{
			float JumpDistance = resultant / physicsFPS * VerticalVector.z;
			if (resultant < 0)
			{// lefelé esik önmagához képest
				Block under = world.getBlockUnderEntity(false, true, entity);// world.getBlockAtF(entity.ViewFrom.x,
																			// entity.ViewFrom.y, entity.ViewFrom.z+JumpDistance);
				if (VerticalVector.z == 1)
				{
	
					if (under != Block.NOTHING && under.z + 1 >= entity.getPos().z - 1.7f + JumpDistance)
					{ // beleesne egy blokkba felï¿½lrï¿½l
						entity.getPos().z = under.z + 2.7f;
						entity.JumpVelocity = 0;
						entity.GravityVelocity = 0;
					} else
					{
						entity.getPos().z += JumpDistance;
					}
				} else
				{
					if (under != Block.NOTHING && under.z <= entity.getPos().z + 1.7f + JumpDistance)
					{ // beleesne egy blokkba alulról
						entity.getPos().z = under.z - 1.7f;
						entity.JumpVelocity = 0;
						entity.GravityVelocity = 0;
					} else
					{
						entity.getPos().z += JumpDistance;
					}
				}
			} else if (resultant > 0)
			{ // felfelé ugrik  önmagához képest
				Block above = world.getBlockUnderEntity(false, false, entity);// world.getBlockAtF(entity.ViewFrom.x,
																				// entity.ViewFrom.y,
																				// entity.ViewFrom.z+JumpDistance);
	
				if (VerticalVector.z == 1)
				{
					if (above != Block.NOTHING && above.z <= entity.getPos().z + JumpDistance)
					{ // belefejelne egy blokkba alulrï¿½l
						entity.getPos().z = above.z - 0.01f;
						entity.JumpVelocity = 0;
						entity.GravityVelocity = 0;
					} else
					{
						entity.getPos().z += JumpDistance;
					}
				} else
				{
					if (above != Block.NOTHING && above.z + 1 >= entity.getPos().z + JumpDistance)
					{ // belefejelne egy blokkba felülrõl
							entity.getPos().z = above.z + 1.01f;
							entity.JumpVelocity = 0;
							entity.GravityVelocity = 0;
					} else
					{
							entity.getPos().z += JumpDistance;
					}
	
				}
			}
	
		}
	
		if (entity.JumpVelocity > 0)
		{
			entity.JumpVelocity = Math.max(0, entity.JumpVelocity - World.GravityAcceleration / physicsFPS);
		}
	}
	
   
	
	private void generateTerrain(long seed, JLabel statusLabel) {
		
		world.seed = seed;

		
		Random RandomGen = new Random(seed);
		Main.log("Seed:" + seed);
		
		int sealevel = 0;//world.CHUNK_HEIGHT/2;
		
		Main.log("- Creating earth above sea level...");
		statusLabel.setText("- Creating earth above sea level...");
		System.gc();

		int hillCount = RandomGen.nextInt(8)+8;

		for(int i = 0;i<hillCount;i++){
			int z = RandomGen.nextInt(3)+3;
			int x = RandomGen.nextInt(world.CHUNK_WIDTH*world.MAP_RADIUS)-world.CHUNK_WIDTH*world.MAP_RADIUS/2;
			int y = RandomGen.nextInt(world.CHUNK_WIDTH*world.MAP_RADIUS)-world.CHUNK_WIDTH*world.MAP_RADIUS/2;
			float slope = RandomGen.nextFloat();

			for(int j=sealevel;j<sealevel+z;j++){
				
				int j2 = j-sealevel;
				float radius = slope*(z-j2)*(z-j2);
				for(float k=-radius;k<radius;k++){
					for(float l=-radius;l<radius;l++){
						int offsetX = (int) (x+k);
						int offsetY = (int) (y+l);
						if(world.getBlockAt(offsetX, offsetY, j) == Block.NOTHING) {
							world.addBlock(new StoneBlock(offsetX,offsetY,j,this), false);
						}
					}
				}
			}
		}
		Main.log("- Creating earth under sea level...");
		statusLabel.setText("- Creating earth under sea level...");
		System.gc();
		hillCount = RandomGen.nextInt(8)+8;

		for(int i = 0;i<hillCount;i++){
			int z = RandomGen.nextInt(3)+3;
			int x = RandomGen.nextInt(world.CHUNK_WIDTH*world.MAP_RADIUS)-world.CHUNK_WIDTH*world.MAP_RADIUS/2;
			int y = RandomGen.nextInt(world.CHUNK_WIDTH*world.MAP_RADIUS)-world.CHUNK_WIDTH*world.MAP_RADIUS/2;
			float slope = RandomGen.nextFloat();

			for(int j=sealevel;j>sealevel-z;j--){
				
				int j2 = -j+sealevel;
				float radius = slope*(z-j2)*(z-j2);
				for(float k=-radius;k<radius;k++){
					for(float l=-radius;l<radius;l++){
						int offsetX = (int) (x+k);
						int offsetY = (int) (y+l);
						if(world.getBlockAt(offsetX, offsetY, j) == Block.NOTHING) {

							world.addBlock(new StoneBlock(offsetX,offsetY,j,this), false);
						}
					}
				}
			}
		}
		System.gc();
		Main.log("- Adding pond...");
		statusLabel.setText("- Adding pond...");
		int top = world.getTop(0, 0);// == 0 ? world.CHUNK_HEIGHT/2 : world.getTop(0, 0).z;

		for(int i = -1; i <= 1; i++)
			for(int j = -1; j <= 1; j++)
				world.addBlock(new WaterBlock(i, j, top, this), true);
		
		Main.log("- Sprinkling sand and grass...");
		
		for(Block b : world.getWhole(false)){
			if(!b.name.equals("Water") && !b.name.equals("Old")){
				if(isNearWater(b)){
					world.addBlock(new GrassBlock(b.x, b.y, b.z,this), true);
				}
				if(world.getBlockAt(b.x, b.y, b.z+1) instanceof WaterBlock){
					world.addBlock(new SandBlock(b.x, b.y, b.z,this), true);
				}

			}
			
		}
		
		/*int oilCount = RandomGen.nextInt(4)+1;
		Main.log("- Spilling oils: " + oilCount + " ...");
		for(int i=0;i<oilCount;i++){
			int x, y;
			do{
				x = RandomGen.nextInt(world.MAP_RADIUS*world.CHUNK_WIDTH*2)-world.MAP_RADIUS*world.CHUNK_WIDTH;
				y = RandomGen.nextInt(world.MAP_RADIUS*world.CHUNK_WIDTH*2)-world.MAP_RADIUS*world.CHUNK_WIDTH;
			}while(!world.getBlockAt(x, y, 1).name.equals("Water"));
			for(int j=x;j<=x+RandomGen.nextInt(1)+1;j++){
				for(int k=y;k<=y+RandomGen.nextInt(1)+1;k++){
					world.addBlock(new OilBlock(j,k,1, this, null), true);
				}
			}
		}*/
		
		//int bushCount = RandomGen.nextInt(10)+5;
		Main.log("- Planting tree ...");
		statusLabel.setText("- Planting tree ...");
		
		ArrayList<Block> grasses = new ArrayList<>();
		for(Block b : world.getWhole(false)){
			if(b instanceof GrassBlock){
				grasses.add(b);
			}
		}
		
		//for(int i=0;i<bushCount;i++){
			//int x, y, z;
			/*do{
				x = RandomGen.nextInt(world.MAP_RADIUS*world.CHUNK_WIDTH*2)-world.MAP_RADIUS*world.CHUNK_WIDTH;
				y = RandomGen.nextInt(world.MAP_RADIUS*world.CHUNK_WIDTH*2)-world.MAP_RADIUS*world.CHUNK_WIDTH;
				z = 0;
				while(!world.getBlockAt(x, y, z).name.equals("Grass") && z<6){
					z++;
				}
			}while(!world.getBlockAt(x, y, z).name.equals("Grass"));*/
			/*Block grass = null;
			do{
			int index = RandomGen.nextInt(grasses.size());
			grass = grasses.get(index);
			}while(world.getBlockAt(grass.x, grass.y, grass.z+1) != Block.NOTHING);
			world.addBlock(new LeafBlock(grass.x, grass.y, grass.z+1, this, null), true);
		}*/
		
		
		//Block grass = null;
		//do{
		if(grasses.size()>0) {
			int index = RandomGen.nextInt(grasses.size());
			Block grass = grasses.get(index);
			//}while(world.getBlockAt(grass.x, grass.y, grass.z+1) != Block.NOTHING);
			world.addBlock(new SaplingBlock(grass.x, grass.y, grass.z+1, this), true);
		}else {
			Main.err("No grass found!!");
			world.addBlock(new SaplingBlock(0, 0, 1, this), true);
		}
		
		Main.log("- Lunar module landing ...");
		statusLabel.setText("- Lunar module landing ...");
		
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
					lmindex++;
				}
			}
		}
		world.addBlocks(lunarModule, true);
		
	}
	
	void afterGen() {
		Main.log("- Executing post-worldgen instructions...");
		if(Main.Mods.size()>0) {
			
			for(String mod : Main.Mods) {
				ModBlock block = new ModBlock(mod, 0, 0, 0, null);
				block.generateWorld();
			}
		}
		for(Block b : world.getWhole(false)) {
			if(b instanceof WorldGenListener) {
				((WorldGenListener)b).generateWorld();
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
	


	
	static float r(float number, int decimals) {
		int pow = (int)Math.pow(10, decimals);
		return Math.round(number * pow) * 1f / pow;
	}
	
	public Block createBlockByName(String name, int x, int y, int z) {

		if(Main.ModRegistry.contains(name)) {
			return createApiBlock(name, x, y, z);
		}
		Item item = Main.Items.get(name);
		String className = item.className;
		return createBlock(className, x, y, z, this);
	}
	
	public Block createBlockByClass(String className, int x, int y, int z) {
		if(Main.ModRegistry.contains(className)) {
			return createApiBlock(className, x, y, z);
		}
		return createBlock(className, x, y, z, this);
	}
	
	private static Block createBlock(String className, int x, int y, int z, GameEngine engine) {
		try{
			Class<?> blockClass = Class.forName(className);
			if(!blockClass.getName().equals(Block.class.getName())){
				if(Block.class.isAssignableFrom(blockClass)){
					Constructor<?> ctor = blockClass.getConstructor(int.class, int.class, int.class, GameEngine.class);
					Block object = (Block)(ctor.newInstance(new Object[] { x ,y , z, engine}));
					return object;
				}
				Main.err("Could create block: "+className+" is not an instance of " + Block.class.getName());
				return Block.NOTHING;
			}
			Main.err("Could create block: Do not use ml.sakii.factoryisland.blocks.Block to place!");
			return Block.NOTHING;
		
		
		
		}catch(Exception e){
			Main.err("Could not create block: "+ className);
			e.printStackTrace();
			return Block.NOTHING;
		}
	}
	
	private Block createApiBlock(String name, int x, int y, int z) {

		return new ModBlock(name, x, y, z, this);
	}
	
	public static Block nullBlock(String className) {
		return createBlock(className, 0, 0, 0, null);
	}
	

	public void startPhysics() {
		physics2=System.currentTimeMillis();
		physics.start();
		//physics = new java.util.Timer();
		//physics.scheduleAtFixedRate(task, 0, 1000/physicsFPS);
		/*if(physics != null) {
		//	Main.err("physics thread already created:"+physics.toString());
		//	new Exception().printStackTrace(System.out);
		}else {
			//physics = new PhysicsThread(this);
			//physics=new Timer();

		//	Main.log("physics started");
		//	new Exception().printStackTrace(System.out);

		}*/
	}
	
	public void stopPhysics() {
		physics.stop();
		//physics.cancel();
		/*if(physics == null) {
		//	Main.err("physics thread not created");
		//	new Exception().printStackTrace(System.out);
//
		}else {
			physics=null;
		//	Main.log("physics stopped");
		//	new Exception().printStackTrace(System.out);

		}*/
	}
}
