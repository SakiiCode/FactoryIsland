package ml.sakii.factoryisland;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Paint;
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
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ml.sakii.factoryisland.blocks.Block;
import ml.sakii.factoryisland.blocks.BlockFace;
import ml.sakii.factoryisland.blocks.BlockInventoryInterface;
import ml.sakii.factoryisland.blocks.BreakListener;
import ml.sakii.factoryisland.blocks.InteractListener;
import ml.sakii.factoryisland.blocks.LoadListener;
import ml.sakii.factoryisland.blocks.PlaceListener;
import ml.sakii.factoryisland.blocks.PowerConsumer;
import ml.sakii.factoryisland.blocks.PowerWire;
import ml.sakii.factoryisland.blocks.TextureListener;
import ml.sakii.factoryisland.blocks.TickListener;
import ml.sakii.factoryisland.blocks.WaterBlock;
import ml.sakii.factoryisland.entities.Entity;
import ml.sakii.factoryisland.entities.PlayerMP;
import ml.sakii.factoryisland.items.ItemType;
import ml.sakii.factoryisland.items.PlayerInventory;
import ml.sakii.factoryisland.net.GameServer;

public class Game extends JPanel implements KeyListener, MouseListener, MouseWheelListener
{

	 static final long serialVersionUID = 2515747642091598425L;

	public GameEngine Engine;
	public PlayerMP PE;
	public final CopyOnWriteArrayList<Object3D> Objects = new CopyOnWriteArrayList<>();

	public boolean moved;
	public BlockInventoryInterface remoteInventory;

	final Vector BottomViewVector = new Vector(), TopViewVector = new Vector(), RightViewVector = new Vector(),
			LeftViewVector = new Vector();
	 final Vector ViewTo = new Vector();
	 final Vector FrontViewVector = new Vector();
	 final Vector BackViewVector = new Vector();
	

	float hratio, vratio;
	final boolean[] key = new boolean[20];
	boolean locked = false;
	float ratio;
	int margin;
	CopyOnWriteArrayList<TextureListener> TextureBlocks = new CopyOnWriteArrayList<>();
	Frustum ViewFrustum;
	Vector ViewVector = new Vector();

	boolean centered;
	 float centerX, centerY, McenterX, McenterY;

	 float difX, difY;
	 Point2D.Float dP=new Point2D.Float();

	 float dx, dy;
	 boolean F3 = false;
	 float FPS = 30f;
	 LinkedList<String> debugInfo = new LinkedList<>();
	 long lastTime;
	 int totalframes;

	BufferedImage FrameBuffer;
	BufferedImage prevFrame;

	 final Cursor invisibleCursor = Toolkit.getDefaultToolkit()
			.createCustomCursor(new BufferedImage(1, 1, Transparency.TRANSLUCENT), new Point(0, 0), "InvisibleCursor");

	 boolean localInvActive = true;
	 final int MAXSAMPLES = 30;
	 BufferedImageOp op;

	 float measurement;
	 Vector previousPos;
	 EAngle previousAim;
	 long previousTime, currentTime;
	 Robot rob;

	 boolean running = true;

	boolean firstframe = true;
	

	 BlockFace SelectedFace;
	 Polygon3D SelectedPolygon;
	 Block SelectedBlock = Block.NOTHING;
	 Entity SelectedEntity;
	 Block ViewBlock;
	 float speed = 4.8f;
	 Star[] Stars;
	 int tickindex = 0;
	 final float[] ticklist = new float[MAXSAMPLES];

	 float ticksum = 0;
	 int VisibleCount;
	boolean creative;
	String error;
	
	boolean customfly=false;

	
	RenderThread renderThread;
	boolean showHUD=true;

	private Paint crosshairColor=new Color(1.0f, 1.0f, 1.0f, 0.2f);
	private Vector tmp = new Vector();


	int previousSkyLight;
	static long truetime=0l;
	static long falsetime=0l; 
	
	public Game(String location, long seed, LoadMethod loadmethod, JLabel statusLabel) {

		try {

			Engine = new GameEngine(location, this, seed, loadmethod, statusLabel);

			

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
			int port = GameServer.DEFAULT_PORT;
			if (addr.length != 1)
			{
				port = Integer.parseInt(addr[1]);
			}
			error = Engine.startClient(addr[0], port, this);
			
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
		
		statusLabel.setText("Loading player inventory...");
		if(Config.creative) {
			PE.inventory=PlayerInventory.Creative;
		}else {
			PE.inventory=Engine.world.loadInv(PE.name, Engine);
		}
		
		init();
		
		previousPos = new Vector().set(PE.getPos());
		previousAim = new EAngle(PE.ViewAngle.yaw, PE.ViewAngle.pitch);
		
		for(Block b : Engine.world.getWhole(false)) {
			if(b instanceof LoadListener) {
				((LoadListener)b).onLoad();
			}
			if(b.lightLevel>1) {
				Engine.world.addLight(b.pos, b, b.lightLevel);
			}
		}
		
		if(PE.inventory.items.size()>0) {
			PE.inventory.setHotbarIndex(0);
		}
		
		
		//SwitchInventory(true);

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

		//this.setDoubleBuffered(true);
		setCursor(invisibleCursor);

		McenterX = Main.Frame.getX() + Main.Width / 2;
		McenterY = Main.Frame.getY() + Main.Height / 2;

		ViewVector.set(PE.ViewAngle.toVector());

		RightViewVector.set(ViewVector).CrossProduct(PE.VerticalVector);
		TopViewVector.set(RightViewVector).CrossProduct(ViewVector);
		LeftViewVector.set(RightViewVector).multiply(-1);
		BottomViewVector.set(TopViewVector).multiply(-1);

		ViewFrustum = new Frustum(this);

		resizeScreen(Config.width, Config.height);
		//resizeScreen(Main.Frame.getWidth(), Main.Frame.getHeight());
		ViewTo.set(PE.getPos()).add(ViewVector);
		setP(ViewTo,dP);
		dx = -Config.zoom * dP.x + centerX;
		dy = -Config.zoom * dP.y + centerY;


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

		Stars = new Star[200];
		for (int i = 0; i < Stars.length-1; i++)
		{
			Stars[i] = new Star(this);
		}

		Stars[Stars.length-1] = new Star(this,100);
		renderThread = new RenderThread(this);
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
				CalcAverageTick();
			}

			if (Main.Frame.isActive())
			{
				
				if(centered) {
					Point arg0 = MouseInfo.getPointerInfo().getLocation();
					difX = McenterX - (float) (arg0.getX());
					difY = McenterY - (float) (arg0.getY());
				}else {
					difX=0;
					difY=0;
				}
				
				try
				{
					rob.mouseMove((int) McenterX, (int) McenterY);
					centered = true;
				} catch (Exception e)
				{
					Main.log("Could not center mouse:" + e.getMessage());
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

			BackViewVector.set(ViewVector).CrossProduct(PE.VerticalVector).CrossProduct(PE.VerticalVector);
			FrontViewVector.set(BackViewVector).multiply(-1);
			Controls();
			Vector PEPos = PE.getPos();
			moved = !previousPos.equals(PEPos) || !previousAim.equals(PE.ViewAngle);
			previousPos.set(PEPos);
			previousAim.pitch = PE.ViewAngle.pitch;
			previousAim.yaw = PE.ViewAngle.yaw;

			if (moved || firstframe)
			{

				PE.ViewAngle.normalize();

				ViewVector.set(PE.ViewAngle.toVector());

				RightViewVector.set(ViewVector).CrossProduct(PE.VerticalVector);
				TopViewVector.set(RightViewVector).CrossProduct(ViewVector);
				LeftViewVector.set(RightViewVector).multiply(-1);
				BottomViewVector.set(TopViewVector).multiply(-1);

				if (!locked) ViewFrustum.update();

				ViewTo.set(PEPos).add(ViewVector);
				setP(ViewTo,dP);

				dx = -Config.zoom * dP.x + centerX;
				dy = -Config.zoom * dP.y + centerY;

				firstframe = false;
			}



			
			if (!locked)
			{
				for (TextureListener b : TextureBlocks)
				{
					b.updateTexture(tmp);
				}

			}


			PE.tmpPoint.set((int)Math.floor(PEPos.x), (int)Math.floor(PEPos.y), (int)Math.floor(PEPos.z));
			ViewBlock = Engine.world.getBlockAtP(PE.tmpPoint);



			SelectedPolygon = null;
			VisibleCount = 0;

			Graphics fb = g;//FrameBuffer.createGraphics();
			fb.setColor(Main.skyColor);
			fb.fillRect(0, 0, Config.width, Config.height);

			fb.setColor(Color.WHITE);
			
			Stars[Stars.length-1].pos.set((float)Math.cos(Polygon3D.getTimePercent(Engine.Tick)*2*Math.PI), 0f, (float)Math.sin(Polygon3D.getTimePercent(Engine.Tick)*2*Math.PI));
			
			for (int i = 0; i < Stars.length; i++)
			{
				Stars[i].draw(fb);
			}
			
			int skyLight = Polygon3D.testLightLevel(Polygon3D.getTimePercent(Engine.Tick));
			if(skyLight != previousSkyLight) {
				previousSkyLight=skyLight;
				new Thread() {
					@Override
					public void run() {
						
						for(Object3D o : Objects) {
							if(o instanceof Polygon3D) {
								((Polygon3D)o).recalcLightedColor();
							}
						}
					}
				}.start();
				
			}
			
			
			

			Objects.parallelStream().filter(o -> o.update()).sorted().forEachOrdered(o->
			
			{
				
				o.draw(FrameBuffer, fb);
				
				if(o instanceof Polygon3D) {
					Polygon3D poly = (Polygon3D)o;
					if (poly.AvgDist < 5 && poly.polygon.contains(centerX, centerY))
					{
						SelectedPolygon = poly;
					}
					VisibleCount++;
				}
			});
			
				
				
	

			
			
			
			if (SelectedPolygon == null)
			{
				SelectedBlock.select(BlockFace.NONE);
				SelectedBlock = Block.NOTHING;
				SelectedFace = BlockFace.NONE;
				SelectedEntity=null;

			} else
			{
				if (SelectedBlock.HitboxPolygons.containsKey(SelectedPolygon))
				{
					BlockFace newFace = SelectedBlock.HitboxPolygons.get(SelectedPolygon);
					if (newFace != SelectedFace)
					{
						SelectedFace = newFace;
						SelectedBlock.select(SelectedFace);
					}
				} else
				{
					SelectedBlock.select(BlockFace.NONE);
					SelectedBlock = Block.NOTHING;
					SelectedFace = BlockFace.NONE;
					Vector centroid = SelectedPolygon.centroid;
					PE.tmpPoint.set((int)Math.floor(centroid.x), (int)Math.floor(centroid.y), (int)Math.floor(centroid.z));
					Block block1 = Engine.world.getBlockAtP(PE.tmpPoint);
					if (block1.HitboxPolygons.containsKey(SelectedPolygon))
					{
						SelectedFace = block1.HitboxPolygons.get(SelectedPolygon);
						SelectedBlock.select(BlockFace.NONE);
						SelectedBlock = block1;
						SelectedBlock.select(SelectedFace);
					} else
					{
						HashMap<BlockFace, Block> BlocksNearby;
						if (block1 == Block.NOTHING)
						{
							PE.tmpPoint.set(centroid.x, centroid.y, centroid.z);
						} else
						{
							PE.tmpPoint.set(block1.pos);
						}

						BlocksNearby = Engine.world.get6Blocks(PE.tmpPoint, false);
						
						for (Block block2 : BlocksNearby.values())
						{
							if (block2.HitboxPolygons.containsKey(SelectedPolygon))
							{
								SelectedFace = block2.HitboxPolygons.get(SelectedPolygon);
								SelectedBlock.select(BlockFace.NONE);
								SelectedBlock = block2;
								SelectedBlock.select(SelectedFace);
								break;
							}

						}
						
						if(SelectedBlock == Block.NOTHING) {
							for(Entity e : Engine.world.getAllEntities()) {
								if(e.Objects.contains(SelectedPolygon)) {
									SelectedEntity=e;
									break;
								}
							}
							
						}

					}
					
					

				}
				

				
				

			}
			if (remoteInventory != null)
			{
				fb.drawImage(op.filter(FrameBuffer, null), 0, 0, null);
			}

			if (ViewBlock instanceof WaterBlock)
			{
				g.setColor(ViewBlock.Polygons.get(0).s.c.getColor());
				g.fillRect(0, 0, Main.Width, Main.Height);
			}

			if(showHUD) {
				((Graphics2D) g).setPaint(crosshairColor);
	
				int cX = (int)centerX;
				int cY = (int)centerY;
				int crosshairSize = Config.height/50;
				g.drawLine(cX, cY - crosshairSize, cX, cY + crosshairSize);
				g.drawLine(cX - crosshairSize, cY, cX + crosshairSize, cY);
				
				
				int fontSize = (int) (Math.cbrt(Config.height)*2)-2;
				g.setFont(new Font("SANS", Font.BOLD, fontSize));
				g.setColor(Color.GRAY);
				float viewportscale=(float) Math.sqrt(Config.width*Config.height*1f/Main.Width/Main.Height);
	
				if (F3)
				{
	
					debugInfo.clear();
					debugInfo.add("Eye:" + PEPos+", yaw: " + Math.round(PE.ViewAngle.yaw) + ", pitch: " + Math.round(PE.ViewAngle.pitch));
					debugInfo.add("Health: " + PE.getHealth() +", ID: " + PE.ID);
					debugInfo.add("FPS (smooth): " + (int) measurement + " - " + FPS);
					debugInfo.add("SelBlock:" + SelectedBlock.getSelectedFace() + ", "+SelectedBlock+",meta:"+SelectedBlock.BlockMeta);
					if(SelectedBlock instanceof PowerWire) {
						debugInfo.add(((PowerWire)SelectedBlock).powers+"");
					}
					if(SelectedBlock instanceof PowerConsumer) {
						debugInfo.add(((PowerConsumer)SelectedBlock).powers+"");
					}
					if(SelectedPolygon != null) {
						debugInfo.add("SelPoly: "+SelectedPolygon+"light:"+SelectedPolygon.getLight());
					}else {
						debugInfo.add("SelPoly: null");
					}
					if(SelectedEntity!=null) {
						debugInfo.add("SelectedEntity: "+SelectedEntity + ", health:" + SelectedEntity.getHealth());
					}else {
						debugInfo.add("SelectedEntity: null");
					}
					debugInfo.add("Polygon count: " + VisibleCount + "/" + Objects.size());
					debugInfo.add("truetime:"+truetime+"falsetime"+falsetime+ ",testing:"+key[6]);
					debugInfo.add("Filter locked: " + locked + ", moved: " + moved + ", nopause:" + Main.nopause);
					debugInfo.add("Tick: " + Engine.Tick + "(" + Engine.TickableBlocks.size() + ")");
					debugInfo.add("needUpdate:" + Engine.TickableBlocks.contains(SelectedBlock.pos) );
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
					int x=(int) Math.floor(PEPos.x) ;
					int y= (int) Math.floor(PEPos.y);
					int feetZ = (int) Math.floor(PEPos.z - ((1.7f + World.GravityAcceleration / FPS) * PE.VerticalVector.z));
					PE.tmpPoint.set(x, y, feetZ);
					debugInfo.add("Physics FPS: " + (int)Engine.actualphysicsfps +", skyLight:"+Polygon3D.getTimePercent(Engine.Tick)+", cached:"+previousSkyLight);
					debugInfo.add("level:"+Polygon3D.testLightLevel(Polygon3D.getTimePercent(Engine.Tick)) + "Inverse:"+Polygon3D.testLightLevel(Polygon3D.getTimePercent(Engine.Tick)));	
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
					g.drawString("FPS: " + (int) measurement, 20, 20);
	
				}
				
				//int w=0, h=0, offset=0;
	
				//INVENTORY SOR
				if (PE.inventory.items.size() > 0)
				{
										
					
					// VIEWMODEL
					if (PE.inventory.hasSelected())
					{

						BufferedImage viewmodel = Main.Items.get(PE.inventory.getSelectedKind().name).ViewmodelTexture;
						int wv = viewmodel.getWidth();
						int hv = viewmodel.getHeight();
	
						g.drawImage(viewmodel, Config.width / 3 * 2, Config.height - hv, 2 * wv, 2 * hv, null);

					}
					
					drawInventory(g, PE.inventory, fontSize, viewportscale, true, null, localInvActive);
					
	
				}
	
				if (remoteInventory != null && remoteInventory.getInv().items.size()>0)
				{
						
					drawInventory(g, remoteInventory.getInv(), fontSize, viewportscale, false, remoteInventory.getBlock(), localInvActive);
	
				}
				
				int icon = (int) (Main.Frame.getWidth() * 64f / 1440f*viewportscale);
				int centerY = Config.height-icon-icon/2;
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


		int halfTextHeight=-g.getFontMetrics().getAscent()+g.getFontMetrics().getHeight()/2;//-(int) (getStringBounds(g,"0123456789ABCDEFGHIKLMNOPRSTUVWXYZ",0,0).getHeight()/2); // J Q nelkul
		
		int i = -1;
		int icon = (int) (Main.Frame.getWidth() * 64f / 1440f*viewportscale);
		int iconTextOffset = icon / 3;
		int iconY=Config.height - icon - (local?0:2*icon);
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
				g.setColor(Color.WHITE);
				g.drawString(amount + "", textX, textY);

				g.setColor(Color.BLACK);
				g.drawString(amount + "", textX - 1, textY - 1);

			} else
			{ //kiemelt
				g.setColor(Color.BLACK);
				g.drawString(amount + "", textX, textY);

				g.setColor(Color.WHITE);
				g.drawString(amount + "", textX - 1, textY - 1);
				
				//Selected.set(kind, amount);
			}
		}
		
		// KIJELÖLT FELIRAT
		if (Inv.hasSelected()) 
		{
			g.setColor(Color.BLACK);
			g.drawString(Inv.getSelectedKind().name, (int) (25*viewportscale), Config.height - icon-icon/2-halfTextHeight);
			g.setColor(Color.WHITE);
			g.drawString(Inv.getSelectedKind().name, (int) (25*viewportscale - 1), Config.height - icon-icon/2-halfTextHeight - 1);
		
		}
		
		// TÁVOLI BLOKK
		if(!local) {
			
			
			g.setColor(Color.BLACK);
			g.drawString(remoteBlock.toString(), 25, Config.height - 3*icon-icon/2-halfTextHeight);
			g.setColor(Color.WHITE);
			g.drawString(remoteBlock.toString(), 25 - 1, Config.height - 3*icon-icon/2-halfTextHeight - 1);
			
		}
		/*g.drawRect(0, Config.height-icon, icon, icon);
		g.setColor(Color.RED);
		g.drawLine(0, Config.height-icon-halfTextHeight, icon, Config.height-icon-halfTextHeight);
		g.setColor(Color.BLUE);
		g.drawLine(0, textY, icon, textY);
		g.setColor(Color.WHITE);
		g.drawRect(0, Config.height-2*icon, icon, icon);
		g.drawRect(0, Config.height-3*icon, icon, icon);
		g.drawRect(0, Config.height-4*icon, icon, icon);*/
		
	}

	void Controls()
	{

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
			PE.ViewAngle.yaw += difX * (Config.sensitivity) / FPS * PE.VerticalVector.z;

		}
		if (difY != 0)
		{
			PE.ViewAngle.pitch += difY * (Config.sensitivity) / FPS * PE.VerticalVector.z;

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
			PE.VerticalVector.z = -1;
			Block above = Engine.world.getBlockUnderEntity(false, true, PE);
			if (PEPos.z >= above.z - 1.7f)
			{
				PEPos.z = above.z - 1.7f;
			}
		} else if (PE.VerticalVector.z == -1 && PEPos.z >= 0 && invunder != Block.NOTHING)
		{
			PE.VerticalVector.z = 1;

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
			GameEngine.doGravity(PE, Engine.world, (int) FPS);
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
			if(Main.devmode) {
				locked = !locked;
			}

			for(Point3D t : Engine.TickableBlocks) {
				Block b = Engine.world.getBlockAtP(t);
				System.out.println(b);
			}
		}
		if (arg0.getKeyCode() == KeyEvent.VK_F3)
		{
			F3 = !F3;
		}
		if (arg0.getKeyCode() == KeyEvent.VK_F6)
		{
			String error =Engine.startServer(); 
			if(error != null) {
				disconnect(error);
			}
		}

		if (arg0.getKeyCode() == KeyEvent.VK_ESCAPE)
		{
			pauseTo("pause");
		}

		if (arg0.getKeyCode() == KeyEvent.VK_T)
		{
			key[6] = true;
		}
		if (arg0.getKeyCode() == KeyEvent.VK_CONTROL)
		{
			key[7] = !key[7];
			customfly = key[7];
			if (customfly)
			{
				speed = 8f;
				
			} else
			{
				speed = 4.7f;
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
		if (remoteInventory == null)
		{
			if (arg0.getButton() == MouseEvent.BUTTON1)
			{
				if (SelectedBlock != Block.NOTHING)
				{
					boolean returnAsItem=true;
					
					if (SelectedBlock instanceof BreakListener)
					{
						returnAsItem = ((BreakListener) SelectedBlock).breaked(Config.username);
					}
					ItemType item = Main.Items.get(SelectedBlock.name);
					Engine.world.destroyBlock(SelectedBlock, true);
					
					if(returnAsItem)
					{
						
						PE.inventory.add( item, 1, true);

					}
				}else if(SelectedEntity != null) {
					Engine.world.hurtEntity(SelectedEntity.ID, 1, true);

					
				}
				
			}

			if (arg0.getButton() == MouseEvent.BUTTON3)
			{
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
					} else if (SelectedBlock instanceof InteractListener)
					{
						((InteractListener) SelectedBlock).interact(SelectedFace);
						if(SelectedBlock instanceof TickListener) {
							Engine.TickableBlocks.add(SelectedBlock.pos);
						}
						if (SelectedBlock instanceof BlockInventoryInterface)
						{
							PlayerInventory otherInv = ((BlockInventoryInterface) SelectedBlock).getInv();
							if (PE.inventory.items.size() == 0 && otherInv.items.size() > 0)
							{
								SwitchInventory(false);
							}
						}
					}
				}

			}
		}
		if (arg0.getButton() == MouseEvent.BUTTON2)
		{
			if (localInvActive)
			{
				if(PE.inventory.hasSelected())
					SwapItems(false, PE.inventory.getSelectedKind().name);
			} else
			{
				SwapItems(true, remoteInventory.getInv().getSelectedKind().name);
			}

		}

	}


	
	public Point2D.Float convert3Dto2D(Vector tmp, Point2D.Float point)
	{
		setP(tmp, point);
		float x2d = dx + Config.zoom * point.x;
		float y2d = dy + Config.zoom * point.y;

		point.setLocation(x2d, y2d);
		return point;

	}
	
	 private void setP(Vector tmp, Point2D.Float point)
	{
		Vector PEPos = PE.getPos();
		float t = tmp.substract(PEPos).DotProduct(ViewVector);
		tmp.multiply(1 / t).add(PEPos);

		point.setLocation(RightViewVector.DotProduct(tmp), BottomViewVector.DotProduct(tmp));
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


	

	public void disconnect(String error)
	{
		
		Thread t = new Thread() {
			@Override
			public void run() {
				
			
				renderThread.kill();
				Engine.disconnect(error);
				Objects.clear();
				Main.SwitchWindow("mainmenu");
				Main.Base.remove(Game.this);
				Main.GAME = null;
				System.gc();
				
			}
		};
		
		t.start();
	}


	
	void pauseTo(String UiName) {
		renderThread.kill();

		if(Config.directRendering) {
			Main.PausedBG=Main.originalPausedBG;
		}else {
			Main.PausedBG = op.filter(Main.deepCopy(FrameBuffer), null);
		}
		if (Engine.isSingleplayer()) { // ezek multiplayerben nem allhatnak le
			Engine.ticker.stop();
			Engine.stopPhysics();//tavoli mpnel nem is futott ugyhogy mindegy
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

		Main.SwitchWindow(UiName);
	}
	
	public void notifyDeath() {
		new Thread() {
			@Override
			public void run() {
				if(renderThread.isAlive()) {
					pauseTo("died");
				}
			}
		}.start();

	}
	
	void respawn() {
		PE.getPos().set(Engine.world.getSpawnBlock().pos).add(new Vector(0,0,2.7f));
		PE.setHealth(PE.maxHealth);
		Engine.world.addEntity(PE, true);
	}

	void resume()
	{
		if(PE.getHealth()==0) {
			Main.SwitchWindow("died");
			return;
		}
		setCursor(invisibleCursor);
		if(!Engine.ticker.isRunning()) {
			Engine.ticker.start();
		}
		if(!Engine.isPhysicsRunning() && (Engine.isSingleplayer() || Engine.isLocalMP())) {
			Engine.startPhysics();
		}
		firstframe = true;
		renderThread = new RenderThread(this);
		renderThread.start();
		
		addKeyListener(this);
		addMouseListener(this);
		addMouseWheelListener(this);
		currentTime=System.nanoTime();
		Main.SwitchWindow("game");

	}

	void resizeScreen(int w, int h)
	{

		FrameBuffer = Main.toCompatibleImage(new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB));

		centerX = w / 2;
		centerY = h / 2;

		ViewFrustum.hratio = centerX / Config.zoom;
		ViewFrustum.vratio = centerY / Config.zoom;

		ratio = (w * 1f / Main.Width
				+ h * 1f / Main.Height) / 2;
		margin = (int) (5 * ratio);

	}

	void CalcAverageTick()
	{
		 totalframes++;
		 long thisTime = System.currentTimeMillis();
		 if(thisTime - lastTime > 500) {
			 measurement=totalframes*2;
			 lastTime=thisTime;
			 totalframes=0;
		 }
		
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

	 boolean placeBlock(String className)
	{
		int nextX = SelectedBlock.x + SelectedFace.direction[0];
		int nextY = SelectedBlock.y + SelectedFace.direction[1];
		int nextZ = SelectedBlock.z + SelectedFace.direction[2];

		Block placeable = Engine.createBlockByClass(className, nextX, nextY, nextZ);
		if (!placeable.canBePlacedOn.isEmpty() && !placeable.canBePlacedOn
				.contains(Engine.world.getBlockAt(placeable.x, placeable.y, placeable.z - 1).name))
		{
			return false;
		}

		boolean success =  Engine.world.addBlockNoReplace(placeable, true);
		if (placeable instanceof PlaceListener) 
		{
			((PlaceListener) placeable).placed(SelectedFace);
		}
		return success;
		

	}

	 void SwapItems(boolean addToLocal, String itemName)
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

	 void teleportToSpawn()
	{
		Block SpawnBlock = Engine.world.getSpawnBlock();
		PE.move(SpawnBlock.x + 0.5f, SpawnBlock.y + 0.5f, SpawnBlock.z + 2.7f, false);
	}

}
