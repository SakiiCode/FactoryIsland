package ml.sakii.factoryisland.blocks;

import java.awt.GradientPaint;
import java.awt.geom.Point2D;
import ml.sakii.factoryisland.Color4;
import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Main;
import ml.sakii.factoryisland.Polygon3D;
import ml.sakii.factoryisland.Surface;
import ml.sakii.factoryisland.Vector;
import ml.sakii.factoryisland.Vertex;

public abstract class SimpleMachineConsumer extends PowerConsumer
		implements InteractListener, PlaceListener, TextureListener, LoadListener, MetadataListener
{
	public Polygon3D TargetPolygon;
	private Color4 side;
	private Color4 front;
	private Color4 active;
	
	
	public SimpleMachineConsumer(String name, int x, int y, int z, Color4 side, Color4 front, Color4 active, Color4 hole, GameEngine engine)
	{
		super(name, x, y, z,1,1,1, engine);

		this.side=side;
		this.front=front;
		this.active=active;
		TargetPolygon = new Polygon3D(new Vertex[]
				{ new Vertex(new Vector()), new Vertex(new Vector()), new Vertex(new Vector()),
						new Vertex(new Vector()) },new int[][] {{0,0},{1,0},{1,1},{0,1}}, new Surface(hole));
		Polygons.add(TargetPolygon);
		BlockMeta.put("target", BlockFace.TOP.id + "");

		updateTargetColors(BlockFace.TOP,BlockFace.TOP);
	}
	

	
	@Override
	public void interact(BlockFace target) { 
		setMetadata("target", target.id + "", true);
	}


	private void updateTargetColors(BlockFace prevTarget, BlockFace newTarget){ //mindenkepp inaktiv lesz, mert nem lehet ugy kattintani h viz fele alljon
		Polygons.get(prevTarget.id).s.c = side;
		Polygons.get(prevTarget.id).s.paint=true;
		Polygons.get(prevTarget.id).recalcLightedColor();
		Polygons.get(prevTarget.opposite).s.paint = true;
		Polygons.get(prevTarget.opposite).s.c = side;
		Polygons.get(prevTarget.opposite).recalcLightedColor();

		Polygons.get(newTarget.id).s.paint = false;
		Polygons.get(newTarget.id).s.c = front;
		Polygons.get(newTarget.id).recalcLightedColor();
		Polygons.get(newTarget.opposite).s.paint = false;
		Polygons.get(newTarget.opposite).s.c = side;
		Polygons.get(newTarget.opposite).recalcLightedColor();
		recalcTargetPolygon(newTarget);

	}
	
	private void updateActiveColors() {
		for(int i=0;i<6;i++){
			if(i != getTarget().id){
				if(isActive()){
					Polygons.get(i).s.c = active;
				}else {
					Polygons.get(i).s.c = side;
				}
				Polygons.get(i).recalcLightedColor();
			}
		}
	}
	
	private boolean isActive() {
		return Integer.parseInt(BlockMeta.get("active"))>0;
	}
	
	

	@Override
	public void updateTexture(Vector v){
		
		BlockFace target = getTarget();
		
		for(BlockFace nearby : target.getNearby()){
			 	
			Point2D[] values = GradientCalculator.getGradientOf(x, y, z, nearby, target, new Vector());
			Point2D begin1 = values[0];
			Point2D begin = values[1];
			Point2D end = values[2];
			
			Point2D endPerp = GradientCalculator.getPerpendicular(begin1, begin, begin, begin.distance(end));
				
			Polygons.get(nearby.id).s.p = new GradientPaint(begin, this.front.getColor(), endPerp,Main.TRANSPARENT);
		 }
		
	}
	
	private void recalcTargetPolygon(BlockFace target) {
		switch (target)
		{

		case TOP:

			TargetPolygon.Vertices[2].set(x + 0.2f, y + 0.2f, z + 1);
			TargetPolygon.Vertices[3].set(x + 0.2f, y + 0.8f, z + 1);
			TargetPolygon.Vertices[0].set(x + 0.8f, y + 0.8f, z + 1);
			TargetPolygon.Vertices[1].set(x + 0.8f, y + 0.2f, z + 1);

			break;
		case BOTTOM:

			TargetPolygon.Vertices[3].set(x + 0.2f, y + 0.2f, z);
			TargetPolygon.Vertices[2].set(x + 0.2f, y + 0.8f, z);
			TargetPolygon.Vertices[1].set(x + 0.8f, y + 0.8f, z);
			TargetPolygon.Vertices[0].set(x + 0.8f, y + 0.2f, z);

			break;
		case NORTH:

			TargetPolygon.Vertices[3].set(x + 0.2f, y + 1, z + 0.2f);
			TargetPolygon.Vertices[2].set(x + 0.2f, y + 1, z + 0.8f);
			TargetPolygon.Vertices[1].set(x + 0.8f, y + 1, z + 0.8f);
			TargetPolygon.Vertices[0].set(x + 0.8f, y + 1, z + 0.2f);

			break;
		case SOUTH:

			TargetPolygon.Vertices[2].set(x + 0.2f, y, z + 0.2f);
			TargetPolygon.Vertices[3].set(x + 0.2f, y, z + 0.8f);
			TargetPolygon.Vertices[0].set(x + 0.8f, y, z + 0.8f);
			TargetPolygon.Vertices[1].set(x + 0.8f, y, z + 0.2f);

			break;
		case EAST:

			TargetPolygon.Vertices[3].set(x + 1, y + 0.2f, z + 0.2f);
			TargetPolygon.Vertices[2].set(x + 1, y + 0.8f, z + 0.2f);
			TargetPolygon.Vertices[1].set(x + 1, y + 0.8f, z + 0.8f);
			TargetPolygon.Vertices[0].set(x + 1, y + 0.2f, z + 0.8f);

			break;
		case WEST:

			TargetPolygon.Vertices[2].set(x, y + 0.2f, z + 0.2f);
			TargetPolygon.Vertices[3].set(x, y + 0.8f, z + 0.2f);
			TargetPolygon.Vertices[0].set(x, y + 0.8f, z + 0.8f);
			TargetPolygon.Vertices[1].set(x, y + 0.2f, z + 0.8f);

			break;
		case NONE:Main.err("Target:NONE");
			break;

		}


		TargetPolygon.recalc(new Vector());
	}
	

	@Override
	public void placed(BlockFace SelectedFace) {
		BlockFace target = SelectedFace.getOpposite();
		setMetadata("target", target.id+"", true);
	}
	
	@Override
	public boolean onMetadataUpdate(String key, String value){
		if(key.equals("target")){
			updateTargetColors(getTarget(),BlockFace.values[Integer.parseInt(value)]);
			BlockMeta.put(key, value);
			return true;
		}
		if(key.equals("active")){
			BlockMeta.put(key, value);
			updateActiveColors();
			return true;
		}
		return false;

	}

	@Override
	public void onLoad(){
		updateTargetColors(BlockFace.TOP,getTarget());
		updateActiveColors();
	}
	

	

	
	
	


}
