package ml.sakii.factoryisland.entities;

import ml.sakii.factoryisland.EAngle;
import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Main;
import ml.sakii.factoryisland.Object3D;
import ml.sakii.factoryisland.Polygon3D;
import ml.sakii.factoryisland.Text3D;
import ml.sakii.factoryisland.Vector;
import ml.sakii.factoryisland.Vertex;

public class Alien extends Entity {

	float[] fxy, fxy1, fx1y1, fx1y;
	public Vector target;
	public boolean locked=false;
	
	public Alien(Vector ViewFrom, EAngle aim, String name,long ID, GameEngine engine){
		super("Alien",ViewFrom, aim, name,ID, engine);
		//showName=false;
		//this.ViewFrom = ViewFrom;
		float yaw2=(float) (-aim.yaw +Math.PI/2);
		this.target=new Vector().set(ViewFrom);
		
		float x = ViewFrom.x;
		float y = ViewFrom.y;
		float z = ViewFrom.z;
		
		float z0 = z-1.7f;

		
		
		fxy = rotateCoordinates(x-0.5f, y, x, y, yaw2);
		fxy1 = rotateCoordinates(x-0.5f, y+1f, x, y, yaw2);
		fx1y1 = rotateCoordinates(x+0.5f, y+1f, x, y, yaw2);
		fx1y = rotateCoordinates(x+0.5f,y, x, y, yaw2);
		
		Vertex x1yz0 = new Vertex(new Vector(fx1y, z0), 0, 0);
		Vertex x1y1z0 = new Vertex(new Vector(fx1y1, z0), 0, 0);
		Vertex xyz0 = new Vertex(new Vector(fxy, z0), 0, 0);
		Vertex xy1z0 = new Vertex(new Vector(fxy1, z0), 0, 0);
		
		Vertex x1yz1 = new Vertex(new Vector(fx1y, z), 0, 0);
		Vertex x1y1z1 = new Vertex(new Vector(fx1y1, z), 0, 0);
		Vertex xyz1 = new Vertex(new Vector(fxy, z), 0, 0);
		Vertex xy1z1 = new Vertex(new Vector(fxy1, z), 0, 0);
		
		
		Vertices.add(x1yz0);
		Vertices.add(x1y1z0);
		Vertices.add(xyz0);
		Vertices.add(xy1z0);
		
		Vertices.add(x1yz1);
		Vertices.add(x1y1z1);
		Vertices.add(xyz1);
		Vertices.add(xy1z1);
		
		
		//top
		Objects.add(new Polygon3D(new Vertex[] {x1yz1, x1y1z1, xy1z1, xyz1}, Main.alienSide));
		//bottom
		Objects.add(new Polygon3D(new Vertex[] {x1yz0, xyz0, xy1z0, x1y1z0}, Main.alienSide));
		//left
		Objects.add(new Polygon3D(new Vertex[] {xyz1, xy1z1, xy1z0, xyz0}, Main.alienSide));
		//right
		Objects.add(new Polygon3D(new Vertex[] {x1y1z0, x1y1z1, x1yz1, x1yz0}, Main.alienFront));
		//back
		Objects.add(new Polygon3D(new Vertex[] {xy1z1, x1y1z1, x1y1z0, xy1z0}, Main.alienSide));
		//front
		Objects.add(new Polygon3D(new Vertex[] {x1yz1, xyz1, xyz0, x1yz0}, Main.alienSide));
		
		//Objects.add(new Text3D("Alien", x, y, z));
	}
	
	private static float[] rotateCoordinates(float x, float y,float centerX, float centerY, float angle){
		float newX = (float)(   ((x-centerX)*Math.cos(angle) - (y-centerY)*Math.sin(angle)) + centerX   );
		float newY = (float)(   ((x-centerX)*Math.sin(angle) + (y-centerY)*Math.cos(angle)) + centerY   );
		return new float[]{newX, newY};
	}
	
	@Override
	public void update(){
		//yaw += Math.toRadians(90);
		//yaw = -yaw;
		float yaw2 = (float) (-ViewAngle.yaw +Math.PI/2);

		float x = ViewFrom.x;
		float y = ViewFrom.y;
		float z = ViewFrom.z;
		
		float z0 = z-1.7f;	
		
		fxy = rotateCoordinates(x-1f, y-0.5f, x, y, yaw2);
		fxy1 = rotateCoordinates(x-1f, y+0.5f, x, y, yaw2);
		fx1y1 = rotateCoordinates(x, y+0.5f, x, y, yaw2);
		fx1y = rotateCoordinates(x,y-0.5f, x, y, yaw2);
		
		
		
		
		Vertices.get(0).pos.set(fx1y, z0);
		Vertices.get(1).pos.set(fx1y1, z0);
		Vertices.get(2).pos.set(fxy, z0);
		Vertices.get(3).pos.set(fxy1, z0);
		Vertices.get(4).pos.set(fx1y, z);
		Vertices.get(5).pos.set(fx1y1, z);
		Vertices.get(6).pos.set(fxy, z);
		Vertices.get(7).pos.set(fxy1, z);
		
		for(Object3D p : Objects) {
			if(p instanceof Polygon3D) {
				((Polygon3D)p).recalcNormal();
				((Polygon3D)p).recalcCentroid();
			}else {
				((Text3D)p).location.set(ViewFrom);				
			}
		}
		
		/*for(Polygon3D p : Polygons) {
			p.update();
		}*/
		
	}
}
