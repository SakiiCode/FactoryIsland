package ml.sakii.factoryisland.entities;

import java.util.Arrays;

import ml.sakii.factoryisland.EAngle;
import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.Main;
import ml.sakii.factoryisland.Object3D;
import ml.sakii.factoryisland.Polygon3D;
import ml.sakii.factoryisland.Text3D;
import ml.sakii.factoryisland.Vector;
import ml.sakii.factoryisland.Vertex;

public class PlayerMP extends Entity {
	public float[] ViewFrom = new float[3];
	public float yaw, yaw2;
	float[] fxy, fxy1, fx1y1, fx1y;
	
	public PlayerMP(Vector ViewFrom, EAngle aim, String name,long ID, GameEngine engine){
		super("PlayerMP",ViewFrom, aim, name,ID, engine);
		showName=true;
		//this.ViewFrom = ViewFrom;
		//this.yaw = yaw;
		yaw2=(float) (-Math.toRadians(yaw) +Math.PI/2);

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
		Objects.add(new Polygon3D(new Vertex[] {x1yz1, x1y1z1, xy1z1, xyz1}, Main.playerSide));
		//bottom
		Objects.add(new Polygon3D(new Vertex[] {x1yz0, xyz0, xy1z0, x1y1z0}, Main.playerSide));
		//left
		Objects.add(new Polygon3D(new Vertex[] {xyz1, xy1z1, xy1z0, xyz0}, Main.playerSide));
		//right
		Objects.add(new Polygon3D(new Vertex[] {x1y1z0, x1y1z1, x1yz1, x1yz0}, Main.playerFront));
		//back
		Objects.add(new Polygon3D(new Vertex[] {xy1z1, x1y1z1, x1y1z0, xy1z0}, Main.playerSide));
		//front
		Objects.add(new Polygon3D(new Vertex[] {x1yz1, xyz1, xyz0, x1yz0}, Main.playerSide));
		/*
		//top
		Polygons.add(new Polygon3D(new Vector[] {
				new Vector(x1y, z1),
				new Vector(x1y1, z1),
				new Vector(xy1, z1),
				new Vector(xy, z1)
		},Main.playerSide));
		
		//bottom
		Polygons.add(new Polygon3D(new Vector[] {
				new Vector(x1y, z0),
				new Vector(xy, z0),
				new Vector(xy1, z0),
				new Vector(x1y1, z0)
		},Main.playerSide));
		
		//left
		Polygons.add(new Polygon3D(new Vector[] {
				new Vector(xy, z1),
				new Vector(xy1, z1),
				new Vector(xy1, z0),
				new Vector(xy, z0)
		},Main.playerSide));
		
		//right
		Polygons.add(new Polygon3D(new Vector[] {
				new Vector(x1y1, z0),
				new Vector(x1y1, z1),
				new Vector(x1y, z1),
				new Vector(x1y, z0)
		},Main.playerSide));
		
		//back
		Polygons.add(new Polygon3D(new Vector[] {
				new Vector(xy1, z1),
				new Vector(x1y1, z1),
				new Vector(x1y1, z0),
				new Vector(xy1, z0)
		},Main.playerSide));
		
		
		//front
		Polygons.add(new Polygon3D(new Vector[] {
				new Vector(x1y, z1),
				new Vector(xy, z1),
				new Vector(xy, z0),
				new Vector(x1y, z0)
		},Main.playerFront));
		
		 */
		
		/*
		float[] xy = rotateCoordinates(x-1f, y-0.5f, x, y, yaw);
		float[] xy1 = rotateCoordinates(x-1f, y+0.5f, x, y, yaw);
		float[] x1y1 = rotateCoordinates(x, y+0.5f, x, y, yaw);
		float[] x1y = rotateCoordinates(x,y-0.5f, x, y, yaw);
		*/
		/*
		float xk=x+(0.5f-xscale/2);
		float xn=x+(0.5f+xscale/2);
		float yk=y+(0.5f-yscale/2);
		float yn=y+(0.5f+yscale/2);
		float zk=z+(0.5f-zscale/2);
		float zn=z+(0.5f+zscale/2);

		Polygons.add(new Polygon3D(new float[]{xn,xn,xk,xk}, new float[]{yk,yn,yn,yk}, new float[]{zn,zn,zn,zn}, top));
		Polygons.add(new Polygon3D(new float[]{xk,xk,xn,xn}, new float[]{yk,yn,yn,yk}, new float[]{zk,zk,zk,zk},bottom));
		Polygons.add(new Polygon3D(new float[]{xk,xk,xn,xn}, new float[]{yn,yn,yn,yn}, new float[]{zk,zn,zn,zk}, north));
		Polygons.add(new Polygon3D(new float[]{xn,xn,xk,xk}, new float[]{yk,yk,yk,yk}, new float[]{zk,zn,zn,zk}, south));
		Polygons.add(new Polygon3D(new float[]{xn,xn,xn,xn}, new float[]{yk,yn,yn,yk}, new float[]{zk,zk,zn,zn}, east));
		Polygons.add(new Polygon3D(new float[]{xk,xk,xk,xk}, new float[]{yk,yn,yn,yk}, new float[]{zn,zn,zk,zk}, west));
		*/
				
				
		/*		new float[]{xn,xn,xk,xk}, new float[]{yk,yn,yn,yk}, new float[]{zn,zn,zn,zn}, top));
		Polygons.add(new Polygon3D(new float[]{xk,xk,xn,xn}, new float[]{yk,yn,yn,yk}, new float[]{zk,zk,zk,zk},bottom));
		Polygons.add(new Polygon3D(new float[]{xk,xk,xn,xn}, new float[]{yn,yn,yn,yn}, new float[]{zk,zn,zn,zk}, north));
		Polygons.add(new Polygon3D(new float[]{xn,xn,xk,xk}, new float[]{yk,yk,yk,yk}, new float[]{zk,zn,zn,zk}, south));
		Polygons.add(new Polygon3D(new float[]{xn,xn,xn,xn}, new float[]{yk,yn,yn,yk}, new float[]{zk,zk,zn,zn}, east));
		Polygons.add(new Polygon3D(new float[]{xk,xk,xk,xk}, new float[]{yk,yn,yn,yk}, new float[]{zn,zn,zk,zk}, west));
		
		
		//top
		Polygons.add(new Polygon3D(new float[]{xy[0],xy1[0],x1y1[0],x1y[0]}, new float[]{xy[1],xy1[1],x1y1[1],x1y[1]}, new float[]{z,z,z,z}, Main.playerSide));

		//back
		Polygons.add(new Polygon3D(new float[]{xy[0],xy1[0],xy1[0],xy[0]}, new float[]{xy[1],xy1[1],xy1[1],xy[1]}, new float[]{z-1.7f,z-1.7f,z,z}, Main.playerSide));

		//right
		Polygons.add(new Polygon3D(new float[]{xy1[0],xy1[0],x1y1[0],x1y1[0]}, new float[]{xy1[1],xy1[1],x1y1[1],x1y1[1]}, new float[]{z-1.7f,z,z,z-1.7f}, Main.playerSide));

		//front
		Polygons.add(new Polygon3D(new float[]{x1y[0],x1y1[0],x1y1[0],x1y[0]}, new float[]{x1y[1],x1y1[1],x1y1[1],x1y[1]}, new float[]{z-1.7f,z-1.7f,z,z}, Main.playerFront));

		//back
		Polygons.add(new Polygon3D(new float[]{xy[0],xy[0],x1y[0],x1y[0]}, new float[]{xy[1],xy[1],x1y[1],x1y[1]}, new float[]{z-1.7f,z,z,z-1.7f}, Main.playerSide));

		//bottom
		Polygons.add(new Polygon3D(new float[]{xy[0],xy1[0],x1y1[0],x1y[0]}, new float[]{xy[1],xy1[1],x1y1[1],x1y[1]}, new float[]{z-1.7f,z-1.7f,z-1.7f,z-1.7f}, Main.playerSide));
		 */
		
		
		//game.Polygons.addAll(Polygons);
		Objects.add(new Text3D(name, x, y, z));
	}
	
	private static float[] rotateCoordinates(float x, float y,float centerX, float centerY, float angle){
		float newX = (float)(   ((x-centerX)*Math.cos(angle) - (y-centerY)*Math.sin(angle)) + centerX   );
		float newY = (float)(   ((x-centerX)*Math.sin(angle) + (y-centerY)*Math.cos(angle)) + centerY   );
		return new float[]{newX, newY};
	}
	
	public void update(){
		//yaw += Math.toRadians(90);
		yaw2=(float) (-Math.toRadians(yaw) +Math.PI/2);


		float x = ViewFrom[0];
		float y = ViewFrom[1];
		//float z = ViewFrom[2];
		fxy = rotateCoordinates(x-1f, y-0.5f, x, y, yaw2);
		fxy1 = rotateCoordinates(x-1f, y+0.5f, x, y, yaw2);
		fx1y1 = rotateCoordinates(x, y+0.5f, x, y, yaw2);
		fx1y = rotateCoordinates(x,y-0.5f, x, y, yaw2);
		float z = ViewFrom[2];
		
		float z0 = z-1.7f;	

			/*	
		Vertex x1yz0 = new Vertex(new Vector(fx1y, z0), 0, 0);
		Vertex x1y1z0 = new Vertex(new Vector(fx1y1, z0), 0, 0);
		Vertex xyz0 = new Vertex(new Vector(fxy, z0), 0, 0);
		Vertex xy1z0 = new Vertex(new Vector(fxy1, z0), 0, 0);
		
		Vertex x1yz1 = new Vertex(new Vector(fx1y, z), 0, 0);
		Vertex x1y1z1 = new Vertex(new Vector(fx1y1, z), 0, 0);
		Vertex xyz1 = new Vertex(new Vector(fxy, z), 0, 0);
		Vertex xy1z1 = new Vertex(new Vector(fxy1, z), 0, 0);
		
		
		*/
		
		Vertices.get(0).pos.set(fx1y, z0);
		Vertices.get(1).pos.set(fx1y1, z0);
		Vertices.get(2).pos.set(fxy, z0);
		Vertices.get(3).pos.set(fxy1, z0);
		Vertices.get(4).pos.set(fx1y, z);
		Vertices.get(5).pos.set(fx1y1, z);
		Vertices.get(6).pos.set(fxy, z);
		Vertices.get(7).pos.set(fxy1, z);
		/*//top
		Polygons.get(0).x = new float[]{xy[0],xy1[0],x1y1[0],x1y[0]};
		Polygons.get(0).y = new float[]{xy[1],xy1[1],x1y1[1],x1y[1]};
		Polygons.get(0).z = new float[]{z,z,z,z};

		//back
		Polygons.get(1).x = new float[]{xy[0],xy1[0],xy1[0],xy[0]};
		Polygons.get(1).y = new float[]{xy[1],xy1[1],xy1[1],xy[1]};
		Polygons.get(1).z = new float[]{z-1.7f,z-1.7f,z,z};

		//right
		Polygons.get(2).x = new float[]{xy1[0],xy1[0],x1y1[0],x1y1[0]};
		Polygons.get(2).y = new float[]{xy1[1],xy1[1],x1y1[1],x1y1[1]};
		Polygons.get(2).z = new float[]{z-1.7f,z,z,z-1.7f};

		//front
		Polygons.get(3).x = new float[]{x1y[0],x1y1[0],x1y1[0],x1y[0]};
		Polygons.get(3).y = new float[]{x1y[1],x1y1[1],x1y1[1],x1y[1]};
		Polygons.get(3).z = new float[]{z-1.7f,z-1.7f,z,z};

		//back
		Polygons.get(4).x = new float[]{xy[0],xy[0],x1y[0],x1y[0]};
		Polygons.get(4).y = new float[]{xy[1],xy[1],x1y[1],x1y[1]};
		Polygons.get(4).z = new float[]{z-1.7f,z,z,z-1.7f};

		//bottom
		Polygons.get(5).x = new float[]{xy[0],xy1[0],x1y1[0],x1y[0]};
		Polygons.get(5).y = new float[]{xy[1],xy1[1],x1y1[1],x1y[1]};
		Polygons.get(5).z = new float[]{z-1.7f,z-1.7f,z-1.7f,z-1.7f};
		*/
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
	
	@Override
	public String toString() {
		return ((Text3D)Objects.get(6)).text + "," + ((Text3D)Objects.get(6)).x + "," + ((Text3D)Objects.get(6)).y + "," + Arrays.toString(ViewFrom);
	}
}
