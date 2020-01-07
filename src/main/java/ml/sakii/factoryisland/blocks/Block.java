package ml.sakii.factoryisland.blocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import ml.sakii.factoryisland.Color4;
import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Point3D;
import ml.sakii.factoryisland.Polygon3D;
import ml.sakii.factoryisland.Surface;
import ml.sakii.factoryisland.Vertex;

public abstract class Block 
{

	public static final Block NOTHING = new Nothing();
	public static final int MAXLIGHT=10;
	public final HashMap<String, String> BlockMeta = new HashMap<>();
	public final ArrayList<String> canBePlacedOn = new ArrayList<>();
	public String name;
	public int refreshRate = 1;
	public boolean solid;

	public boolean transparent;
	//public Surface[] surfaces = new Surface[6];//top,bottom,north,south,east,west
	public int x, y, z;


	public Point3D pos;

	GameEngine Engine;
	
	
	public final HashMap<Polygon3D, BlockFace> HitboxPolygons = new HashMap<>();
	public ArrayList<Polygon3D> Polygons = new ArrayList<>(6);
	public boolean fullblock = true;
	public int lightLevel = 0;
	private BlockFace selectedFace = BlockFace.NONE;
	

	/** ModBlock és Nothing miatt */
	public Block(int x, int y, int z, GameEngine engine)
	{
		init("",x, y, z, engine);
	}

	public Block(String name, int x, int y, int z, GameEngine engine)
	{
		this(name, x, y, z, 1, 1, 1, engine);
	}
	
	
	public Block(String name, int x, int y, int z, float xscale, float yscale, float zscale, GameEngine engine)
	{
		init(name, x, y, z, engine);

		generate( xscale, yscale, zscale);
	}
	

	
	
	private void init(String name, int x, int y, int z, GameEngine engine) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.pos = new Point3D(x, y, z);
		this.name = name;
		this.Engine = engine;
		solid = true;
		transparent = false;
	}






	public void generate(float xscale, float yscale, float zscale)
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

		Vertex xnykzn = new Vertex(xn, yk, zn);
		Vertex xnynzn = new Vertex(xn, yn, zn);
		Vertex xnynzk = new Vertex(xn, yn, zk);
		Vertex xnykzk = new Vertex(xn, yk, zk);
		

		Vertex xkykzk = new Vertex(xk, yk, zk);
		Vertex xkynzk = new Vertex(xk, yn, zk);
		Vertex xkynzn = new Vertex(xk, yn, zn);
		Vertex xkykzn = new Vertex(xk, yk, zn);
		

		Polygons.add(new Polygon3D(new Vertex[]	{ xkynzn, xnynzn, xnykzn, xkykzn }, new int[][] {{0,0},{w,0},{w,h},{0,h}},top));
		Polygons.add(new Polygon3D(new Vertex[]	{ xkykzk, xnykzk, xnynzk, xkynzk }, new int[][] {{0,0},{w,0},{w,h},{0,h}},bottom));


		Polygons.add(new Polygon3D(new Vertex[]	{ xnynzn, xkynzn, xkynzk, xnynzk }, new int[][] {{0,0},{w,0},{w,h},{0,h}}, north));
		Polygons.add(new Polygon3D(new Vertex[]	{ xkykzn, xnykzn, xnykzk, xkykzk }, new int[][] {{0,0},{w,0},{w,h},{0,h}}, south));
		
		Polygons.add(new Polygon3D(new Vertex[]	{ xnykzn, xnynzn, xnynzk, xnykzk  }, new int[][] {{0,0},{w,0},{w,h},{0,h}}, east));
		Polygons.add(new Polygon3D(new Vertex[]	{ xkynzn, xkykzn, xkykzk, xkynzk  }, new int[][] {{0,0},{w,0},{w,h},{0,h}}, west));


		HitboxPolygons.put(Polygons.get(0), BlockFace.TOP);
		HitboxPolygons.put(Polygons.get(1), BlockFace.BOTTOM);
		HitboxPolygons.put(Polygons.get(2), BlockFace.NORTH);
		HitboxPolygons.put(Polygons.get(3), BlockFace.SOUTH);
		HitboxPolygons.put(Polygons.get(4), BlockFace.EAST);
		HitboxPolygons.put(Polygons.get(5), BlockFace.WEST);


	}
	
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

	public static Surface[] generateSurfaces(Surface s) {
		return new Surface[] {s,s,s,s,s,s};
	}
	
	public static Surface[] generateSurfaces(Color4 c) {
		Surface s = new Surface(c);
		return new Surface[] {s,s,s,s,s,s};
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
			if(this instanceof MetadataListener) {
				
				alreadyput = ((MetadataListener)this).onMetadataUpdate(key, value);
				
				
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
		return BlockFace.values[Integer.parseInt(this.BlockMeta.get("target"))];
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

}
