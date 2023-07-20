package ml.sakii.factoryisland.blocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import ml.sakii.factoryisland.Color4;
import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Model;
import ml.sakii.factoryisland.Object3D;
import ml.sakii.factoryisland.Point3D;
import ml.sakii.factoryisland.Polygon3D;
import ml.sakii.factoryisland.Surface;
import ml.sakii.factoryisland.Vector;
import ml.sakii.factoryisland.World;

public abstract class Block extends Model.Int implements BlockInterface
{

	public static final Block NOTHING = new Nothing();
	public final HashMap<String, String> BlockMeta = new HashMap<>();
	public final ArrayList<String> canBePlacedOn = new ArrayList<>();
	public int refreshRate = 1;
	public boolean solid=true;

	public boolean transparent=false;


	public final HashMap<Polygon3D, BlockFace> HitboxPolygons = new HashMap<>();
	public ArrayList<Object3D> Objects = new ArrayList<>(6);
	public boolean fullblock = true;
	public int lightLevel = 0;
	private BlockFace selectedFace = BlockFace.NONE;
	

	/** ModBlock és Nothing miatt */
	public Block(int x, int y, int z, GameEngine engine)
	{
		super("",x,y,z,engine);
	}

	public Block(String name, int x, int y, int z, GameEngine engine)
	{
		this(name, x, y, z, 1, 1, 1, engine);
	}
	
	
	public Block(String name, int x, int y, int z, float xscale, float yscale, float zscale, GameEngine engine)
	{
		super(name,x,y,z,engine);
		generate( xscale, yscale, zscale);
	}





	void generate(float xscale, float yscale, float zscale)
	{
		Surface[] surfaces=getSurfaces();
		Surface top = surfaces[0];
		Surface bottom = surfaces[1];
		Surface north = surfaces[2];
		Surface south = surfaces[3];
		Surface east = surfaces[4];
		Surface west = surfaces[5];
		float xk = x + (0.5f - xscale / 2);
		float xn = x + (0.5f + xscale / 2);
		float yk = y + (0.5f - yscale / 2);
		float yn = y + (0.5f + yscale / 2);
		float zk = z + (0.5f - zscale / 2);
		float zn = z + (0.5f + zscale / 2);

		if (xscale != 1 || yscale != 1 || zscale != 1)
		{
			fullblock = false;
		}

		int w = top.color ? 1 : top.Texture.getWidth() - 1, h = top.color ? 1 : top.Texture.getHeight() - 1;

		Vector xnykzn = new Vector(xn, yk, zn);
		Vector xnynzn = new Vector(xn, yn, zn);
		Vector xnynzk = new Vector(xn, yn, zk);
		Vector xnykzk = new Vector(xn, yk, zk);
		

		Vector xkykzk = new Vector(xk, yk, zk);
		Vector xkynzk = new Vector(xk, yn, zk);
		Vector xkynzn = new Vector(xk, yn, zn);
		Vector xkykzn = new Vector(xk, yk, zn);
		

		Objects.add(new Polygon3D(new Vector[]	{ xkynzn, xnynzn, xnykzn, xkykzn }, new int[][] {{0,0},{w,0},{w,h},{0,h}},top,this));
		Objects.add(new Polygon3D(new Vector[]	{ xnynzk, xkynzk, xkykzk, xnykzk }, new int[][] {{0,0},{w,0},{w,h},{0,h}},bottom,this));


		Objects.add(new Polygon3D(new Vector[]	{ xnynzn, xkynzn, xkynzk, xnynzk }, new int[][] {{0,0},{w,0},{w,h},{0,h}}, north,this));
		Objects.add(new Polygon3D(new Vector[]	{ xkykzn, xnykzn, xnykzk, xkykzk }, new int[][] {{0,0},{w,0},{w,h},{0,h}}, south,this));
		
		Objects.add(new Polygon3D(new Vector[]	{ xnykzn, xnynzn, xnynzk, xnykzk  }, new int[][] {{0,0},{w,0},{w,h},{0,h}}, east,this));
		Objects.add(new Polygon3D(new Vector[]	{ xkynzn, xkykzn, xkykzk, xkynzk  }, new int[][] {{0,0},{w,0},{w,h},{0,h}}, west,this));


		HitboxPolygons.put((Polygon3D)Objects.get(0), BlockFace.TOP);
		HitboxPolygons.put((Polygon3D)Objects.get(1), BlockFace.BOTTOM);
		HitboxPolygons.put((Polygon3D)Objects.get(2), BlockFace.NORTH);
		HitboxPolygons.put((Polygon3D)Objects.get(3), BlockFace.SOUTH);
		HitboxPolygons.put((Polygon3D)Objects.get(4), BlockFace.EAST);
		HitboxPolygons.put((Polygon3D)Objects.get(5), BlockFace.WEST);


	}
	
	//TODO add polygonIterator
	/*
	public Iterator<Polygon3D> polygonIterator(){
		return new Iterator<>() {
			Iterator<Object3D> iter = Objects.iterator();
			Object3D next = null;
			
			
			@Override
			public boolean hasNext() {
				do{
					next = iter.next();
				}while(next != null && !(next instanceof Polygon3D));
				return next == null;
			}
			
			@Override
			public Polygon3D next() {
				if(next==null) {
					hasNext();
				}
				return (Polygon3D)next;
			}
			
			
			
		}
	}*/
	
	public void select(BlockFace face)
	{
		if (selectedFace != face)
		{
			for (Entry<Polygon3D, BlockFace> entry : HitboxPolygons.entrySet())
			{
				if (entry.getValue() == face)
				{
					entry.getKey().selected = true;
				} else
				{
					entry.getKey().selected = false;
				}
			}
			selectedFace = face;
		}
	}

	

	public BlockFace getSelectedFace()
	{
		return selectedFace;
	}

	static Surface[] generateSurfacesNoCopy(Surface s) {
		return new Surface[] {s,s,s,s,s,s};
	}
	
	static Surface[] generateSurfacesCopy(Surface s) {
		return new Surface[] {s.copy(),s.copy(),s.copy(),s.copy(),s.copy(),s.copy()};
	}
	
	static Surface[] generateSurfaces(Color4 c) {
		return new Surface[] {new Surface(c),new Surface(c),new Surface(c),new Surface(c),new Surface(c),new Surface(c)};
	}


	public void recalcOcclusions(World world, Point3D tmp) {
		
	
		for(Entry<Polygon3D, BlockFace> entry1 : HitboxPolygons.entrySet()) {
			Polygon3D poly = entry1.getKey();
			poly.clearOcclusions();
				
			if(transparent || lightLevel>0) {
				continue;
			}
		
			BlockFace face = entry1.getValue();
			
			for(Entry<BlockFace, Block> entry : world.get6Blocks(pos.cpy().add(face), false).entrySet()) {
				if(entry.getValue().transparent || entry.getValue().lightLevel>0) {
					continue;
				}
				
				BlockFace nearbyFace = entry.getKey();
				if(nearbyFace == face || nearbyFace == face.getOpposite()) {
					continue;
				}
				poly.addSimpleOcclusion(nearbyFace);
				
				
				
			}

		
			if(transparent || lightLevel>0) {
				return;
			}
	
			for(Entry<Point3D, Block> entry : world.get4Blocks(pos, face, false).entrySet()) {
				Point3D delta = entry.getKey();
				if(isBlockingCorner(world.getBlockAtP(tmp.set(pos).add(face).add(delta.x,0,0))) || 
						isBlockingCorner(world.getBlockAtP(tmp.set(pos).add(face).add(0,delta.y,0))) || 
							isBlockingCorner(world.getBlockAtP(tmp.set(pos).add(face).add(0,0,delta.z)))) {
					continue;
				}
				if(entry.getValue().transparent || entry.getValue().lightLevel > 0) {
					continue;
				}
				poly.addCornerOcclusion(delta);
			}
			
			
			poly.recalcTexturedOcclusions();
		}
	}
	
	private static boolean isBlockingCorner(Block b) {
		return !b.transparent && b != Block.NOTHING;
	}




	public final void setMetadata(String key, String value, boolean resend)
	{
		if(BlockMeta.get(key) != null && BlockMeta.get(key).equals(value)) {
			return;
		}
		if(resend && Engine.client != null) {
			Engine.client.sendMetadataEdit(this, key, value);
		
		}else {
			boolean alreadyput = false;
			if(this instanceof MetadataListener ml) {
				
				alreadyput = ml.onMetadataUpdate(key, value);
				
				
			}
			if(!alreadyput) {
				BlockMeta.put(key, value);
			}
			if(this instanceof TickListener) {
				Engine.TickableBlocks.add(pos);
			}
			for (Block b2 : Engine.world.get6Blocks(this, false).values())
			{
				if (b2 instanceof TickListener && (Engine.client == null || (Engine.client != null && Engine.client != null)))
				{
					Engine.TickableBlocks.add(b2.pos);
				}
			}

			
		}

		

	}

	@Override
	public String toString()
	{
		return name + "," + x + "," + y + "," + z;
	}



	BlockFace getTarget()
	{
		return BlockFace.values[java.lang.Integer.parseInt(this.BlockMeta.get("target"))];
	}

	long getTick()
	{
		if (Engine != null)
		{
			return Engine.Tick;
		}
		return 0;
	}
	

	/**
	 * Block() ->
	 * init() ->
	 * postInit() ->
	 * generate() ->
	 * getSurfaces()<br>
	 * ha valamikor is változik bármelyik tulajdonság bármelyik értéke, new-okat kell használni
	 * 
	 */
	public abstract Surface[] getSurfaces();
	
	@Override
	public GameEngine getEngine() {
		return Engine;
	}
	
	@Override
	public World getWorld() {
		return Engine.world;
	}
	
	@Override
	public HashMap<String, String> getBlockMeta() {
		return BlockMeta;
	}
	
	@Override
	public void setBlockMeta(String key, String value, boolean resend) {
		setMetadata(key, value, resend);

	}

}
