package ml.sakii.factoryisland.blocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import ml.sakii.factoryisland.Color4;
import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Model;
import ml.sakii.factoryisland.Object3D;
import ml.sakii.factoryisland.Point3D;
import ml.sakii.factoryisland.Polygon3D;
import ml.sakii.factoryisland.Surface;
import ml.sakii.factoryisland.Vector;
import ml.sakii.factoryisland.World;
import ml.sakii.factoryisland.blocks.components.Component;
import ml.sakii.factoryisland.blocks.components.TickUpdateComponent;

public abstract class Block extends Model.Int
{
	public static final Block NOTHING = new Nothing();
	public static final BlockDescriptor DEFAULT = new BlockDescriptor() {};
	
	public final HashMap<Polygon3D, BlockFace> HitboxPolygons = new HashMap<>();
	public final ArrayList<Object3D> Objects = new ArrayList<>(6);
	
	private final HashMap<String, String> BlockMeta = new HashMap<>();
	
	private final ArrayList<Component> Components = new ArrayList<>();
	
	
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
				
			if(isTransparent() || getLightLevel()>0) {
				continue;
			}
		
			BlockFace face = entry1.getValue();
			
			for(Entry<BlockFace, Block> entry : world.get6Blocks(pos.cpy().add(face), false).entrySet()) {
				if(entry.getValue().isTransparent() || entry.getValue().getLightLevel()>0) {
					continue;
				}
				
				BlockFace nearbyFace = entry.getKey();
				if(nearbyFace == face || nearbyFace == face.getOpposite()) {
					continue;
				}
				poly.addSimpleOcclusion(nearbyFace);
				
				
				
			}

		
			if(isTransparent() || getLightLevel()>0) {
				return;
			}
	
			for(Entry<Point3D, Block> entry : world.get4Blocks(pos, face, false).entrySet()) {
				Point3D delta = entry.getKey();
				if(isBlockingCorner(world.getBlockAtP(tmp.set(pos).add(face).add(delta.x,0,0))) || 
						isBlockingCorner(world.getBlockAtP(tmp.set(pos).add(face).add(0,delta.y,0))) || 
							isBlockingCorner(world.getBlockAtP(tmp.set(pos).add(face).add(0,0,delta.z)))) {
					continue;
				}
				if(entry.getValue().isTransparent() || entry.getValue().getLightLevel() > 0) {
					continue;
				}
				poly.addCornerOcclusion(Corner.fromDelta(face, delta));
			}
			
			
			poly.recalcTexturedOcclusions();
		}
	}
	
	private static boolean isBlockingCorner(Block b) {
		return !b.isTransparent() && b != Block.NOTHING;
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
			for(TickUpdateComponent tuc : this.getComponents(TickUpdateComponent.class)) {
				Engine.TickableBlocks.add(tuc);
			}
			for (Block b2 : Engine.world.get6Blocks(this, false).values())
			{
				if(Engine.client == null || (Engine.client != null && Engine.client != null)) {
					for(TickUpdateComponent tuc2 : b2.getComponents(TickUpdateComponent.class)) {
						Engine.TickableBlocks.add(tuc2);
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
	
	public GameEngine getEngine() {
		return Engine;
	}
	
	public World getWorld() {
		return Engine.world;
	}
	
	public void storeMetadata(String key, String value) {
		BlockMeta.put(key, value);
	}
	
	public String getMetadata(String key) {
		return BlockMeta.get(key);
	}
	
	public Set<Entry<String,String>> getAllMetadata(){
		return BlockMeta.entrySet();
	}
	
	@SuppressWarnings("static-method")
	public BlockDescriptor getDescriptor() {
		return DEFAULT;
	}
	
	public final List<String> getCanBePlacedOn(){
		return getDescriptor().getCanBePlacedOn();
	}
	public final int getLightLevel() {
		return getDescriptor().getLightLevel();
	}
	public final boolean isFullBlock() {
		return getDescriptor().isFullBlock();
	}
	public final boolean isTransparent() {
		return getDescriptor().isTransparent();
	}
	public final boolean isSolid() {
		return getDescriptor().isSolid();
	}
	public final int getRefreshRate() {
		return getDescriptor().getRefreshRate();
	}
	
	@SuppressWarnings("unchecked")
	public final <T extends Component> List<T> getComponents(Class<T> type) {
		ArrayList<T> results = new ArrayList<>();
		for(Component c : Components) {
			if(type.isAssignableFrom(c.getClass())) {
				results.add((T) c);
			}
		}
		return results;
	}
	
	public final void addComponent(Component c) {
		Components.add(c);
		if(!c.SubComponents.isEmpty()) {
			for(Component sc : c.SubComponents) {
				addComponent(sc);
			}
		}
	}
	
	public final List<Component> getComponents(){
		return Components;
	}

}
