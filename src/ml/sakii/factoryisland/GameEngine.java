package ml.sakii.factoryisland;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JLabel;
import javax.swing.Timer;

import ml.sakii.factoryisland.blocks.Block;
import ml.sakii.factoryisland.blocks.BlockFace;
import ml.sakii.factoryisland.blocks.GrassBlock;
import ml.sakii.factoryisland.blocks.ChestModuleBlock;
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
	//public Set<Point3D> TickableBlocks =  Collections.newSetFromMap(new ConcurrentHashMap<Point3D, Boolean>());
	public CopyOnWriteArrayList<Point3D> TickableBlocks = new CopyOnWriteArrayList<>();
	public long Tick;//, day, hours;
	//public static double skyLightF;
	
	//public CopyOnWriteArrayList<Entity> Entities = new CopyOnWriteArrayList<>();
	Timer ticker;
	
	//PhysicsThread physics=null;
	
	Timer physics;
	int physicsFPS=30;
	
	
	public GameClient client;
	public GameServer server;
	
	//float[] previousPos = new float[5];
	//float[] currentPos = new float[5];
	Vector previousPos=new Vector();
	EAngle previousAim=new EAngle();

	int aliens=0;

	long physics1, physics2;
	float actualphysicsfps;
	long lastupdate;

	
	
	String error="OK";

	public GameEngine(String location, Game game, long seed, LoadMethod loadmethod, JLabel statusLabel) throws Exception {
		initTimers();
		
		switch(loadmethod) {
		case GENERATE:
			Main.log("Generating world " + location);
			statusLabel.setText("Generating world " + location);
			long startTime = System.currentTimeMillis();
			
			world = new World(location, this, game, false, statusLabel);
			if(!world.success.equals("OK")) {
				throw new Exception(world.success);
			}
			generateTerrain(seed, statusLabel);	
			

			long finishTime = System.currentTimeMillis();
			Main.log("Done. World generation took: " + (finishTime-startTime)/1000.0f + " seconds.");
			statusLabel.setText("<html><body>Done. World generation took: " + (finishTime-startTime)/1000.0f + " seconds.<br>Loading...</body></html>");

			break;
		case MULTIPLAYER:
			world = new World(location, this, game, false, statusLabel);
			if(!world.success.equals("OK")) {
				throw new Exception(world.success);
			}
			break;
		case EXISTING:
			Main.log("Loading world "+location+"...");
			statusLabel.setText("Loading world "+location+"...");
			world = new World(location, this, game, true, statusLabel);
			if(!world.success.equals("OK")) {
				error = (world.success);
				return;
			}
			String str ="Map loading done: " + world.getSize() + " block loaded."; 
			Main.log(str);
			statusLabel.setText(str);
		}
		
		
				
	}



	
	private void initTimers() {
		
		ActionListener tickPerformer = new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent evt) {
        		

            	if(server != null || client==null) {
            		
            		ArrayList<Point3D> current = new ArrayList<>(TickableBlocks);
					TickableBlocks.clear();
					for(Point3D p : current) {
        				

						Block bl = world.getBlockAtP(p);
						if(bl != Block.NOTHING) {
							
							if(Tick % bl.refreshRate == 0){
								if(bl instanceof TickListener) {
									TickListener b =(TickListener)bl;
									
									if(b.tick(Tick)) {
										
										
										//TickableBlocks.remove(p);
										TickableBlocks.add(p);
									}
								
								}else {
									Main.err("Attempted to tick non-tickable block:"+bl);
									//TickableBlocks.remove(p);
								}
							}else {
								TickableBlocks.add(p);
							}
						}else if(Main.devmode){
							// Air block ticked
							Main.err("Attempted to tick air block:"+p);

							//TickableBlocks.remove(p);
						}
						
					}
					
					if(world.getAlienCount() < 6 && Math.random() < 0.004 && world.getWhole(true).size()>0) {
						int Min = 0;
						//int Max = world.getWhole(true).size();
						
						//Vector[] arr = world.SpawnableSurface.toArray(new Vector[0]);
						ArrayList<Block> surface = world.getWhole(true);
						Vector pos = null;
						Block b;
						//boolean found = false;
						//do {
							int index = Min + (int)(Math.random() * ((surface.size()-1 - Min) + 1));
							b = surface.get(index);
							
								
								
							for(Entry<Polygon3D,BlockFace> entry : b.HitboxPolygons.entrySet()) {
								if(Main.GAME.PE.VerticalVector.z == 1 && entry.getValue() == BlockFace.TOP) {
									if(entry.getKey().getLight()<3 && entry.getKey().adjecentFilter) {
										pos=entry.getKey().centroid;
										//found=true;
										
									}
									break;
								}else if(Main.GAME.PE.VerticalVector.z == -1 && entry.getValue() == BlockFace.BOTTOM) {
									if(entry.getKey().getLight()<3 && entry.getKey().adjecentFilter) {
										pos=entry.getKey().centroid;
										//found=true;
										
									}
									break;
									
								}
			
							}
								
								
								
								
							
									
						if(pos!=null) {
							Alien newAlien = new Alien(new Vector().set(Vector.PLAYER).multiply(Math.signum(pos.z-0.5f)).add(pos), new EAngle(40,0),"",10,new Random().nextLong(), GameEngine.this);
							world.addEntity(newAlien, true);
						}
						
					}
            	
				
					if(Tick % (2f/Main.TICKSPEED) == 0) {
						for(Entity entity : world.getAllEntities()) {
							if(entity instanceof Alien) {
								
								Alien alien = ((Alien) entity);
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
								for(Entity data : world.getAllEntities()) {
									if(data instanceof PlayerMP) {
										
										if(data.getPos().distance(alien.getPos())<0.2) {
											world.hurtEntity(data.ID, 3, true);
										}
									}
								}
							}
							
							
						}
					}



            	} //vege a szerveroldali dolgoknak



				if(Tick % Main.ENTITYSYNCRATE == 0) {
					
					if(Main.GAME != null && client != null){

							if( Main.GAME.PE.getPos().distance(previousPos)>0.2f || Math.abs(previousAim.yaw-Main.GAME.PE.ViewAngle.yaw)>20) {
								client.sendPlayerPos();

								previousPos.set(Main.GAME.PE.getPos());
								previousAim.set(Main.GAME.PE.ViewAngle);
							}
						
					}
					
					if(server != null) {
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

							client.sendData(data.toString());
						}
					}

				}
				Tick++;


            }
        };

        ticker = new Timer((int)(1000*Main.TICKSPEED) , tickPerformer);

        //physics = new Timer((int) (1000f/Main.PHYSICS_FPS) , serverMain);

        ActionListener physicsPerformer = new ActionListener() {


    		@Override
    		public void actionPerformed(ActionEvent event)

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
    					if(entity instanceof Alien) {
    						Alien alien = (Alien)entity;
    						Vector closest = null;
    						
    						
    						Vector alienPos = alien.getPos();
    						

    						
    						float dst=Float.MAX_VALUE;
							
							for(Entity e : world.Entities.values()) {
								if(e instanceof PlayerMP) {
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
    							
    							//long time=System.currentTimeMillis();
    							
    							//if(client == null || ((client != null && server != null)&& time-lastupdate>Main.TICKSPEED*1000*Main.ENTITYSYNCRATE) ) {
    								if(!world.walk(aim, 2, alien, physicsFPS, false)){ // false mert a sync mashol van
    									alien.jump();
    								}
    							/*	lastupdate=time;
    							}else if(client != null && server != null) {
    								
    								if(!world.walk(aim, 2, alien, physicsFPS, false)){
    									alien.jump();
    								}
    								//lastupdate=time;
    							}
    							*/
    							alien.ViewAngle.yaw=(float) Math.atan2(aim.x, aim.y);
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
    					
    							doGravity(entity, world, physicsFPS);
    							entity.update();
    						
    						}
    					}
    				
    				
    				}); // minden entity kód vége
            	
    			}


            
        };
    	
    	
        
        physics = new Timer(1000/physicsFPS, physicsPerformer);


        
	}

	static void doGravity(Entity entity, World world, int physicsFPS) {//, Point3D feetPoint, Point3D tmpPoint,TreeSet<Point3D> playerColumn) {
		Vector entityPos = entity.getPos();
		Vector VerticalVector = entity.VerticalVector;
		//Point3D feetPoint=entity.feetPoint;
		Point3D tmpPoint=entity.tmpPoint;
		//TreeSet<Point3D> playerColumn = entity.playerColumn;

		tmpPoint.set(entityPos.x, entityPos.y, entityPos.z - ((1.7f + World.GravityAcceleration / physicsFPS) * VerticalVector.z));
		if (!world.getBlockAtP(tmpPoint).solid)
		{
			entity.GravityVelocity -= World.GravityAcceleration / physicsFPS;
		}
	
		float resultant = (entity.JumpVelocity + entity.GravityVelocity);
		if (Math.abs(entity.JumpVelocity) + Math.abs(entity.GravityVelocity) != 0f)
		{
			float JumpDistance = resultant / physicsFPS * VerticalVector.z;
			//Block tmpNothing = new Nothing();
			if (resultant < 0)
			{// lefelé esik önmagához képest
				Block under = world.getBlockUnderEntity(false, true, entity);//, feetPoint, tmpPoint, playerColumn);// world.getBlockAtF(entity.ViewFrom.x,
																			// entity.ViewFrom.y, entity.ViewFrom.z+JumpDistance);
				if (VerticalVector.z == 1)
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
				Block above = world.getBlockUnderEntity(false, false, entity);//,feetPoint, tmpPoint,  playerColumn);// world.getBlockAtF(entity.ViewFrom.x,
																				// entity.ViewFrom.y,
																				// entity.ViewFrom.z+JumpDistance);
	
				if (VerticalVector.z == 1)
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
			entity.JumpVelocity = Math.max(0, entity.JumpVelocity - World.GravityAcceleration / physicsFPS);
		}
	}
	
   
	
	private void generateTerrain(long seed, JLabel statusLabel) {
		
		world.seed = seed;

		
		Random RandomGen = new Random(seed);
		Main.log("Seed:" + seed);
		

		
		
		int sealevel = 0;//world.CHUNK_HEIGHT/2;
		
		
		
		Main.log("- Adding pond...");
		statusLabel.setText("- Adding pond...");
		int top = world.getTop(0, 0);// == 0 ? world.CHUNK_HEIGHT/2 : world.getTop(0, 0).z;

		for(int i = -1; i <= 1; i++)
			for(int j = -1; j <= 1; j++)
				
				world.addBlockNoReplace(new WaterBlock(i, j, top, this), false);
		
		
		
		Main.log("- Creating earth above sea level...");
		statusLabel.setText("- Creating earth above sea level...");
		System.gc();

		int hillCount = RandomGen.nextInt(8)+8;
		Point3D tmpPoint=new Point3D();
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
						tmpPoint.set(offsetX, offsetY, j);
						if(world.getBlockAtP(tmpPoint) == Block.NOTHING) {
							world.addBlockNoReplace(new StoneBlock(offsetX,offsetY,j,this), false);
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
						tmpPoint.set(offsetX, offsetY, j);
						if(world.getBlockAtP(tmpPoint) == Block.NOTHING) {
							world.addBlockNoReplace(new StoneBlock(offsetX,offsetY,j,this), false);
						}
					}
				}
			}
		}
		System.gc();

		
		Main.log("- Sprinkling sand and grass...");
		
		for(Block b : world.getWhole(false)){
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
		//world.addBlocks(lunarModule, true);
		for(int i=0;i<24;i++) {
			Block replace = world.getBlockAtP(lunarModule[i].pos);
			if(replace != Block.NOTHING) {
				world.destroyBlock(replace, false);
			}
			world.addBlockNoReplace(lunarModule[i], true);
		}
		
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
	
	public Block createBlockByClass(String className, int x, int y, int z) {
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
	
	public static Block nullBlock(String className) {
		return createBlock(className, 0, 0, 0, null);
	}
	

	public void startPhysics() {
		physics2=System.currentTimeMillis();
		physics.start();
	}
	
	public void stopPhysics() {
		physics.stop();
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
