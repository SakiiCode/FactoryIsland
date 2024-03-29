package ml.sakii.factoryisland;

import java.awt.Graphics;
import java.util.ArrayList;

import ml.sakii.factoryisland.entities.PlayerMP;

public class Sphere3D extends Object3D implements BufferRenderable{

	public ArrayList<SpherePolygon3D> Polygons = new ArrayList<>();
	
	private Surface surface;
	private float radius;
	private Vector pos;
	private float centerDist;
	
	public Sphere3D(Vector pos, float radius, int resolution, Surface s, Model m) {
		
		this.surface=s;
		this.pos=pos;
		this.radius=radius;
		
		Vector top = Vector.Z.cpy().multiply(radius).add(pos);
		Vector bottom = Vector.Z.cpy().multiply(-radius).add(pos);
		
		Vector[][] points = new Vector[resolution-1][resolution];
		float delta = 180f/resolution;
		
		
		EAngle generator = new EAngle();
		
		generator.pitch = -90+delta;
		for(int i=0;i<resolution-1;i++) {
			generator.yaw = 0;
			for(int j=0;j<resolution;j++) {
				generator.yaw += 2*delta;
				points[i][j] = pos.cpy().add(generator.toVector().multiply(radius));
			}
			generator.pitch += delta;
		}
		
		for(int i=0;i<resolution;i++) {
			for(int j=0;j<resolution;j++) {
				if(i == 0) {
					Polygons.add(new SpherePolygon3D(
							new Vector[] {
								points[i][j],
								points[i][(j+1)%resolution],
								bottom,
							},
							s,
							m));
				}else if(i == resolution-1) {
					Polygons.add(new SpherePolygon3D(
							new Vector[] {
								top,
								points[i-1][(j+1)%resolution],
								points[i-1][j]
							},
							s,
							m));
				}else {
					Polygons.add(new SpherePolygon3D(
							new Vector[] {
								points[i][j],
								points[i][(j+1)%resolution],
								points[i-1][(j+1)%resolution],
								points[i-1][j]
							},
							s,
							m));
				}
			}
		}
		
		
		
		
	}
	
	@Override
	protected boolean update(UpdateContext context) {
		for(Polygon3D p : Polygons) {
			p.update(context);
		}
		centerDist = context.game.PE.getPos().distance(pos);
		if(context.game.key[8]) {
			AvgDist = 4;
		}else {
			AvgDist = centerDist;
		}
		return true;
	}
	
	@Override
	protected void draw(Graphics g, Game game) {
		for(SpherePolygon3D p : Polygons) {
			if(p.isVisible() && !isPlayerInside(game.PE)) {
				p.draw(g, game);
			}
		}
	}
	
	public Color4 getColor() {
		return surface.c;
	}
	
	public float getRadius() {
		return radius;
	}
	
	public float getCenterDist() {
		return centerDist;
	}
	
	public Vector getPos() {
		return pos;
	}
	
	public boolean isPlayerInside(PlayerMP PE) {
		return PE.ViewFrom.distance(getPos())<getRadius();
	}
	
	public boolean isModelInside(Model model) {
		return model.getPos().distance(getPos())<getRadius();
	}
	
	@Override
	public void drawToBuffer(TextureRenderThread context) {
		for(SpherePolygon3D p : Polygons) {
			if(p.isVisible()) {
				p.drawToBuffer(context);
			}
		}
	}
	
	private class SpherePolygon3D extends Polygon3D {
		
		private boolean visible;
		
		public SpherePolygon3D(Vector[] vertices, Surface s, Model model) {
			super(vertices, new int[vertices.length][2], s, model);
			if(model.Engine != null) {
				addSource(new Point3D((int)model.getPos().x, (int)model.getPos().y, (int)model.getPos().z), 15);
			}
		}
		
		@Override
		protected boolean update(UpdateContext context) {
			visible = super.update(context);
			if(!Config.useTextures) {
				if(context.game.key[8]) {
					AvgDist = 4;
				}else {
					AvgDist = context.game.PE.getPos().distance(pos);
				}
			}
			
			return visible;
		}
		
		@Override
		protected void draw(Graphics g, Game game) {
			g.setColor(s.c.getColor());
			g.fillPolygon(polygon);
		}
		
		boolean isVisible() {
			return visible;
		}
		
	}

}
