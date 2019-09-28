package ml.sakii.factoryisland;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RadialGradientPaint;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import ml.sakii.factoryisland.blocks.Block;

public class Polygon3D extends Object3D{
	Polygon polygon = new Polygon();
	//float AvgDist;
	boolean adjecentFilter = true;
	boolean faceFilter = true;
	public boolean selected;
	private Vector normal=new Vector();
	Vector centroid=new Vector();
	public Surface s;
	
	
	public final Vertex[] Vertices;
	private final Vertex[] clip = new Vertex[20];
	private final Vertex[] clip2 = new Vertex[20];
	private int clipSize, clip2Size;
	final HashMap<Vertex, UVZ> TextureMap = new HashMap<>();
	
	HashMap<Integer, Integer> bufferXmin = new HashMap<>(Config.height);
	HashMap<Integer, Integer> bufferXmax = new HashMap<>(Config.height);
	
	HashMap<Integer, UVZ> bufferUVZmin = new HashMap<>(Config.height);
	HashMap<Integer, UVZ> bufferUVZmax = new HashMap<>(Config.height);
	
	private Plane tmpnear=new Plane();
	private Vector ViewToPoint=new Vector();
	private Vector CameraToTriangle = new Vector();
	private Vector tmp=new Vector();
	int ymax, ymin;
	private int light=0;
	
	private final HashMap<Block, Integer> lightSources = new HashMap<>();
	private Color4 lightedcolor = new Color4();
	private Color4 overlay=new Color4();
	Color4 pixel = new Color4();
	//Vector spawnpoint=new Vector();
	Point2D.Double centroid2D = new Point2D.Double();
	private float[] fractions = new float[]{0.5f,1.0f};
	private Color[] colors = new Color[]{new Color(0.0f,0.0f,0.0f,0.0f), Color.BLACK};
	//public static final HashMap<Vertex, Point> Cache = new HashMap<>();
	
	
	public Polygon3D(Vertex[] vertices, Surface s) {
		
		this.Vertices = vertices;
		
		this.s = s;
		for(int i=0;i<clip.length;i++) {
			clip[i]=new Vertex(Vertex.NULL);
		}
		for(int i=0;i<clip2.length;i++) {
			clip2[i]=new Vertex(Vertex.NULL);
		}
		
		recalc(new Vector());
		recalcLightedColor();
	}
	


	@Override
	boolean update(){
			
		// Ha bármelyik hamis, eltûnik. Csak akkor jelenik meg, ha az összes igaz.
			if(adjecentFilter) {
				if(!Main.GAME.locked) {
					if(Main.GAME.ViewBlock.Polygons.contains(this)) {
						faceFilter=true;
					}else {
						CameraToTriangle.set(centroid).substract(Main.GAME.PE.getPos());
						faceFilter = CameraToTriangle.DotProduct(normal) >= 0;
					}
				}
				
				
				if(faceFilter) {
					if(!Main.GAME.locked) {
						AvgDist = GetDist();
					}
					if(AvgDist<=Config.renderDistance && !isAllBehind()) {
						clearClip();
						//clip(Main.GAME.ViewFrustum.znear);
						clip(Main.GAME.ViewFrustum.sides[0]);
						clip(Main.GAME.ViewFrustum.sides[1]);
						clip(Main.GAME.ViewFrustum.sides[3]);
						clip(Main.GAME.ViewFrustum.sides[2]);
							
						if(Main.GAME.locked){
							tmpnear.normal.set(Main.GAME.ViewVector);
							tmpnear.normal.multiply(0.01f);
							tmpnear.normal.add(Main.GAME.PE.getPos());
							tmpnear.distance = Main.GAME.ViewVector.DotProduct(tmpnear.normal);
							tmpnear.normal.set(Main.GAME.ViewVector);
							clip(tmpnear);
						}
						
					
						if(clipSize>0){
							
							polygon.reset();

							
							for(int i=0;i<clipSize;i++) {
								Vertex v=clip[i];
								

								v.update();
								polygon.addPoint(v.proj.x, v.proj.y);

								
							}
							
							if(Config.useTextures) {
							
								ymin = clip[0].proj.y;
								ymax = clip[0].proj.y;
								for(int i=0;i<clipSize;i++) {
									Vertex v=clip[i];
									ymin = Math.min(ymin,v.proj.y);
									ymax = Math.max(ymax,v.proj.y);
								}
								
								ymin=Math.max(ymin, 0);
								ymax=Math.max(ymax, 0);
								ymin=Math.min(ymin, Main.GAME.FrameBuffer.getHeight());
								ymax=Math.min(ymax, Main.GAME.FrameBuffer.getHeight());
								
								if(ymin==ymax) {
									return false;
								}
							
							}
							return true;
	
						
						}
					}
				}
			}
			
			return false;
		}
	
	public void addSource(Block b, int intensity) {
		
		lightSources.put(b, intensity);
		
		recalcLightedColor();
	}
	
	void removeSource(Block b) {
		
		lightSources.remove(b);
		recalcLightedColor();
	}
	
	Integer checkSource(Block b) {
		return lightSources.get(b);
	}
	
	Set<Block> getSources(){
		return lightSources.keySet();
	}
	
	
	public Color4 getLightOverlay() {
		light=0;
		for(int intensity : lightSources.values()) {
			if(intensity>light) {
				light=intensity;
			}
		}
		return overlay.setAlpha((int)(Math.pow(0.8, light+1)*255));
	}
	
	public Color4 getLightedColor() {
		return lightedcolor;
	}
	
	public void recalcLightedColor() {
		lightedcolor.set(s.c).blend(getLightOverlay());
	}
	
	public int getLight() {
		return light;
	}
		



	@Override
	void draw(BufferedImage FrameBuffer, Graphics g){
		boolean lighted=false;
		if(s.color || !Config.useTextures || AvgDist > 25){

			if(!s.paint && !Config.useTextures) {
				g.setColor(lightedcolor.getColor());
				lighted=true;
			}else {
				g.setColor(s.c.getColor());
			}
			g.fillPolygon(polygon);
			
		}else{
			// buffer init
			bufferXmin.clear();
			bufferXmax.clear();
			bufferUVZmin.clear();
			bufferUVZmax.clear();
			

			//vertexrõl vertexre körbemegyünk
			for(int i=0;i<clipSize;i++) {
				
				Vertex v1 = clip[i]; 
				Vertex v2 = i != clipSize-1 ? clip[i+1] : clip[0];
				
				
				final Point p1 = v1.proj;
				final Point p2 = v2.proj;
				
				//p1 és p2 meredeksége
				final double m=(p2.y==p1.y) ? 0.0 : (p2.x-p1.x)*1.0/(p2.y-p1.y);
				
				// mindenképpen y pozitív irányban szeretnénk végigmenni a polygon oldalán, de lehet, hogy p2 van feljebb.
				final int xmin = (p1.y<p2.y) ? p1.x : p2.x;
				final int ymin= (p1.y<p2.y) ? p1.y : p2.y; // y határértékei adott szakaszon
				final int ymax = (p1.y<p2.y) ? p2.y : p1.y;

				
				


				double x  = xmin; //aktuális x érték
				
				// megkeressük az adott sor bal és jobb szélét, társítunk a két ponthoz UVZ-t is
				// y-t 1-gyel, x-et m-mel léptetjük
				for(int y=ymin;y<ymax;y++) {

					if(bufferXmin.get(y) == null || x < bufferXmin.get(y)) {
						bufferXmin.put(y,(int) x);
						bufferUVZmin.put(y, UVZ.interp(p1, p2, new Point((int) x, y), v1.uvz, v2.uvz));
					}
					
					if(bufferXmax.get(y) == null || x > bufferXmax.get(y)) {
						bufferXmax.put(y,(int) x);
						bufferUVZmax.put(y, UVZ.interp(p1, p2, new Point((int) x, y), v1.uvz, v2.uvz));
					}
					
					x+=m;
						
				}
					
				
				

			}
		
			//int imgx = Collections.min(bufferXmin.values());
			int imgw = Collections.max(bufferXmax.values()) -  Collections.min(bufferXmin.values());
			//int imgy = ymin;
			//int imgh = ymax-ymin;
			
			
			g.setColor(Color.BLACK);
			if(imgw>0) { //ha merõlegesen állunk ne rajzoljon
				
				// átmeneti kép, erre rajzoljuk a polygont, és ezt rajzoljuk az ablakra

				
				//a polygon minden során végigmegyünk
				for(int y=ymin;y<ymax;y++)
				{
					//a polygon adott sorának két szélének adatai
					int xmin=bufferXmin.get(y);
					int xmax=bufferXmax.get(y);
					UVZ uvzmin = bufferUVZmin.get(y);
					UVZ uvzmax = bufferUVZmax.get(y);
				 


					double Siz = Util.getSlope(xmin, xmax, uvzmin.iz, uvzmax.iz);
					double Suz = Util.getSlope(xmin, xmax, uvzmin.uz, uvzmax.uz);
					double Svz = Util.getSlope(xmin, xmax, uvzmin.vz, uvzmax.vz);

					int xmin2=Math.max(xmin, 0);
					int xmax2=Math.min(xmax, Config.width);
					for(int x=xmin2;x<xmax2;x++)
					{
					 
					 	double iz=Util.interpSlope(xmin, x, uvzmin.iz, Siz);
					 	double uz=Util.interpSlope(xmin, x, uvzmin.uz, Suz);
					 	double vz=Util.interpSlope(xmin, x, uvzmin.vz, Svz);
					 	double u=uz/iz;
					 	double v=vz/iz;
					 	//int rgb=0;
					 	//try {
					 		int rgb = pixel.set(FrameBuffer.getRGB(x, y)).blend(s.Texture.getRGB((int)u, (int)v)).getRGB();
					 		//Color4 rgb = new Color4();					 		
					 		//Color4 light = new Color4().blend3(rgb);//.blend3(getLightOverlay());
					 	/*}catch(Exception e) {
					 		Main.log(e.getMessage() + " on texture");
					 		Main.log("u= "+u+" ,v="+v);
					 	}
					 	try {*/
					 		FrameBuffer.setRGB(x, y, rgb);
					 	/*}catch(Exception e) {
					 		Main.log(e.getMessage() + " on framebuffer");
					 		//Main.log(this);
					 		//Main.log("FrameBuffer.setRGB("+(nx)+", "+(y)+", s.Texture.getRGB("+(int)u+", "+(int)v+"));");
					 		Main.log("xmin= "+xmin+" ,x= "+x+" ,xmax= "+xmax+" ,ymin= "+ymin+" ,y= "+y+" ,ymax= "+ymax);
					 		Main.log(FrameBuffer.getWidth()+","+FrameBuffer.getHeight());
					 		Main.log("------------------------------------------------------------------");
					 	}*/
					 		//lighted=true;
					}
					
				}
				//g.drawImage(img,imgx,imgy, null);
			}
			
		}
		
		if(s.paint){
			((Graphics2D)g).setPaint(s.p);
			g.fillPolygon(polygon);
			//((Graphics2D)g).setPaint(Color.BLACK);
		}
		
		if(!lighted && !Config.useTextures) {
			
			Color4 lightedc=getLightOverlay();
			//Graphics2D g2 =((Graphics2D)g); 
			g.setColor(lightedc.getColor());
			g.fillPolygon(polygon);
			//Graphics2D g2 = (Graphics2D) g;
            //g2.setStroke(new BasicStroke(2));
			//g.drawPolygon(polygon);
			
		}
		

		
		boolean drawfog = (AvgDist > Config.renderDistance*(0.75f) && Config.fogEnabled);
		if(drawfog){
			g.setClip(polygon);
			float totalFogSize = Config.renderDistance/4f;
			float foggyDist = Config.renderDistance-AvgDist;
			int ratio = (int) (255*(foggyDist/totalFogSize));
			if(ratio > 255)
				ratio=255;
			if(ratio < 0){
				ratio = 0;
			}

			Color customColor = new Color(Main.skyColor.getRed(), Main.skyColor.getGreen(), Main.skyColor.getBlue(), 255-ratio);
			g.setColor(customColor);

			g.fillPolygon(polygon);

			g.setClip(null);
			g.setColor(new Color(0,0,0,ratio));
		}else{
			g.setColor(Color.BLACK);
		}
		
		if(s.c.getAlpha() == 255){
			g.drawPolygon(polygon);
		}
		
		

		
		if(selected){
			((Graphics2D)g).setPaint(new RadialGradientPaint(
					calculateCentroid(polygon, centroid2D),
					getRadius(polygon),
					fractions ,
					colors ,
					CycleMethod.NO_CYCLE));
			g.fillPolygon(polygon);

		}
		

	}
	
	


	public void recalc(Vector tmpVector) {
		if(Vertices.length>0) {
			Vector v0 = Vertices[0].pos;
			Vector v1 = Vertices[1].pos;
			Vector v2 = Vertices[2].pos;
			normal.set(v1).substract(v0).CrossProduct(tmpVector.set(v1).substract(v2));
			//Plane p = new Plane(Vertices[0].pos, Vertices[1].pos, Vertices[2].pos);
			//normal.set(p.normal);
		}
		
		float dx=0, dy=0, dz=0;
		
	    int pointCount = Vertices.length;
		for(Vertex v : Vertices) {

	    
	        dx += v.pos.x;
	        dy += v.pos.y;
	        dz += v.pos.z;
	    }

	    this.centroid.set(dx/pointCount, dy/pointCount, dz/pointCount);
	    //this.spawnpoint.set(centroid);
	}
	
	
	private boolean isAllBehind() {

		for(Vertex v : Vertices) {
			ViewToPoint.set(v.pos);
			ViewToPoint.substract(Main.GAME.PE.getPos());
			if(Main.GAME.ViewVector.DotProduct(ViewToPoint)<0){
				continue;
			}
			return false;
		}
		
		return true;
			
	}
	
	
	private static float getRadius(Polygon polygon){
		Rectangle2D box = polygon.getBounds2D();
		double w = box.getWidth();
		double h = box.getHeight();
		
		if((float) Math.sqrt(w*w+h*h)/2 < 2){
			return 2;
		}
		return (float) Math.sqrt(w*w+h*h)/2;
	}
	
	
	private static Point2D.Double calculateCentroid(Polygon polygon, Point2D.Double point) {
	    double x = 0d;
	    double y = 0d;
	    for (int i = 0;i < polygon.npoints;i++){
	        x += polygon.xpoints[i];
	        y += polygon.ypoints[i];
	    }

	    x = x/polygon.npoints;
	    y = y/polygon.npoints;

	    point.setLocation(x, y);
	    return point;
	}
	
	private void clearClip() {
		for(int i=0;i<clip.length;i++) {
			clip[i].set(i<Vertices.length? Vertices[i] : Vertex.NULL);
		}
		clipSize=Vertices.length;
	}
	
	
	private void copyClip() {
		for(int i=0;i<clip.length;i++) {
			clip[i].set(i<clip2Size? clip2[i] : Vertex.NULL);
		}
		clipSize=clip2Size;
		/*for(Vertex v : clip) {
			v.set(Vertices[]);
		}*/
	}
	
	private void clip(Plane P){
		if(clipSize==0) {
			return;
		}
		//ArrayList<Vertex> tmp = new ArrayList<>(Arrays.asList(clip)); 
		clip2Size=0;
		for(int i=0;i<clipSize;i++){

				Vertex v1 = clip[i];
				Vertex v2 = (i != clipSize-1) ? clip[i+1] : clip[0];
				
				Vector a = v1.pos;
				Vector b = v2.pos;
				
				
				
				float da = a.DotProduct(P.normal) - P.distance;
				float db = b.DotProduct(P.normal) - P.distance;
				
				float s= da/(da-db);

				if(da > 0 && db > 0){ // mindkettõ elõtte
					
					//tmp.add(v1);
					clip2[clip2Size].set(v1);
					clip2Size++;
				}else if(da < 0 && db < 0){ // mindkettõ mögötte
					
				}else if(da < 0 && db > 0){
					//Vector pos = b.cpy().substract(a).multiply(s).add(a);//a.cpy().add(a.to(b).multiply(s));
					//Vector pos = new Vector(a.x + s*(b.x-a.x), a.y + s*(b.y-a.y), a.z + s*(b.z-a.z));
					//tmp.add(Vertex.setInterp(a, pos, b, v1, v2));
					tmp.set(b);
					tmp.substract(a).multiply(s).add(a);
					Vertex.setInterp(a, tmp, b, v1, v2, clip2[clip2Size]);
					clip2Size++;
				}else if(da >0 && db < 0){ // elölrõl vágja félbe
					
					//tmp.add(v1);
					clip2[clip2Size].set(v1);
					clip2Size++;
					//Vector pos = b.cpy().substract(a).multiply(s).add(a);
					//Vector pos = new Vector(a.x + s*(b.x-a.x), a.y + s*(b.y-a.y), a.z + s*(b.z-a.z));
					//tmp.add(Vertex.setInterp(a, pos, b, v1, v2));
					tmp.set(b);
					tmp.substract(a).multiply(s).add(a);
					Vertex.setInterp(a, tmp, b, v1, v2, clip2[clip2Size]);
					clip2Size++;
					
				}
		}
		
		//clip.clear();
		//clip.addAll(tmp);
		copyClip();
	}
	




	private float GetDist()
	{
			
			return Main.GAME.PE.getPos().distance(centroid);
	}
	
	/*public void recalcCentroid() {
		float dx=0, dy=0, dz=0;
		
	    int pointCount = Vertices.length;
		for(Vertex v : Vertices) {

	    
	        dx += v.pos.x;
	        dy += v.pos.y;
	        dz += v.pos.z;
	    }

	    this.centroid.set(dx/pointCount, dy/pointCount, dz/pointCount);
	    this.spawnpoint.set(centroid).add(Vector.PLAYER);
	}*/

	



	@Override
	public String toString() {
		/*return "Polygon3D [adjecentFilter=" + adjecentFilter + ", faceFilter=" + faceFilter + ", s=" + s + ", Vertices="
				+ Arrays.toString(Vertices) + ", ymax=" + ymax + ", ymin=" + ymin + ", lights="+lightSources+"]";*/
		return s+","+lightSources;
	}
	
	
	
	/*double interpolate(Point2D point){
		double sum=0, weightsum = 0;
		for(int i=0; i<x.length;i++){
			double proximity =1d/point.distance(polygon.xpoints[i], polygon.ypoints[i]); 
			sum+=proximity*(Math.sqrt((x[i]-Main.GAME.PE.ViewFrom.x)*(x[i]-Main.GAME.PE.ViewFrom.x)+ (y[i]-Main.GAME.PE.ViewFrom.y)*(y[i]-Main.GAME.PE.ViewFrom.y)+ (z[i]-Main.GAME.PE.ViewFrom.z)*(z[i]-Main.GAME.PE.ViewFrom.z)));
			weightsum += proximity;
			
			
		}
		return sum / weightsum;
		
	}*/


	
	
}
