package ml.sakii.factoryisland;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.RescaleOp;
import java.awt.image.VolatileImage;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Consumer;

import javax.swing.JPanel;

import ml.sakii.factoryisland.api.API;
import ml.sakii.factoryisland.blocks.Block;
import ml.sakii.factoryisland.blocks.BlockFace;
import ml.sakii.factoryisland.blocks.BlockInventoryInterface;
import ml.sakii.factoryisland.blocks.BreakListener;
import ml.sakii.factoryisland.blocks.InteractListener;
import ml.sakii.factoryisland.blocks.LoadListener;
import ml.sakii.factoryisland.blocks.PlaceListener;
import ml.sakii.factoryisland.blocks.SignalConsumer;
import ml.sakii.factoryisland.blocks.SignalPropagator;
import ml.sakii.factoryisland.blocks.TextureListener;
import ml.sakii.factoryisland.blocks.WaterBlock;
import ml.sakii.factoryisland.blocks.components.BreakComponent;
import ml.sakii.factoryisland.blocks.components.TickUpdateComponent;
import ml.sakii.factoryisland.entities.Entity;
import ml.sakii.factoryisland.entities.PlayerMP;
import ml.sakii.factoryisland.items.ItemStack;
import ml.sakii.factoryisland.items.ItemType;
import ml.sakii.factoryisland.items.PlayerInventory;

public class Game extends JPanel implements KeyListener, MouseListener, MouseWheelListener {
	static final long serialVersionUID = 2515747642091598425L;

	// ENGINE
	GameEngine Engine;
	public PlayerMP PE;
	public final ArrayList<Object3D> Objects = new ArrayList<>();
	ArrayList<TextureListener> TextureBlocks = new ArrayList<>();
	final ArrayList<Sphere3D> Spheres = new ArrayList<>();
	
	
	// RENDERING
	final Vector BottomViewVector = new Vector(), TopViewVector = new Vector(), RightViewVector = new Vector(),
			LeftViewVector = new Vector();
	private final Vector FrontViewVector = new Vector();
	private final Vector BackViewVector = new Vector();
	private RenderThread renderThread;

	Frustum ViewFrustum;
	Vector ViewVector = new Vector();
	
	float ratio;
	int margin;
	
	private boolean showHUD=true;
	private boolean F3 = false;
	private float FPS = 30f;
	private LinkedList<String> debugInfo = new LinkedList<>();
	private long previousTime, currentTime;


	VolatileImage VolatileFrameBuffer;
	BufferedImage FrameBuffer;
	BufferedImage prevFrame;
	private BufferedImageOp op;

	
	private BlockFace SelectedFace;
	private Polygon3D SelectedPolygon;
	private Block SelectedBlock = Block.NOTHING;
	private Entity SelectedEntity;
	private Block ViewBlock;
	private ArrayList<Sphere3D> ViewSpheres;

	int VisibleCount;
	HashSet<Point3D> dirtyLights = new HashSet<>();
	private Point2D.Double centroid2D = new Point2D.Double();

	
	private Renderer renderer;

	// CONTROLS
	public boolean moved;
	boolean[] key = new boolean[20];
	boolean[] mouse = new boolean[3];
	boolean locked = false;
	boolean centered;
	
	float centerX, centerY;
	private float McenterX, McenterY;
	private float difX, difY;
	private float prevX, prevY;
	
	private final Cursor invisibleCursor = Toolkit.getDefaultToolkit()
			.createCustomCursor(new BufferedImage(1, 1, Transparency.TRANSLUCENT), new Point(0, 0), "InvisibleCursor");
	private float measurement;
	private boolean firstframe = true;
	
	private Vector previousPos;
	private EAngle previousAim;
	private Robot rob;
	
	private float speed = Globals.WALK_SPEED;
	private boolean customfly=false;


	// INVENTORY
	public BlockInventoryInterface remoteInventory;
	private boolean localInvActive = true;
	

	// OTHER
	String error;
	private Vector tmp = new Vector();
	private boolean benchmarkMode = false;
	private GUIManager guiManager;
	private long tickCounter=0;
	private float rotationTarget=0;
	//private float rotationTargetPitch=0;
	private float rotationPhase=0;
	
	
	public Game(String location, long seed, LoadMethod loadmethod, WorldType type, int size, GUIManager guiManager, Consumer<String> update) {
		this.setBackground(new Color(20,20,20)); // csak igy lehet a feher villanast elkerulni valtaskor cardlayout miatt
		this.guiManager=guiManager;
		try {

			Engine = new GameEngine(location, this, seed, loadmethod, type, size, update);
			
			API.Engine=Engine;
			ml.sakii.factoryisland.api.Block.Engine=Engine;

			if(!Engine.error.equals("OK")) {
				System.gc();
				
				error=Engine.error;
				return;
			}
		}catch(Exception e) {
			System.gc();
			e.printStackTrace();
			error=e.getMessage();
			return;
		} catch (OutOfMemoryError e) {
			System.gc();
			e.printStackTrace();
			error="Out of memory! Use the start.bat file to launch the game!";
			return;
		
		}

		PE=new PlayerMP(Config.username,new Vector(19.5f, 19.5f, 15.0f), -135,0,20, new PlayerInventory(Config.username,Engine),null,0, Engine); // alap playermp
		Engine.world.addEntity(PE, false);

		switch(loadmethod) {
		case MULTIPLAYER:


			String[] addr = location.split(":");
			int port = Globals.DEFAULT_PORT;
			if (addr.length != 1)
			{
				port = Integer.parseInt(addr[1]);
			}
			error = Engine.startClient(addr[0], port, this);
			break;
		case BENCHMARK:
			benchmarkMode=true;
			teleportToSpawn();
			break;
		case EXISTING:
			File playerFile = new File("saves/" + location + "/" + Config.username + ".xml");
			if (playerFile.exists())
			{
				Engine.world.parsePE(Config.username, PE);
				
				
				
				break;
			}

			//$FALL-THROUGH$
		case GENERATE:
			teleportToSpawn();
		}
		
		update.accept("Loading player inventory...");
		if(Config.creative) {
			PE.inventory=PlayerInventory.Creative;
		}else {
			World.loadInv(PE.name,location, PE.inventory);
		}
		
		init();
		
		previousPos = new Vector().set(PE.getPos());
		previousAim = new EAngle(PE.ViewAngle.yaw, PE.ViewAngle.pitch);
		
		updateSkyLight();
		
		for(Block b : Engine.world.getWhole()) {
			if(b instanceof LoadListener ll) {
				ll.onLoad(this);
			}
			update.accept("Spreading light...");
			if(b.getLightLevel()>1) {
				Engine.world.addLight(b.pos);
			}
		}
		
		if(PE.inventory.items.size()>0) {
			PE.inventory.setHotbarIndex(0);
		}
		
		
		addKeyListener(this);
		addMouseListener(this);
		addMouseWheelListener(this);
		addComponentListener(new ComponentAdapter()
		{
			@Override
			public void componentShown(ComponentEvent e)
			{
				Game.this.requestFocusInWindow();
			}
		});
		
	}
	

	private void init()
	{

		setCursor(invisibleCursor);

		McenterX = guiManager.getX() + Main.Width / 2;
		McenterY = guiManager.getY() + Main.Height / 2;

		ViewVector.set(PE.ViewAngle.toVector());

		RightViewVector.set(ViewVector).CrossProduct(PE.VerticalVector);
		TopViewVector.set(RightViewVector).CrossProduct(ViewVector);
		LeftViewVector.set(RightViewVector).multiply(-1);
		BottomViewVector.set(TopViewVector).multiply(-1);

		ViewFrustum = new Frustum(this);
		
		ViewSpheres=new ArrayList<>();
		
		renderer = new Renderer(this);
		resizeScreen(Config.getWidth(), Config.getHeight());


	    op = new RescaleOp(
	            new float[]{0.2f, 0.2f, 0.2f, 1f}, // scale factors for red, green, blue, alpha
	            new float[]{0, 0, 0, 0}, // offsets for red, green, blue, alpha
	            null);
		
		try
		{
			rob = new Robot();
		} catch (AWTException e)
		{
			e.printStackTrace();
		}
		
		
		//sphere = new Sphere3D(0,0,0,10,new Color4(60f/255f,19f/255f,97f/255f,0.3f));
		/*SphereWeird3D sphere1 = new SphereWeird3D(0,0,0,10,new Color4(1,0,1,0.3f));
		Objects.add(sphere1);
		Spheres.add(sphere1);*/
		/*Sphere3D sphere2 = new Sphere3D(10,10,10,3,new Color4(0,1,0,0.3f));
		Objects.add(sphere2);
		Spheres.add(sphere2);*/
		//Sphere3D Sphere3 = new Sphere3D("Gömb", new Vector(0,0,0), 10, 50, new Surface(new Color4(1f,0f,1f,0.5f)), Engine);
		//Spheres.add(Sphere3);
		//Objects.addAll(Sphere3.Polygons);
		renderThread = new RenderThread(this, guiManager);

	}
	


	void render(Graphics g) {
		

			
			previousTime = currentTime;
			currentTime = System.nanoTime();

			if (previousTime == 0L)
			{
				FPS = 60;
			} else
			{
				FPS = 1000000000f / (currentTime - previousTime);
			}
			if(!firstframe) {
				CalcAverageTick((currentTime - previousTime)/1000L);
			}

			if (guiManager.isActive())
			{
				
				if(centered) {
					Point arg0 = MouseInfo.getPointerInfo().getLocation();
					difX=prevX-arg0.x;
					difY=prevY-arg0.y;
					if(Math.abs(arg0.x-McenterX)>Main.Width/5 || Math.abs(arg0.y-McenterY)>Main.Height/5) {
						centerMouse();
					}else {
						prevX=arg0.x;
						prevY=arg0.y;
					}
				}else {
					difX=0;
					difY=0;
					centerMouse();
				}
			}else if(!Main.nopause){
				Main.log("game not in focus");
				new Thread() {
					@Override
					public void run() {
						pauseTo("pause");
					}
				}.start();
				return;
			}

			
			
			
			Controls();
			
			if(rotationPhase != rotationTarget) {
				rotationPhase += rotationTarget/FPS*4;
				if(Math.abs(rotationPhase) > Math.abs(rotationTarget)) {
					rotationPhase = rotationTarget;
				}
				Vector verticalVector = Vector.Z.cpy();
				Vector sideVector = ViewVector.cpy().CrossProduct(Vector.Z);
				verticalVector.multiply(rotationPhase);
				sideVector.multiply((1-Math.abs(rotationPhase)));
				PE.VerticalVector.set(verticalVector).add(sideVector);
				PE.VerticalVector.normalize();
				Main.log(rotationPhase+","+(1-Math.abs(rotationPhase)));
				
				/*float startPitch = -rotationTargetPitch;
				if(rotationTarget>0) {
					PE.ViewAngle.pitch = -startPitch*rotationPhase;
				}else {
					PE.ViewAngle.pitch = startPitch*rotationPhase;
				}*/
			}
			
			
			Vector PEPos = PE.getPos();
			moved = !previousPos.equals(PEPos) || !previousAim.equals(PE.ViewAngle);
			previousPos.set(PEPos);
			previousAim.pitch = PE.ViewAngle.pitch;
			previousAim.yaw = PE.ViewAngle.yaw;


			PE.ViewAngle.normalize();

			ViewVector.set(PE.ViewAngle.toVector());

			RightViewVector.set(ViewVector).CrossProduct(PE.VerticalVector);
			TopViewVector.set(RightViewVector).CrossProduct(ViewVector);
			LeftViewVector.set(RightViewVector).multiply(-1);
			BottomViewVector.set(TopViewVector).multiply(-1);

			ViewFrustum.update();

			
			
			if(!firstframe) {
				long MAX_TICKS = 1000000000/Globals.TICKSPEED;
				tickCounter += currentTime-previousTime; 
				if(tickCounter>MAX_TICKS) {
					Engine.performTick();
					tickCounter -= MAX_TICKS;
				}
			}
				
			firstframe = false;
			
			if(dirtyLights.size() > 0) {
				//TODO polygonokat tarolni majd a komponensben
				for(Object3D obj : Objects) {
					if(obj instanceof Polygon3D poly) {
						for(Point3D source : dirtyLights) {
							poly.getSources().remove(source);
						}
					}
				}
				for(Point3D p : dirtyLights) {
					Engine.world.reAddLight(p);
				}
				dirtyLights.clear();
				updateSkyLight();
			}

			Engine.performPhysics(FPS);
			
			
			if (!locked && moved)
			{
				for (TextureListener b : TextureBlocks)
				{
					b.updateTexture(tmp, this);
				}
				
				ViewSpheres.clear();
				
				for(Sphere3D s : Spheres) {
					if(s.isPlayerInside(PE)) {
						ViewSpheres.add(s);
					}
				}

			}


			ViewBlock = Engine.world.getBlockAt((int)Math.floor(PEPos.x), (int)Math.floor(PEPos.y), (int)Math.floor(PEPos.z));



			SelectedPolygon = null;
			VisibleCount = 0;

			Graphics fb = g;

			fb.setColor(AssetLibrary.skyColor);
			fb.fillRect(0, 0, Config.getWidth(), Config.getHeight());

			
			fb.setColor(Color.WHITE);
			
			
			if(!Config.useTextures) {
				Spheres.sort((s1,s2)->Float.compare(s2.getCenterDist(),s1.getCenterDist()));
				for(Sphere3D sphere : Spheres) {
					if(sphere.isPlayerInside(PE)){
						fb.setColor(sphere.getColor().getColor());
						fb.fillRect(0, 0, Config.getWidth(), Config.getHeight());
					}
				}
			}
			
			

			SelectedPolygon = renderer.render(g, Objects, Spheres);
			
			
			
			if (SelectedPolygon == null)
			{
				SelectedBlock = Block.NOTHING;
				SelectedFace = BlockFace.NONE;
				SelectedEntity=null;

			} else
			{
				if(SelectedPolygon.model instanceof Block b) {
					SelectedEntity=null;
					SelectedBlock=b;
					SelectedFace=b.HitboxPolygons.get(SelectedPolygon);
					if(showHUD) {
						Polygon3D.renderSelectOutline(fb, SelectedPolygon.polygon, centroid2D);
					}
				
				}else {
					SelectedBlock = Block.NOTHING;
					SelectedFace = BlockFace.NONE;
					SelectedEntity=(Entity) SelectedPolygon.model;
				}
				

			}
			
			
			if (remoteInventory != null)
			{
				g.setColor(new Color(0f,0f,0f,0.5f));
				g.fillRect(0, 0, Config.getWidth(), Config.getHeight());
			}

			if (ViewBlock instanceof WaterBlock)
			{
				g.setColor(((Polygon3D)ViewBlock.Objects.get(0)).s.c.getColor());
				g.fillRect(0, 0, Main.Width, Main.Height);
			}

			if(showHUD) {
				((Graphics2D) g).setPaint(Color4.CROSSHAIR_COLOR);
	
				int cX = (int)centerX;
				int cY = (int)centerY;
				int crosshairSize = Config.getHeight()/50;
				g.drawLine(cX, cY - crosshairSize, cX, cY + crosshairSize);
				g.drawLine(cX - crosshairSize, cY, cX + crosshairSize, cY);
				
				
				int fontSize;
				if(Config.getHeight()<1080) {
					fontSize = (int) Util.interp(0,1080,Config.getHeight(),5,19);
				}else {
					fontSize = (int) Util.interp(0,1080,Config.getHeight(),0,19);
				}
				g.setFont(new Font("SANS", Font.BOLD, fontSize));
				g.setColor(Color.GRAY);
				float viewportscale=(float) Math.sqrt(Config.getWidth()*Config.getHeight()*1f/Main.Width/Main.Height);
	
				if (F3)
				{
	
					debugInfo.clear();
					debugInfo.add("Eye:" + PEPos+", yaw: " + Math.round(PE.ViewAngle.yaw) + ", pitch: " + Math.round(PE.ViewAngle.pitch) +", difX: "+ difX + ", difY: "+ difY);
					debugInfo.add("McenterX:" + McenterX + ", McenterY:" + McenterY);
					debugInfo.add("Health: " + PE.getHealth() +", ID: " + PE.ID);
					debugInfo.add("FPS (smooth): " + (1f/measurement) + " (" + measurement + " ms) - " + FPS);
					debugInfo.add("SelBlock:" + SelectedFace + ", "+SelectedBlock+",meta:"+SelectedBlock.BlockMeta);
					debugInfo.add("SelBlock Components:" + SelectedBlock.Components.toString());
					if(SelectedBlock instanceof SignalPropagator wire) {
						debugInfo.add(wire.powers+"");
					}
					if(SelectedBlock instanceof SignalConsumer consumer) {
						debugInfo.add(consumer.getSignals()+"");
					}
					if(SelectedPolygon != null) {
						debugInfo.add("SelPoly: "+SelectedPolygon);
					}else {
						debugInfo.add("SelPoly: null");
					}
					if(SelectedEntity!=null) {
						debugInfo.add("SelectedEntity: "+SelectedEntity + ", health:" + SelectedEntity.getHealth());
					}else {
						debugInfo.add("SelectedEntity: null");
					}
					if(Config.useTextures) {
						debugInfo.add("Selected Pixel: "+renderer.ZBuffer[FrameBuffer.getWidth()/2-1][FrameBuffer.getHeight()/2-1].toString());
					}
					debugInfo.add("Polygon count: " + VisibleCount + "/" + Objects.size());
					debugInfo.add("testing:"+key[6]);
					debugInfo.add("Filter locked: " + locked + ", moved: " + moved + ", nopause:" + Main.nopause);
					long realTime = Engine.Tick % Globals.TICKS_PER_DAY;
					float timePercent = (realTime*1f/Globals.TICKS_PER_DAY);
					double light = Math.sin(2*Math.PI*timePercent);
					debugInfo.add("Tick: " + Engine.Tick + "(" + Engine.TickableBlocks.size() + "), day:"+Engine.isDay((int)PE.ViewFrom.z)+",light:"+light);
					debugInfo.add("needUpdate:" + !SelectedBlock.getComponent(TickUpdateComponent.class).isEmpty() +", blockLightPass:"+Engine.world.lightCalcRuns);
					debugInfo.add("Blocks: " + Engine.world.getSize() + ", hotbarIndex:"+PE.inventory.getHotbarIndex()+", selected:"+((PE.inventory.getHotbarIndex()>-1 ) ? PE.inventory.getSelectedKind() : ""));
					if (Engine.client != null)
					{
						debugInfo.add("PacketCount: " + Engine.client.packetCount);
						debugInfo.add("BlockCount: " + Engine.client.blockcount);
					}
					debugInfo.add("Seed: " + Engine.world.seed);
					if(SelectedEntity == null) {
						debugInfo.add("GravityVelocity: " + PE.GravityVelocity + ", JumpVelocity: " + PE.JumpVelocity + ", result: "
								+ (PE.JumpVelocity - PE.GravityVelocity));
						
						debugInfo.add("FirstBlockUnder: " + Engine.world.getBlockUnderEntity(false, true, PE)+",VV:"+PE.VerticalVector.z);
					}else {
						debugInfo.add("GravityVelocity: " + SelectedEntity.GravityVelocity + ", JumpVelocity: " + SelectedEntity.JumpVelocity + ", result: "
								+ (SelectedEntity.JumpVelocity - SelectedEntity.GravityVelocity));
						
						debugInfo.add("FirstBlockUnder: " + Engine.world.getBlockUnderEntity(false, true, SelectedEntity)+",VV:"+SelectedEntity.VerticalVector.z);
					}
					debugInfo.add("Entities ("+Engine.world.getAllEntities().size()+"): "+Engine.world.getAllEntities());
					long lastLightUpdateTick = Engine.Tick / (Globals.TICKS_PER_DAY/Globals.LIGHT_UPDATES_PER_DAY) * (Globals.TICKS_PER_DAY/Globals.LIGHT_UPDATES_PER_DAY); 
					debugInfo.add("TimePercent:"+Engine.getTimePercent()+
							", level:"+Polygon3D.testLightLevel(Engine.getTimePercent())+
							", cached:"+Polygon3D.testLightLevel(GameEngine.getTimePercent(lastLightUpdateTick)));
					// DEBUG SZÖVEG
					g.setColor(Color.BLACK);
					g.setFont(new Font(g.getFont().getName(), g.getFont().getStyle(), fontSize));
					
					
					for (int j = 1; j > -1; j--)
					{
	
						Iterator<String> iter = debugInfo.iterator();
						int height = g.getFontMetrics().getHeight();
						int ascent = g.getFontMetrics().getAscent();
						int i = ascent;
						while (iter.hasNext())
						{
							i += (int) (10*viewportscale)+height;
							g.drawString(iter.next(), 20, i + j);
						}
						g.setColor(Color.WHITE);
					}
				} else
				{
					g.drawString("FPS: " + (int) (1000f/measurement), (int)(viewportscale*20), (int)(viewportscale*20));
	
				}
				
				//INVENTORY SOR
				if (PE.inventory.items.size() > 0)
				{
										
					
					// VIEWMODEL
					if (PE.inventory.hasSelected())
					{

						BufferedImage viewmodel = Main.Items.get(PE.inventory.getSelectedKind().name).ViewmodelTexture;
						int x = Config.getWidth() * 2 / 3;
						int y = (int) (Config.getHeight() - viewportscale * viewmodel.getHeight());
						int w = (int) (viewmodel.getWidth() * 2 * viewportscale);
						int h = (int) (viewmodel.getHeight() * 2 * viewportscale);
	
						g.drawImage(viewmodel, x, y, w, h, null);

					}
					
					drawInventory(g, PE.inventory, fontSize, viewportscale, true, null, localInvActive);
					
	
				}
	
				if (remoteInventory != null && remoteInventory.getInv().items.size()>0)
				{
						
					drawInventory(g, remoteInventory.getInv(), fontSize, viewportscale, false, remoteInventory.getBlock(), localInvActive);
	
				}
				
				int icon = (int) (Main.Width * 64f / 1440f*viewportscale);
				int centerY = Config.getHeight()-icon-icon/2;
				int halfBarHeight = icon/6;
				int halfBgHeight = icon/4;
				int halfTextHeight = g.getFontMetrics().getAscent()-g.getFontMetrics().getHeight()/2;
				g.setColor(Color.DARK_GRAY);
				g.fillRoundRect((int) (400*viewportscale), centerY-halfBgHeight, (int) (20*30*viewportscale), halfBgHeight*2,5,5);
				g.setColor(Color.RED);
				g.fillRoundRect((int) (400*viewportscale), centerY-halfBarHeight, (int) (PE.getHealth()*30*viewportscale), halfBarHeight*2, 20, 20);
				g.setColor(Color.BLACK);
				g.drawString(PE.getHealth()+"",(int)(480*viewportscale),centerY+halfTextHeight);
				g.setColor(Color.WHITE);
				g.drawString(PE.getHealth()+"",(int)(480*viewportscale)-1,centerY+halfTextHeight-1);
			} // showHUD

		
		}

	private static void drawInventory(Graphics g, PlayerInventory Inv, int fontSize, float viewportscale, boolean local, Block remoteBlock, boolean localInvActive) {
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, fontSize+2));


		int halfTextHeight=-g.getFontMetrics().getAscent()+g.getFontMetrics().getHeight()/2;
		
		int i = -1;
		int icon = (int) (Main.Width * 64f / 1440f*viewportscale);
		int iconTextOffset = icon / 3;
		int iconY=Config.getHeight() - icon - (local?0:2*icon);
		int textY = iconY- halfTextHeight;
		for(Entry<ItemType, Integer>  stack : Inv.items.entrySet())
		{
			ItemType kind = stack.getKey();
			int amount = stack.getValue();
			i++;
			ItemType item = Main.Items.get(kind.name);
			g.drawImage(item.ItemTexture, i * icon, iconY,icon, icon, null);

			int textX = i * icon + iconTextOffset;

			if (kind != Inv.getSelectedKind() || (local != localInvActive))
			{ //alap
				drawOutlinedString(amount + "", textX, textY, Color.BLACK, Color.WHITE, g);
				

			} else
			{ //kiemelt
				drawOutlinedString(amount + "", textX, textY, Color.WHITE, Color.BLACK, g);
			}
		}
		
		// KIJELÖLT FELIRAT
		if (Inv.hasSelected()) 
		{
			drawOutlinedString(Inv.getSelectedKind().name, (int) (25*viewportscale), Config.getHeight() - icon-icon/2-halfTextHeight, Color.WHITE, Color.BLACK, g);
		
		}
		
		// TÁVOLI BLOKK
		if(!local) {
			
			drawOutlinedString(remoteBlock.toString(), 25, Config.getHeight() - 3*icon-icon/2-halfTextHeight, Color.WHITE, Color.BLACK, g);
			
		}
		
	}

	private static void drawOutlinedString(String text, int x, int y, Color mainColor, Color outlineColor, Graphics g) {
		g.setColor(outlineColor);
		g.drawString(text, x - 1, y - 1);
		g.drawString(text, x - 1, y);
		g.drawString(text, x - 1, y + 1);
		g.drawString(text, x, y - 1);
		g.drawString(text, x, y + 1);
		g.drawString(text, x + 1, y - 1);
		g.drawString(text, x + 1, y);
		g.drawString(text, x + 1, y + 1);

		g.setColor(mainColor);
		g.drawString(text, x, y);
	}
	
	private void Controls()
	{
		BackViewVector.set(ViewVector).CrossProduct(PE.VerticalVector).CrossProduct(PE.VerticalVector);
		FrontViewVector.set(BackViewVector).multiply(-1);
		
		if(key[10] && !benchmarkMode) {

			benchmarkMode=true;
			PE.move(20, 20, 15, true);
			PE.ViewAngle.set(-135, -27);
		}
		
		if(!key[10] && benchmarkMode) {
			benchmarkMode=false;
		}
		
		if(benchmarkMode) {
			Engine.world.walk(LeftViewVector, speed/4, PE, FPS, false);
			PE.ViewAngle.yaw -= 5 / FPS * PE.VerticalVector.z;
			return;
		}

		if (key[0]) // W
		{
			Engine.world.walk(FrontViewVector, speed, PE, FPS, false); // false hogy ne legyen keses, a sync idozitve van
		}

		if (key[1]) // S
		{
			Engine.world.walk(BackViewVector, speed, PE, FPS, false);

		}
		if (key[2]) // A
		{
			Engine.world.walk(LeftViewVector, speed, PE, FPS, false);

		}

		if (key[3]) // D
		{
			Engine.world.walk(RightViewVector, speed, PE, FPS, false);

		}

		if (difX != 0)
		{
			PE.ViewAngle.yaw += difX * Config.sensitivity / 20 * PE.VerticalVector.z;

		}
		if (difY != 0)
		{
			PE.ViewAngle.pitch += difY * Config.sensitivity / 20 * PE.VerticalVector.z;

		}
		Vector PEPos = PE.getPos();
		if (PE.flying)
		{
			if (key[4])
			{
				PEPos.z += speed / FPS * PE.VerticalVector.z;

			}
			if (key[5])
			{
				PEPos.z -= speed / FPS * PE.VerticalVector.z;

			}
		}
		Block invunder = Engine.world.getBlockUnderEntity(true, true, PE);
		if (PE.VerticalVector.z == 1 && PEPos.z < 0	&& invunder != Block.NOTHING)
		{
			startRotation(-1);
			//PE.VerticalVector.z = -1;
			Block above = Engine.world.getBlockUnderEntity(false, true, PE);
			if (PEPos.z >= above.z - 1.7f)
			{
				PEPos.z = above.z - 1.7f;
			}
		} else if (PE.VerticalVector.z == -1 && PEPos.z >= 0 && invunder != Block.NOTHING)
		{
			//PE.VerticalVector.z = 1;
			startRotation(1);

			Block under = Engine.world.getBlockUnderEntity(false, true, PE);
			if (PEPos.z <= under.z + 2.7f)
			{
				PEPos.z = under.z + 2.7f;
			}
		}
		
		if(key[7]) {
			PE.fly(true);
		}else {
			if (Engine.world.getBlockUnderEntity(false, true, PE) == Block.NOTHING)
			{
				PE.fly(true);
			} else
			{
				PE.fly(false);
			}
			
		}

		
		if (!PE.flying) {
			if(key[4]) {
				PE.jump();
			}
			Engine.doGravity(PE, FPS);
		}
		
		if(mouse[0]) {
			if (remoteInventory == null) {
				if (SelectedBlock != Block.NOTHING)
				{
					if(Engine.isSingleplayer()) { //mpben az item visszaadast a gameclient kezeli
					
						ItemStack[] returnAsItem=new ItemStack[] {new ItemStack(Main.Items.get(SelectedBlock.name),1)};
						
						
						
						Optional<BreakComponent> bc = SelectedBlock.getComponent(BreakComponent.class);
						if(!bc.isEmpty()) {
							bc.get().onBreak();
						}
							
						PE.inventory.add( returnAsItem, true);
						
						
						//PowerGenerator miatt utana kell torolni
						Engine.world.destroyBlock(SelectedBlock, true);

	
					}else{
						Engine.world.destroyBlock(SelectedBlock, true);
					}
				}else if(SelectedEntity != null) {
					Engine.world.hurtEntity(SelectedEntity.ID, 1, true);

					
				}
			}
			mouse[0]=false;
		}
		
		if(mouse[1]) {
			if (localInvActive)
			{
				if(PE.inventory.hasSelected())
					SwapItems(false, PE.inventory.getSelectedKind().name);
			} else
			{
				SwapItems(true, remoteInventory.getInv().getSelectedKind().name);
			}
			mouse[1]=false;
		}
		
		
		if(mouse[2]) {
			if (remoteInventory == null) {
				if (SelectedBlock != Block.NOTHING)
				{
					if (!key[5])
					{
						if (PE.inventory.hasSelected())
						{
							ItemType selected = PE.inventory.getSelectedKind();
							if (placeBlock(selected.className))
							{
								PE.inventory.add(selected, -1, true);
							}
						}
					} else if (SelectedBlock instanceof InteractListener il)
					{
						il.interact(SelectedFace);
						
						if (SelectedBlock instanceof BlockInventoryInterface bii)
						{
							PlayerInventory otherInv = bii.getInv();
							if (PE.inventory.items.size() == 0 && otherInv.items.size() > 0)
							{
								SwitchInventory(false);
							}
						}
					}
				}
			}
			mouse[2]=false;
		}
		
		

	}

	@Override
	public void keyPressed(KeyEvent arg0)
	{
		if (arg0.getKeyCode() == KeyEvent.VK_W)
		{
			key[0] = true;
			if (remoteInventory != null)
				openInventory(null);
		}
		if (arg0.getKeyCode() == KeyEvent.VK_S)
		{
			key[1] = true;
			if (remoteInventory != null)
				openInventory(null);
		}
		if (arg0.getKeyCode() == KeyEvent.VK_A)
		{
			key[2] = true;
			if (remoteInventory != null)
				openInventory(null);
		}
		if (arg0.getKeyCode() == KeyEvent.VK_D)
		{
			key[3] = true;
			if (remoteInventory != null)
				openInventory(null);
		}
		if (arg0.getKeyCode() == KeyEvent.VK_SPACE)
		{
			key[4] = true;
			if (remoteInventory != null)
				openInventory(null);
		}
		if (arg0.getKeyCode() == KeyEvent.VK_SHIFT)
		{
			key[5] = true;
		}
		if (arg0.getKeyCode() == KeyEvent.VK_F)
		{
			if(F3) {
				locked = !locked;
			}
		}
		if (arg0.getKeyCode() == KeyEvent.VK_F3)
		{
			F3 = !F3;
		}

		if (arg0.getKeyCode() == KeyEvent.VK_ESCAPE)
		{
			pauseTo("pause");
		}

		if (arg0.getKeyCode() == KeyEvent.VK_T) //TESZT: deptmap-nál ne mutassa a kereteket
		{
			key[6] = true;
		}
		
		if (arg0.getKeyCode() == KeyEvent.VK_Z) //TESZT: gömb határok
		{
			key[8] = true;
		}
		
		if (arg0.getKeyCode() == KeyEvent.VK_U) //TESZT: xmax-xmin fps
		{
			key[9] = true;
		}
		if (arg0.getKeyCode() == KeyEvent.VK_B)
		{
			key[10] = true;
		}
		if (arg0.getKeyCode() == KeyEvent.VK_CONTROL)
		{
			key[7] = !key[7];
			customfly = key[7];
			if (customfly)
			{
				speed = Globals.FLY_SPEED;
				
			} else
			{
				speed = Globals.WALK_SPEED;
			}
		}


		if (arg0.getKeyCode() == KeyEvent.VK_Q)
		{
			SwapInv();
		}
		if (arg0.getKeyCode() == KeyEvent.VK_P)
		{
			Main.nopause=!Main.nopause;
		}
		

	}

	@Override
	public void keyReleased(KeyEvent arg0)
	{
		if (arg0.getKeyCode() == KeyEvent.VK_W)
		{
			key[0] = false;
		}
		if (arg0.getKeyCode() == KeyEvent.VK_S)
		{
			key[1] = false;
		}
		if (arg0.getKeyCode() == KeyEvent.VK_A)
		{
			key[2] = false;
		}
		if (arg0.getKeyCode() == KeyEvent.VK_D)
		{
			key[3] = false;
		}
		if (arg0.getKeyCode() == KeyEvent.VK_SPACE)
		{
			key[4] = false;
		}
		if (arg0.getKeyCode() == KeyEvent.VK_SHIFT)
		{
			key[5] = false;
		}
		if (arg0.getKeyCode() == KeyEvent.VK_T)
		{
			key[6] = false;
		}
		if (arg0.getKeyCode() == KeyEvent.VK_Z)
		{
			key[8] = false;
		}
		if (arg0.getKeyCode() == KeyEvent.VK_U) //TESZT: xmax-xmin fps
		{
			key[9] = false;
		}
		if (arg0.getKeyCode() == KeyEvent.VK_B)
		{
			key[10] = false;
		}
		if (arg0.getKeyCode() == KeyEvent.VK_F2)
		{
			renderThread.screenshot=true;
		}
		
		if (arg0.getKeyCode() == KeyEvent.VK_F1)
		{
			showHUD = !showHUD;
		}

	}

	@Override
	public void mouseExited(MouseEvent arg0)
	{

	}

	@Override
	public void mousePressed(MouseEvent arg0)
	{
		if(arg0.getButton() == MouseEvent.BUTTON1) {
			mouse[0] = true;
		}
		
		if(arg0.getButton() == MouseEvent.BUTTON2) {
			mouse[1] = true;
		}
		
		if(arg0.getButton() == MouseEvent.BUTTON3) {
			mouse[2] = true;
		}

	}
	
	public void updateSkyLight() {
		for(Object3D o : Objects) {
			if(o instanceof Polygon3D p) {
				p.recalcLightedColor();
			}
		}
	}

	
	public Point convert3Dto2D(Vector input, Point output)
	{
		int zoom = Config.getZoom();
		Vector PEPos = PE.getPos();
		
		float diffX = input.x-PEPos.x;
		float diffY = input.y-PEPos.y;
		float diffZ = input.z-PEPos.z;
		
		float dot =  diffX * ViewVector.x + diffY * ViewVector.y + diffZ * ViewVector.z;
		float sumX = (diffX * RightViewVector.x + diffY * RightViewVector.y + diffZ * RightViewVector.z) / dot;
		float x = sumX * zoom + centerX;
		float sumY = (diffX * BottomViewVector.x + diffY * BottomViewVector.y + diffZ*BottomViewVector.z)/dot;
		float y = sumY * zoom + centerY;
		output.setLocation((int)x, (int)y);
		
		return output;

	}
	
	public Point[] convert3Dto2D(Vector[] input, Point[] output, int size)
	{
		int zoom = Config.getZoom();
		Vector PEPos = PE.getPos();
		
		for(int i=0;i<size;i++) {
			float diffX = input[i].x-PEPos.x;
			float diffY = input[i].y-PEPos.y;
			float diffZ = input[i].z-PEPos.z;
			
			float dot =  diffX * ViewVector.x + diffY * ViewVector.y + diffZ * ViewVector.z;
			float sumX = (diffX * RightViewVector.x + diffY * RightViewVector.y + diffZ * RightViewVector.z) / dot;
			float x = sumX * zoom + centerX;
			float sumY = (diffX * BottomViewVector.x + diffY * BottomViewVector.y + diffZ*BottomViewVector.z)/dot;
			float y = sumY * zoom + centerY;
			output[i].setLocation((int)x, (int)y);
		}
		
		return output;

	}
	
	public Point2D.Float[] convert3Dto2D(Vector[] input, Point2D.Float[] output, int size)
	{
		int zoom = Config.getZoom();
		Vector PEPos = PE.getPos();
		
		for(int i=0;i<size;i++) {
			float diffX = input[i].x-PEPos.x;
			float diffY = input[i].y-PEPos.y;
			float diffZ = input[i].z-PEPos.z;
			
			float dot =  diffX * ViewVector.x + diffY * ViewVector.y + diffZ * ViewVector.z;
			float sumX = (diffX * RightViewVector.x + diffY * RightViewVector.y + diffZ * RightViewVector.z) / dot;
			float x = sumX * zoom + centerX;
			float sumY = (diffX * BottomViewVector.x + diffY * BottomViewVector.y + diffZ*BottomViewVector.z)/dot;
			float y = sumY * zoom + centerY;
			output[i].setLocation(x, y);
		}
		
		return output;

	}

	@Override
	public void mouseReleased(MouseEvent arg0)
	{

	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent arg0)
	{
		if (arg0.getWheelRotation() > 0)
		{ // UP/AWAY
			if (localInvActive)
			{
				PE.inventory.wheelUp();
			} else
			{
				remoteInventory.getInv().wheelUp();
			}

		}

		if (arg0.getWheelRotation() < 0)
		{ // DOWN/TOWARDS
			if (localInvActive)
			{
				PE.inventory.wheelDown();
			} else
			{
				remoteInventory.getInv().wheelDown();
			}
		}

	}

	public void openInventory(BlockInventoryInterface inv)
	{
		remoteInventory = inv;
		if (remoteInventory != null)
		{
			remoteInventory.getInv().setHotbarIndex(-1);
		} else //kilépés
		{
			SwitchInventory(true);
		}
	}


	void start() {
		/*Engine.ticker.start();
		if(Engine.isSingleplayer())
			Engine.startPhysics();*/




		if(PE.getHealth()==0) {
			Engine.world.hurtEntity(PE.ID, 0, false);
		}


		centerMouse();
		renderThread.start();


	}

	public void disconnect(String error)
	{
		renderThread.kill();
		Engine.disconnect(error);
		Objects.clear();
		guiManager.closeGame();
		System.gc();
	}


	
	private void pauseTo(String UiName) {
		renderThread.kill();

		if(Config.renderMethod == RenderMethod.DIRECT) {
			AssetLibrary.FreezeBG = AssetLibrary.StandardBG;
		}else if(Config.renderMethod == RenderMethod.VOLATILE){
			BufferedImage tmp = new BufferedImage(VolatileFrameBuffer.getWidth(),VolatileFrameBuffer.getHeight(),BufferedImage.TYPE_INT_ARGB);
			tmp.getGraphics().drawImage(VolatileFrameBuffer, 0,0,tmp.getWidth(), tmp.getHeight(), null);
			AssetLibrary.FreezeBG = op.filter(tmp, null);
		}else {
			AssetLibrary.FreezeBG = op.filter(FrameBuffer, null);
		}
		if (Engine.isSingleplayer()) { // ezek multiplayerben nem allhatnak le
			//Engine.ticker.stop();
			//Engine.stopPhysics();//tavoli mpnel nem is futott ugyhogy mindegy
		}
		

		centered = false;
		setCursor(Cursor.getDefaultCursor());
		removeKeyListener(this);
		removeMouseListener(this);
		removeMouseWheelListener(this);
		for (int i = 0; i < key.length; i++)
		{
			if(i!=7)
				key[i] = false;
		}

		guiManager.SwitchWindow(UiName);
	}
	
	void notifyDeath() {
		new Thread() {
			@Override
			public void run() {
				if(renderThread.isAlive()) {
					pauseTo("died");
				}
			}
		}.start();

	}
	
	public void respawn() {
		PE.getPos().set(Engine.world.getSpawnBlock().pos).add(new Vector(0,0,2.7f));
		PE.setHealth(PE.maxHealth);
		Engine.world.addEntity(PE, true);
	}

	public void resume()
	{
		if(PE.getHealth()==0) {
			guiManager.SwitchWindow("died");
			return;
		}
		setCursor(invisibleCursor);
		/*if(!Engine.ticker.isRunning()) {
			Engine.ticker.start();
		}
		if(!Engine.isPhysicsRunning() && (Engine.isSingleplayer() || Engine.isLocalMP())) {
			Engine.startPhysics();
		}*/
		firstframe = true;
		renderThread = new RenderThread(this, guiManager);
		
		
		addKeyListener(this);
		addMouseListener(this);
		addMouseWheelListener(this);
		currentTime=System.nanoTime();
		renderThread.start();
		centerMouse();
	}

	private void centerMouse() {
		try
		{
			rob.mouseMove((int) McenterX, (int) McenterY);
			prevX=McenterX;
			prevY=McenterY;
			centered = true;
		} catch (Exception e)
		{
			Main.log("Could not center mouse:" + e.getMessage());
		}
	}

	public void resizeScreen(int w, int h)
	{

		FrameBuffer = Main.toCompatibleImage(new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB));
		GraphicsConfiguration config = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[Main.screen].getDefaultConfiguration();
		VolatileFrameBuffer = config.createCompatibleVolatileImage(w, h);
		renderer.resizeScreen(w,h);
		centerX = w / 2;
		centerY = h / 2;
		Main.log("FOV:"+Config.FOV+",Zoom:"+Config.FOVToZoom(Config.FOV));

		ViewFrustum.setRatios(centerX / Config.getZoom(), centerY / Config.getZoom());

		ratio = (w * 1f / Main.Width
				+ h * 1f / Main.Height) / 2;
		margin = (int) (5 * ratio);

	}
	
	private void startRotation(float target) {
		rotationTarget=target;
		rotationPhase=PE.VerticalVector.z;
		//rotationTargetPitch = -PE.ViewAngle.pitch;
	}

	private void CalcAverageTick(long frameTimeUs)
	{
		double frameTimeMs = frameTimeUs/1000.0;
		double smoothing = Math.pow(0.95, frameTimeMs * 60.0 / 1000.0);
		measurement = (float)((measurement * smoothing) + (frameTimeMs * (1.0-smoothing)));
		//Main.log("frameTime: "+frameTimeMs+", smoothing: "+smoothing+", measurement: "+measurement);
	}
	
	boolean insideBlock(Polygon3D polygon) {
		return ViewBlock == polygon.model;
	}
	
	
	//TODO számontartani egy ViewSphere-t és nem végigmenni az összesen, hanem leellenőrizni hogy a polygon hozzá tartozik-e
	@SuppressWarnings("unlikely-arg-type")
	boolean insideSphere(Polygon3D polygon) {
		for(int i=0;i<ViewSpheres.size();i++) {
			if(ViewSpheres.get(i).Polygons.contains(polygon)) {
				return true;
			}
		}
		return false;
	}

	private void SwitchInventory(boolean local)
	{
		if (!local && remoteInventory != null)
		{ // tavolira valtas

			PE.inventory.setHotbarIndex(-1);

			if (remoteInventory.getInv().items.size() > 0)
			{
				remoteInventory.getInv().setHotbarIndex(0);
			}

			// }
			localInvActive = false;
		} else if(local)
		{ //helyire valtas
			
			
			if (remoteInventory != null)
			{
				remoteInventory.getInv().setHotbarIndex(-1);
			}

			if (PE.inventory.items.size() > 0)
			{
				PE.inventory.setHotbarIndex(0);
			}

			localInvActive = true;
		}
	}
	
	public void SwapInv() {
		if (localInvActive && remoteInventory != null && remoteInventory.getInv().items.size() > 0)
		{
			SwitchInventory(false);
		} else if (!localInvActive && PE.inventory.items.size() > 0)
		{
			SwitchInventory(true);
		}
	}

	


	@Override
	public void keyTyped(KeyEvent arg0)
	{

	}

	@Override
	public void mouseClicked(MouseEvent arg0)
	{

	}

	@Override
	public void mouseEntered(MouseEvent arg0)
	{

	}

	private boolean placeBlock(String className)
	{
		int nextX = SelectedBlock.x + SelectedFace.direction[0];
		int nextY = SelectedBlock.y + SelectedFace.direction[1];
		int nextZ = SelectedBlock.z + SelectedFace.direction[2];

		Block placeable = Engine.createBlockByClass(className, nextX, nextY, nextZ);
		if (!placeable.getCanBePlacedOn().isEmpty() && !placeable.getCanBePlacedOn()
				.contains(Engine.world.getBlockAt(placeable.x, placeable.y, placeable.z - 1).name))
		{
			return false;
		}
		
		boolean success =  Engine.world.addBlockNoReplace(placeable, true);
		if(success && placeable instanceof TextureListener tl) {
			tl.updateTexture(new Vector(), this);
		}
		if (success && placeable instanceof PlaceListener pl) 
		{
			pl.placed(SelectedFace);
		}
		
		return success;
		

	}

	private void SwapItems(boolean addToLocal, String itemName)
	{
		if (Engine.client == null)
		{
			if (!addToLocal)
			{
				if (PE.inventory.items.size() > 0 && remoteInventory != null)
				{
					ItemType removedFromLocal = PE.inventory.getSelectedKind();
					remoteInventory.getInv().add(removedFromLocal, 1, true);
					PE.inventory.add(removedFromLocal, -1, true);
				}


			} else
			{
				ItemType removedFromActiveInv = remoteInventory.getInv().getSelectedKind();
				PE.inventory.add(removedFromActiveInv, 1, true);
				remoteInventory.getInv().add(removedFromActiveInv, -1, true);


			}

		} else
		{
			Engine.client.sendInvSwap(Config.username, remoteInventory.getBlock(), itemName,addToLocal);

		}
	}

	private void teleportToSpawn()
	{
		Block SpawnBlock = Engine.world.getSpawnBlock();
		PE.move(SpawnBlock.x + 0.5f, SpawnBlock.y + 0.5f, SpawnBlock.z + 2.7f, false);
	}


}
