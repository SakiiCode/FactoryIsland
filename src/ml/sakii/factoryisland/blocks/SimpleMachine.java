package ml.sakii.factoryisland.blocks;

import java.awt.GradientPaint;
import java.awt.geom.Point2D;

import ml.sakii.factoryisland.Color4;
import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Polygon3D;
import ml.sakii.factoryisland.Surface;
import ml.sakii.factoryisland.Vector;
import ml.sakii.factoryisland.Vertex;

public class SimpleMachine extends Block
		implements InteractListener, PlaceListener, PowerListener, TextureListener, LoadListener, MetadataListener
{
	public Polygon3D TargetPolygon;
	private Color4 side=new Color4();
	private Color4 front=new Color4();
	private Color4 active=new Color4();

	public SimpleMachine(String name, int x, int y, int z, Color4 side, Color4 front, Color4 active, Color4 target, GameEngine engine)
	{
		super(name, x, y, z,
				new Surface(side, new GradientPaint(0, 0, front.getColor(), 0, 0, side.getColor())),
				new Surface(side, new GradientPaint(0, 0, front.getColor(), 0, 0, side.getColor())),
				new Surface(side, new GradientPaint(0, 0, front.getColor(), 0, 0, side.getColor())),
				new Surface(side, new GradientPaint(0, 0, front.getColor(), 0, 0, side.getColor())),
				new Surface(side, new GradientPaint(0, 0, front.getColor(), 0, 0, side.getColor())),
				new Surface(side, new GradientPaint(0, 0, front.getColor(), 0, 0, side.getColor())), engine);
		BlockMeta.put("target", BlockFace.TOP.id + "");
		BlockMeta.put("powered", "0");
		this.side.set(side);
		this.front.set(front);
		this.active.set(active);

		TargetPolygon = new Polygon3D(new Vertex[]
		{ new Vertex(new Vector()), new Vertex(new Vector()), new Vertex(new Vector()),
				new Vertex(new Vector()) },new int[][] {{0,0},{0,0},{0,0},{0,0}}, new Surface(target));
		
		Polygons.add(TargetPolygon);
		
		

		setTarget(BlockFace.TOP);
		
	}

	@Override
	public void interact(BlockFace target)
	{
		setMetadata("target", target.id + "", true);

	}


	@SuppressWarnings("incomplete-switch")
	private void setTarget(BlockFace newTarget)
	{
		BlockFace target = getTarget();
		
		Polygons.get(target.id).s.c.set(side);
		Polygons.get(target.id).s.paint = true;
		Polygons.get(target.id).recalcLightedColor();
		
		Polygons.get(target.opposite).s.c.set(side);
		Polygons.get(target.opposite).s.paint = true;
		Polygons.get(target.opposite).recalcLightedColor();

		Polygons.get(newTarget.id).s.c.set(front);
		Polygons.get(newTarget.id).s.paint = false;
		Polygons.get(newTarget.id).recalcLightedColor();

		Polygons.get(newTarget.opposite).s.c.set(side);
		Polygons.get(newTarget.opposite).s.paint = false;
		Polygons.get(newTarget.opposite).recalcLightedColor();
		
		
		switch (newTarget)
		{

		case TOP:

			TargetPolygon.Vertices[1].pos.set(x + 0.2f, y + 0.2f, z + 1);
			TargetPolygon.Vertices[0].pos.set(x + 0.2f, y + 0.8f, z + 1);
			TargetPolygon.Vertices[3].pos.set(x + 0.8f, y + 0.8f, z + 1);
			TargetPolygon.Vertices[2].pos.set(x + 0.8f, y + 0.2f, z + 1);

			break;
		case BOTTOM:

			TargetPolygon.Vertices[0].pos.set(x + 0.2f, y + 0.2f, z);
			TargetPolygon.Vertices[1].pos.set(x + 0.2f, y + 0.8f, z);
			TargetPolygon.Vertices[2].pos.set(x + 0.8f, y + 0.8f, z);
			TargetPolygon.Vertices[3].pos.set(x + 0.8f, y + 0.2f, z);

			break;
		case NORTH:

			TargetPolygon.Vertices[0].pos.set(x + 0.2f, y + 1, z + 0.2f);
			TargetPolygon.Vertices[1].pos.set(x + 0.2f, y + 1, z + 0.8f);
			TargetPolygon.Vertices[2].pos.set(x + 0.8f, y + 1, z + 0.8f);
			TargetPolygon.Vertices[3].pos.set(x + 0.8f, y + 1, z + 0.2f);

			break;
		case SOUTH:

			TargetPolygon.Vertices[1].pos.set(x + 0.2f, y, z + 0.2f);
			TargetPolygon.Vertices[0].pos.set(x + 0.2f, y, z + 0.8f);
			TargetPolygon.Vertices[3].pos.set(x + 0.8f, y, z + 0.8f);
			TargetPolygon.Vertices[2].pos.set(x + 0.8f, y, z + 0.2f);

			break;
		case EAST:

			TargetPolygon.Vertices[0].pos.set(x + 1, y + 0.2f, z + 0.2f);
			TargetPolygon.Vertices[1].pos.set(x + 1, y + 0.8f, z + 0.2f);
			TargetPolygon.Vertices[2].pos.set(x + 1, y + 0.8f, z + 0.8f);
			TargetPolygon.Vertices[3].pos.set(x + 1, y + 0.2f, z + 0.8f);

			break;
		case WEST:

			TargetPolygon.Vertices[1].pos.set(x, y + 0.2f, z + 0.2f);
			TargetPolygon.Vertices[0].pos.set(x, y + 0.8f, z + 0.2f);
			TargetPolygon.Vertices[3].pos.set(x, y + 0.8f, z + 0.8f);
			TargetPolygon.Vertices[2].pos.set(x, y + 0.2f, z + 0.8f);

			break;

		}

		/*TargetPolygon.recalcNormal();
		TargetPolygon.recalcCentroid();*/
		TargetPolygon.recalc(new Vector());
		
		
	}

	@Override
	public void updateTexture(Vector tmp)
	{
		BlockFace target = getTarget();
		//target = target==BlockFace.NONE?BlockFace.TOP:target;
		
		for (BlockFace nearby : target.getNearby())
		{

			Point2D[] values = GradientCalculator.getGradientOf(x, y, z, nearby, target, tmp);
			Point2D begin1 = values[0];
			Point2D begin = values[1];
			Point2D end = values[2];

			Point2D endPerp = GradientCalculator.getPerpendicular(begin1, begin, begin, begin.distance(end));
			String level = BlockMeta.get("active");
			if(level ==null || Integer.valueOf(level)==0) {
				Polygons.get(nearby.id).s.p = new GradientPaint(begin, front.getColor(), endPerp, side.getColor());
				Polygons.get(nearby.id).s.paint=true;
			}else {
				Polygons.get(nearby.id).s.p = new GradientPaint(begin, front.getColor(), endPerp, active.getColor());
				Polygons.get(nearby.id).s.paint=true;
			}
			
		}

		
	}

	@Override
	public void placed(BlockFace SelectedFace)
	{
		BlockFace target = SelectedFace.getOpposite();
		setMetadata("target", target.id + "", true);

	}



	@Override
	public boolean onMetadataUpdate(String key, String value)
	{
		if (key.equals("target"))
		{
			setTarget(BlockFace.values[Integer.parseInt(value)]);
		}
		
		return false;
	}


	
	@Override
	public void onLoad()
	{
		
		for(int i=0;i<6;i++) {
			Polygon3D poly=Polygons.get(i);
			poly.s.c.set(side);
			poly.s.paint = false;
			poly.recalcLightedColor();
		}
		setTarget(getTarget());
	}

	@Override
	public void addPower(int power, BlockFace relativeFrom)
	{
		powers.put(relativeFrom, power);
		work();
		setMetadata("powered", getCharge() + "", true);

	}

	@Override
	public void removePower(BlockFace relativeFrom)
	{
		powers.remove(relativeFrom);
		setMetadata("powered", getCharge() + "", true);

	}
	
	
	void work()
	{

	}


}
