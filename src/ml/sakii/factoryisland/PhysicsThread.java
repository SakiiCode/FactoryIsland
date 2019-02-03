package ml.sakii.factoryisland;

import ml.sakii.factoryisland.blocks.Block;
import ml.sakii.factoryisland.entities.Alien;
import ml.sakii.factoryisland.entities.Entity;
import ml.sakii.factoryisland.entities.PlayerEntity;
import ml.sakii.factoryisland.net.PlayerMPData;

public class PhysicsThread extends Thread
{

	
	float FPS;
	private long previousTime, currentTime;
	private boolean running=true;
	private GameEngine engine;
	private int targetFPS=60;

	
	public PhysicsThread(GameEngine engine) {
		this.engine=engine;
		setPriority(Thread.MIN_PRIORITY);
	}
	
	
	@Override
	public void run() {
		//running=true;
		while(running) {
		
			   
			previousTime = currentTime;
			currentTime = System.nanoTime();
		
			if (previousTime == 0L)
			{
				FPS = 60;
			} else
			{
				FPS = 1000000000f / (currentTime - previousTime);
			}
			   
			if(FPS>targetFPS) {
				double deltaTime=1d/FPS;
				double targetTime=1d/targetFPS;
				try
				{
					Thread.sleep((long) ((targetTime-deltaTime)*1000));
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				FPS=10;
			}
			   
			   
			   
			for(Entity entity : engine.world.getAllEntities()) {
				if(entity instanceof Alien && entity.VerticalVector.equals(Main.GAME.PE.VerticalVector)) {
					Alien alien = (Alien)entity;
					Vector closest = null;
					
					if(engine.server==null) {
						if(Main.GAME.PE.getPos().distance(alien.getPos())<10)
							closest = Main.GAME.PE.getPos();
					}else {
						float dst=Float.MAX_VALUE;
						
						for(PlayerMPData player : engine.server.clients.values()) {
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
					
					if(!Game.move(aim, 2, alien, FPS, engine.world)){
						alien.jump();
					}
					
					alien.ViewAngle.yaw=(float) Math.atan2(aim.x, aim.y);
					alien.update();
				}
				
			}
			
			
			if (engine.world.getBlockUnderEntity(false, true, entity) == Block.NOTHING)
			{
				entity.fly(true);
			} else
			{
				entity.fly(false);
			}
			if (!entity.flying && !(entity instanceof PlayerEntity))
			{
			
					
				
				//float FPS=Main.PHYSICS_FPS;
				Vector VerticalVector = entity.VerticalVector;
				if (!engine.world.getBlockAtF(entity.getPos().x, entity.getPos().y,
						entity.getPos().z - ((1.7f + World.GravityAcceleration / FPS) * VerticalVector.z)).solid)
				{
					entity.GravityVelocity -= World.GravityAcceleration / FPS;
				}
			
				float resultant = (entity.JumpVelocity + entity.GravityVelocity);
				if (Math.abs(entity.JumpVelocity) + Math.abs(entity.GravityVelocity) != 0f)
				{
					float JumpDistance = resultant / FPS * VerticalVector.z;
					if (resultant < 0)
					{// lefelé esik önmagához képest
						Block under = engine.world.getBlockUnderEntity(false, true, entity);// world.getBlockAtF(entity.ViewFrom.x,
																					// entity.ViewFrom.y, entity.ViewFrom.z+JumpDistance);
						if (VerticalVector.z == 1)
						{
			
							if (under != Block.NOTHING && under.z + 1 >= entity.getPos().z - 1.7f + JumpDistance)
							{ // beleesne egy blokkba felülrõl
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
					{ // felfelé ugrik önmagához képest
						Block above = engine.world.getBlockUnderEntity(false, false, entity);// world.getBlockAtF(entity.ViewFrom.x,
																						// entity.ViewFrom.y,
																						// entity.ViewFrom.z+JumpDistance);
			
						if (VerticalVector.z == 1)
						{
							if (above != Block.NOTHING && above.z <= entity.getPos().z + JumpDistance)
							{ // belefejelne egy blokkba alulról
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
						entity.JumpVelocity = Math.max(0, entity.JumpVelocity - World.GravityAcceleration / FPS);
					}
				}
			}
			
			
		

		}

		
	}
	    
	    public void kill() {
	    	running =false;
	    }
	
}
