package ml.sakii.factoryisland.blocks;

import java.awt.GradientPaint;
import java.awt.geom.Point2D;
import ml.sakii.factoryisland.Color4;
import ml.sakii.factoryisland.Game;
import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Main;
import ml.sakii.factoryisland.Polygon3D;
import ml.sakii.factoryisland.Surface;
import ml.sakii.factoryisland.Vector;
import ml.sakii.factoryisland.Vector2D;
import ml.sakii.factoryisland.blocks.components.DynamicTextureComponent;
import ml.sakii.factoryisland.blocks.components.InteractComponent;
import ml.sakii.factoryisland.blocks.components.PlaceComponent;
import ml.sakii.factoryisland.blocks.components.WorldLoadComponent;

public abstract class SimpleMachine extends Block implements MetadataListener {

	private Polygon3D TargetPolygon;
	private Color4 side;
	private Color4 front;
	private Color4 active;
	
	private Vector[] tmpArr = new Vector[] {new Vector(),new Vector(), new Vector()};
	private Point2D.Float[] values = new Point2D.Float[] {new Point2D.Float(),new Point2D.Float(),new Point2D.Float()};
	private Vector2D lineVec = new Vector2D();
	private Vector2D pointVec = new Vector2D();
	
	protected InteractComponent ic;
	protected DynamicTextureComponent dtc;
	protected PlaceComponent pc;
	protected WorldLoadComponent wlc;
	
	
	public SimpleMachine(String name, int x, int y, int z, Color4 side, Color4 front, Color4 active, Color4 hole, GameEngine engine)
	{
		super(name, x, y, z,1,1,1, engine);

		this.side=side;
		this.front=front;
		this.active=active;
		TargetPolygon = new Polygon3D(new Vector[]
				{ new Vector(), new Vector(), new Vector(),
						new Vector() },new int[][] {{0,0},{1,0},{1,1},{0,1}}, new Surface(hole),this);
		Objects.add(TargetPolygon);
		HitboxPolygons.put(TargetPolygon, BlockFace.TOP);
		storeMetadata("target", BlockFace.TOP.id + "");
		storeMetadata("active", "0");
		
		if(engine != null) {
			updateTargetColors(BlockFace.TOP,BlockFace.TOP);
		}
		
		wlc = new WorldLoadComponent(this) {
			
			@Override
			public void onLoad(Game game) {
				updateTargetColors(BlockFace.TOP,getTarget());
				updateActiveColors();
			}
		};
		
		addComponent(wlc);
		
		dtc = new DynamicTextureComponent(this) {
			
			@Override
			public void updateTexture(Vector tmp, Game game) {
				BlockFace target = getTarget();
				
				for(BlockFace nearby : target.getNearby()){
					 	
					GradientCalculator.getGradientOf(x, y, z, nearby, target, tmpArr, values, game);
					
					GradientCalculator.getPerpendicular(values, pointVec, lineVec);
						
					((Polygon3D)Objects.get(nearby.id)).s.p = new GradientPaint(lineVec, SimpleMachine.this.front.getColor(), values[2], Color4.TRANSPARENT);
				 }
				
			}
		};
		
		addComponent(dtc);
		
		pc = new PlaceComponent(this) {
			@Override
			public void placed(BlockFace selectedFace) {
				BlockFace target = selectedFace.getOpposite();
				setMetadata("target", target.id+"", true);	
			}
		};
		
		addComponent(pc);
		
		ic = new InteractComponent(this) {
			
			@Override
			public void interact(BlockFace target) {
				setMetadata("target", target.id + "", true);				
			}
		};
		
		addComponent(ic);
		
	}
	


	private void updateTargetColors(BlockFace prevTarget, BlockFace newTarget){ //mindenkepp inaktiv lesz, mert nem lehet ugy kattintani h viz fele alljon
		
		Polygon3D prevTargetPoly = (Polygon3D)Objects.get(prevTarget.id);
		Polygon3D prevTargetOppositePoly = (Polygon3D)Objects.get(prevTarget.opposite);
		Polygon3D newTargetPoly = (Polygon3D)Objects.get(newTarget.id);
		Polygon3D newTargetOppositePoly = (Polygon3D)Objects.get(newTarget.opposite);
		
		
		prevTargetPoly.s.c = side;
		prevTargetPoly.s.paint=true;
		prevTargetPoly.recalcLightedColor();
		prevTargetOppositePoly.s.paint = true;
		prevTargetOppositePoly.s.c = side;
		prevTargetOppositePoly.recalcLightedColor();

		newTargetPoly.s.paint = false;
		newTargetPoly.s.c = front;
		newTargetPoly.recalcLightedColor();
		newTargetOppositePoly.s.paint = false;
		newTargetOppositePoly.s.c = side;
		newTargetOppositePoly.recalcLightedColor();
		recalcTargetPolygon(newTarget);

	}
	
	private void updateActiveColors() {
		for(int i=0;i<6;i++){
			if(i != getTarget().id){
				if(Objects.get(i) instanceof Polygon3D poly) {
					if(isActive()){
						poly.s.c = active;
					}else {
						poly.s.c = side;
					}
					poly.recalcLightedColor();
				}
			}
		}
	}
	
	private boolean isActive() {
		return Integer.parseInt(getMetadata("active"))>0;
	}
	
	private void recalcTargetPolygon(BlockFace target) {
		switch (target)
		{

		case TOP:

			TargetPolygon.Vertices[2].set(x + 0.2f, y + 0.2f, z + 1.01f);
			TargetPolygon.Vertices[3].set(x + 0.2f, y + 0.8f, z + 1.01f);
			TargetPolygon.Vertices[0].set(x + 0.8f, y + 0.8f, z + 1.01f);
			TargetPolygon.Vertices[1].set(x + 0.8f, y + 0.2f, z + 1.01f);

			break;
		case BOTTOM:

			TargetPolygon.Vertices[3].set(x + 0.2f, y + 0.2f, z-.01f);
			TargetPolygon.Vertices[2].set(x + 0.2f, y + 0.8f, z-.01f);
			TargetPolygon.Vertices[1].set(x + 0.8f, y + 0.8f, z-.01f);
			TargetPolygon.Vertices[0].set(x + 0.8f, y + 0.2f, z-.01f);

			break;
		case NORTH:

			TargetPolygon.Vertices[3].set(x + 0.2f, y + 1.01f, z + 0.2f);
			TargetPolygon.Vertices[2].set(x + 0.2f, y + 1.01f, z + 0.8f);
			TargetPolygon.Vertices[1].set(x + 0.8f, y + 1.01f, z + 0.8f);
			TargetPolygon.Vertices[0].set(x + 0.8f, y + 1.01f, z + 0.2f);

			break;
		case SOUTH:

			TargetPolygon.Vertices[2].set(x + 0.2f, y-.01f, z + 0.2f);
			TargetPolygon.Vertices[3].set(x + 0.2f, y-.01f, z + 0.8f);
			TargetPolygon.Vertices[0].set(x + 0.8f, y-.01f, z + 0.8f);
			TargetPolygon.Vertices[1].set(x + 0.8f, y-.01f, z + 0.2f);

			break;
		case EAST:

			TargetPolygon.Vertices[3].set(x + 1.01f, y + 0.2f, z + 0.2f);
			TargetPolygon.Vertices[2].set(x + 1.01f, y + 0.8f, z + 0.2f);
			TargetPolygon.Vertices[1].set(x + 1.01f, y + 0.8f, z + 0.8f);
			TargetPolygon.Vertices[0].set(x + 1.01f, y + 0.2f, z + 0.8f);

			break;
		case WEST:

			TargetPolygon.Vertices[2].set(x-.01f, y + 0.2f, z + 0.2f);
			TargetPolygon.Vertices[3].set(x-.01f, y + 0.8f, z + 0.2f);
			TargetPolygon.Vertices[0].set(x-.01f, y + 0.8f, z + 0.8f);
			TargetPolygon.Vertices[1].set(x-.01f, y + 0.2f, z + 0.8f);

			break;
		case NONE:Main.err("Target:NONE");
			break;

		}


		TargetPolygon.recalc();
		
		HitboxPolygons.put(TargetPolygon, target);

	}
	
	
	@Override
	public boolean onMetadataUpdate(String key, String value){
		if(key.equals("target")){

			updateTargetColors(getTarget(),BlockFace.values[Integer.parseInt(value)]);
			storeMetadata(key, value);

			if(Engine.game != null) {
				dtc.updateTexture(new Vector(), Engine.game);
			}
			return true;
		}
		if(key.equals("active")){
			storeMetadata(key, value);
			updateActiveColors();
			return true;
		}
		return false;

	}
	
	

}
