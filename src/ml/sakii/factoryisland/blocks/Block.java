package ml.sakii.factoryisland.blocks;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;

import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Main;
import ml.sakii.factoryisland.Object3D;
import ml.sakii.factoryisland.Polygon3D;
import ml.sakii.factoryisland.Surface;
import ml.sakii.factoryisland.Vertex;
import ml.sakii.factoryisland.items.ItemType;

public class Block extends Object3D
{

	public static final Block NOTHING = new Nothing();
	public static final int MAXLIGHT=10;
	public final HashMap<String, String> BlockMeta = new HashMap<>();
	public final ArrayList<String> canBePlacedOn = new ArrayList<>();
	public boolean fullblock = true;
	public final HashMap<Polygon3D, BlockFace> HitboxPolygons = new HashMap<>();
	public int lightLevel = 0;
	public String name = "";
	public ArrayList<Polygon3D> Polygons = new ArrayList<>(6);
	public final HashMap<BlockFace, Integer> powers = new HashMap<>();
	public int refreshRate = 1;
	//public boolean returnOnBreak = true;

	public boolean solid;

	public boolean transparent;

	public int x, y, z;

	GameEngine Engine;
	private BlockFace selectedFace = BlockFace.NONE;

	public Block(int x, int y, int z, GameEngine engine)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.Engine = engine;
		solid = true;
		transparent = false;
	}

	public Block(String name, int x, int y, int z, Surface top, Surface bottom, Surface north, Surface south,
			Surface east, Surface west, float xscale, float yscale, float zscale, GameEngine engine)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.Engine = engine;
		solid = true;
		transparent = false;

		generate(name, x, y, z, top, bottom, north, south, east, west, xscale, yscale, zscale);
	}

	public Block(String name, int x, int y, int z, Surface top, Surface bottom, Surface north, Surface south,
			Surface east, Surface west, GameEngine engine)
	{
		this(name, x, y, z, top, bottom, north, south, east, west, 1, 1, 1, engine);

	}

	public void addToUpdates(TickListener e)
	{
		//if (!Engine.TickableBlocks.contains(e))
			Engine.TickableBlocks.add(e);
	}

	public int getCharge()
	{
		if (powers.isEmpty())
		{
			return 0;
		}
		return powersSorted().lastEntry().getKey();
	}

	public int getHeight()
	{
		return Integer.parseInt(BlockMeta.get("height"));
	}

	public BlockFace getSelectedFace()
	{
		return selectedFace;
	}


	void setLight(int intensity) {
		lightLevel=intensity;
		for(Polygon3D poly : Polygons) {
			poly.addSource(this, lightLevel);
		}
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

	public final void setMetadata(String key, String value, boolean resend)
	{
		if(resend && Engine.client != null) {
			Engine.client.sendData(("07," + this.x + "," + this.y + "," + this.z + ","
					+ key + "," + value));
		
		}else {
			boolean alreadyput = false;
			if(this instanceof MetadataListener) {
				
				alreadyput = ((MetadataListener)this).onMetadataUpdate(key, value);
				
				
			}
			if(!alreadyput) {
				BlockMeta.put(key, value);
			}
			
			for (Block b2 : Engine.world.get6Blocks(this, false).values())
			{
				if (b2 instanceof TickListener && (Engine.client == null || (Engine.client != null && Engine.client != null)))
				{
					//addToUpdates((TickListener) b2);
					if(!Engine.TickableBlocks.contains(b2)) {
						Engine.TickableBlocks.add((TickListener) b2);
					}
				}
			}
			
		}

		

	}

	@Override
	public String toString()
	{
		return name + "," + x + "," + y + "," + z;
	}

	protected BufferedImage generateIcon()
	{
		int size = (int) (Main.Frame.getWidth() * 64f / 1440f);
		int s16 = (int) (Main.Frame.getWidth() * 16f / 1440f);
		BufferedImage icon = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		Graphics g = icon.createGraphics();
		Point[] p = new Point[]
		{ new Point(0, s16), new Point(s16, 0), new Point(size, 0), new Point(s16 * 3, s16), new Point(0, size),
				new Point(s16 * 3, size), new Point(size, s16 * 3) };
		Polygon top = new Polygon(new int[]
		{ p[0].x, p[1].x, p[2].x, p[3].x }, new int[]
		{ p[0].y, p[1].y, p[2].y, p[3].y }, 4);
		Polygon front = new Polygon(new int[]
		{ p[0].x, p[3].x, p[5].x, p[4].x }, new int[]
		{ p[0].y, p[3].y, p[5].y, p[4].y }, 4);
		Polygon side = new Polygon(new int[]
		{ p[2].x, p[3].x, p[5].x, p[6].x }, new int[]
		{ p[2].y, p[3].y, p[5].y, p[6].y }, 4);

		if (Polygons.get(0).s.color)
		{ // TOP
			g.setColor(Polygons.get(0).s.c.getColor());
			g.fillPolygon(top);
		} else
		{
			g.setClip(top);
			Rectangle bounds = top.getBounds();
			g.drawImage(Polygons.get(0).s.Texture, bounds.x, bounds.y, bounds.width, bounds.height, null);
			g.setClip(null);
		}

		if (Polygons.get(3).s.color)
		{ // NORTH
			g.setColor(Polygons.get(3).s.c.getColor());
			g.fillPolygon(front);
		} else
		{
			g.setClip(front);
			Rectangle bounds = front.getBounds();
			g.drawImage(Polygons.get(3).s.Texture, bounds.x, bounds.y, bounds.width, bounds.height, null);
			g.setClip(null);
		}

		if (Polygons.get(4).s.color)
		{ // WEST
			g.setColor(Polygons.get(4).s.c.getColor());
			g.fillPolygon(side);
		} else
		{
			g.setClip(side);
			Rectangle bounds = side.getBounds();
			g.drawImage(Polygons.get(4).s.Texture, bounds.x, bounds.y, bounds.width, bounds.height, null);
			g.setClip(null);
		}
		g.setColor(Color.BLACK);
		g.drawPolygon(top);
		g.drawPolygon(side);
		g.drawPolygon(front);
		g.dispose();
		return icon;
	}

	protected BufferedImage generateViewmodel()
	{

		float res = Main.Frame.getHeight() / 900f;
		int size = (int) (res * 250);
		BufferedImage icon = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		Graphics g = icon.createGraphics();
		Point[] p = new Point[]
		{ new Point(0, (int) (res * 100)), new Point((int) (res * 90), (int) (res * 50)),
				new Point((int) (res * 200), (int) (res * 20)), new Point((int) (res * 100), (int) (res * 70)),
				new Point((int) (res * 50), (int) (res * 230)), new Point((int) (res * 160), (int) (res * 200)),
				new Point(size, (int) (res * 150)) };
		Polygon top = new Polygon(new int[]
		{ p[0].x, p[1].x, p[2].x, p[3].x }, new int[]
		{ p[0].y, p[1].y, p[2].y, p[3].y }, 4);
		Polygon front = new Polygon(new int[]
		{ p[0].x, p[3].x, p[5].x, p[4].x }, new int[]
		{ p[0].y, p[3].y, p[5].y, p[4].y }, 4);
		Polygon side = new Polygon(new int[]
		{ p[2].x, p[3].x, p[5].x, p[6].x }, new int[]
		{ p[2].y, p[3].y, p[5].y, p[6].y }, 4);

		if (Polygons.get(0).s.color)
		{ // TOP
			g.setColor(Polygons.get(0).s.c.getColor());
			g.fillPolygon(top);
		} else
		{
			g.setClip(top);
			Rectangle bounds = top.getBounds();
			g.drawImage(Polygons.get(0).s.Texture, bounds.x, bounds.y, bounds.width, bounds.height, null);
			g.setClip(null);
		}

		if (Polygons.get(3).s.color)
		{ // NORTH
			g.setColor(Polygons.get(3).s.c.getColor());
			g.fillPolygon(front);
		} else
		{
			g.setClip(front);
			Rectangle bounds = front.getBounds();
			g.drawImage(Polygons.get(3).s.Texture, bounds.x, bounds.y, bounds.width, bounds.height, null);
			g.setClip(null);
		}

		if (Polygons.get(4).s.color)
		{ // WEST
			g.setColor(Polygons.get(4).s.c.getColor());
			g.fillPolygon(side);
		} else
		{
			g.setClip(side);
			Rectangle bounds = side.getBounds();
			g.drawImage(Polygons.get(4).s.Texture, bounds.x, bounds.y, bounds.width, bounds.height, null);
			g.setClip(null);
		}
		g.setColor(Color.BLACK);
		g.drawPolygon(top);
		g.drawPolygon(side);
		g.drawPolygon(front);
		g.dispose();
		return icon;
	}

	/*void addBlock(Block b, boolean overwrite)
	{
		Engine.world.addBlock(b, overwrite);
	}*/

	/*void deleteBlock(Block b)
	{
		Engine.world.destroyBlock(b);
	}*/

	void generate(String name, int x, int y, int z, Surface top, Surface bottom, Surface north, Surface south,
			Surface east, Surface west, float xscale, float yscale, float zscale)
	{
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

		Vertex xnykzn = new Vertex(xn, yk, zn, 0, 0);
		Vertex xnynzn = new Vertex(xn, yn, zn, 0, h);
		Vertex xkynzn = new Vertex(xk, yn, zn, w, h);
		Vertex xkykzn = new Vertex(xk, yk, zn, w, 0);

		Vertex xkykzk = new Vertex(xk, yk, zk, 0, 0);
		Vertex xkynzk = new Vertex(xk, yn, zk, 0, h);
		Vertex xnynzk = new Vertex(xn, yn, zk, w, h);
		Vertex xnykzk = new Vertex(xn, yk, zk, w, 0);

		Polygons.add(new Polygon3D(new Vertex[]	{ xnykzn, xnynzn, xkynzn, xkykzn }, top));
		Polygons.add(new Polygon3D(new Vertex[]	{ xkykzk, xkynzk, xnynzk, xnykzk }, bottom));
		Polygons.add(new Polygon3D(new Vertex[]	{ xkynzk, xkynzn, xnynzn, xnynzk }, north));
		Polygons.add(new Polygon3D(new Vertex[]	{ xnykzk, xnykzn, xkykzn, xkykzk }, south));
		Polygons.add(new Polygon3D(new Vertex[]	{ xnykzk, xnynzk, xnynzn, xnykzn }, east));
		Polygons.add(new Polygon3D(new Vertex[]	{ xkykzn, xkynzn, xkynzk, xkykzk }, west));

		/*
		 * Polygons.add(new Polygon3D(new float[]{xn,xn,xk,xk}, new
		 * float[]{yk,yn,yn,yk}, new float[]{zn,zn,zn,zn}, top)); Polygons.add(new
		 * Polygon3D(new float[]{xk,xk,xn,xn}, new float[]{yk,yn,yn,yk}, new
		 * float[]{zk,zk,zk,zk},bottom)); Polygons.add(new Polygon3D(new
		 * float[]{xk,xk,xn,xn}, new float[]{yn,yn,yn,yn}, new float[]{zk,zn,zn,zk},
		 * north)); Polygons.add(new Polygon3D(new float[]{xn,xn,xk,xk}, new
		 * float[]{yk,yk,yk,yk}, new float[]{zk,zn,zn,zk}, south)); Polygons.add(new
		 * Polygon3D(new float[]{xn,xn,xn,xn}, new float[]{yk,yn,yn,yk}, new
		 * float[]{zk,zk,zn,zn}, east)); Polygons.add(new Polygon3D(new
		 * float[]{xk,xk,xk,xk}, new float[]{yk,yn,yn,yk}, new float[]{zn,zn,zk,zk},
		 * west));
		 */

		/*
		 * Polygons.get(0).visibleFrom = BlockFace.TOP; Polygons.get(1).visibleFrom =
		 * BlockFace.BOTTOM; Polygons.get(2).visibleFrom = BlockFace.NORTH;
		 * Polygons.get(3).visibleFrom = BlockFace.SOUTH; Polygons.get(4).visibleFrom =
		 * BlockFace.EAST; Polygons.get(5).visibleFrom = BlockFace.WEST;
		 */

		HitboxPolygons.put(Polygons.get(0), BlockFace.TOP);
		HitboxPolygons.put(Polygons.get(1), BlockFace.BOTTOM);
		HitboxPolygons.put(Polygons.get(2), BlockFace.NORTH);
		HitboxPolygons.put(Polygons.get(3), BlockFace.SOUTH);
		HitboxPolygons.put(Polygons.get(4), BlockFace.EAST);
		HitboxPolygons.put(Polygons.get(5), BlockFace.WEST);

		this.name = name;

		if (!Main.Items.containsKey(name))
		{
			Main.Items.put(name,
					new ItemType(name,
							Main.ModRegistry.contains(name) ? name : "ml.sakii.factoryisland.blocks." + name + "Block",
							generateIcon(), generateViewmodel()));
			/*
			 * Main.ItemTextures.put(name, generateIcon()); Main.ViewmodelTextures.put(name,
			 * generateViewmodel());
			 */

		}
	}

	HashMap<BlockFace, Block> get6Blocks(Block center, boolean includeNothing)
	{

		return Engine.world.get6Blocks(center, includeNothing);

	}

	Block getBlockAt(int x, int y, int z)
	{
		return Engine.world.getBlockAt(x, y, z);
	}

	BlockFace getPreviousTarget()
	{
		return BlockFace.values[Integer.parseInt(this.BlockMeta.get("previousTarget"))];

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

	private TreeMap<Integer, BlockFace> powersSorted()
	{
		TreeMap<Integer, BlockFace> result = new TreeMap<>();
		for (Entry<BlockFace, Integer> entry : powers.entrySet())
		{
			result.put(entry.getValue(), entry.getKey());
		}

		return result;

	}

}
