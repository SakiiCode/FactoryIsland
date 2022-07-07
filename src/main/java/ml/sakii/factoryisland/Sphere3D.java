package ml.sakii.factoryisland;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Sphere3D extends Object3D{

	public ArrayList<SpherePolygon3D> Polygons = new ArrayList<>();
	
	private Surface surface;
	private float radius;
	private GameEngine engine;
	private Vector pos;
	private float centerDist;
	
	public Sphere3D(String name, Vector pos, float radius, int resolution, Surface s, Model m, GameEngine engine) {
		
		
		this.surface=s;
		this.pos=pos;
		this.engine=engine;
		this.radius=radius;
		
		Vertex top = new Vertex(pos.cpy().add(Vector.Z.cpy().multiply(radius)));
		Vertex bottom = new Vertex(pos.cpy().add(Vector.Z.cpy().multiply(-radius)));
		
		Vertex[][] points = new Vertex[resolution-1][resolution];
		float delta = 180f/resolution;
		
		
		EAngle generator = new EAngle();
		
		generator.pitch = -90+delta;
		for(int i=0;i<resolution-1;i++) {
			generator.yaw = 0;
			for(int j=0;j<resolution;j++) {
				generator.yaw += 2*delta;
				Main.log(generator);
				points[i][j] = new Vertex(pos.cpy().add(generator.toVector().multiply(radius)));
			}
			generator.pitch += delta;
		}
		
		for(int i=0;i<resolution;i++) {
			for(int j=0;j<resolution;j++) {
				if(i == 0) {
					Polygons.add(new SpherePolygon3D(
							new Vertex[] {
								points[i][j],
								points[i][(j+1)%resolution],
								bottom,
							},
							s,
							m, pos));
				}else if(i == resolution-1) {
					Polygons.add(new SpherePolygon3D(
							new Vertex[] {
								top,
								points[i-1][(j+1)%resolution],
								points[i-1][j]
							},
							s,
							m, pos));
				}else {
					Polygons.add(new SpherePolygon3D(
							new Vertex[] {
								points[i][j],
								points[i][(j+1)%resolution],
								points[i-1][(j+1)%resolution],
								points[i-1][j]
							},
							s,
							m, pos));
				}
			}
		}
		
		
		
		
	}
	
	@Override
	protected boolean update(Game game) {
		for(Polygon3D p : Polygons) {
			p.update(game);
		}
		centerDist = game.PE.getPos().distance(pos); 
		if(game.key[8]) {
			AvgDist = 4;
		}else {
			AvgDist = centerDist;
		}
		return true;
	}
	
	@Override
	protected void draw(BufferedImage FrameBuffer, Graphics g, Game game) {
		for(SpherePolygon3D p : Polygons) {
			if(p.isVisible()) {
				p.draw(FrameBuffer, g, game);
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
	
	
	class SpherePolygon3D extends Polygon3D {

		private Vector pos;
		private boolean visible;
		
		public SpherePolygon3D(Vertex[] vertices, Surface s, Model model, Vector pos) {
			super(vertices, new int[vertices.length][2], s, model);
			this.pos=pos;
		}
		
		@Override
		protected boolean update(Game game) {
			visible = super.update(game);
			if(!Config.useTextures) {
				if(game.key[8]) {
					AvgDist = 4;
				}else {
					AvgDist = game.PE.getPos().distance(pos);
				}
			}
			
			return visible;
		}
		
		@Override
		protected void draw(BufferedImage FrameBuffer, Graphics g, Game game) {
			g.setColor(s.c.getColor());
			g.fillPolygon(polygon);
		}
		
		boolean isVisible() {
			return visible;
		}
		
	}

}
