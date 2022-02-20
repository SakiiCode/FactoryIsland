package ml.sakii.factoryisland;

import java.awt.Color;
import java.awt.GradientPaint;
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
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import ml.sakii.factoryisland.blocks.Block;
import ml.sakii.factoryisland.blocks.BlockFace;
import ml.sakii.factoryisland.blocks.Corner;
import ml.sakii.factoryisland.blocks.GradientCalculator;

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
	private Vector RadiusVector=new Vector();
	private Vector CameraToTriangle = new Vector();
	private Vector tmp=new Vector();
	int ymax, ymin;
	private int light=0;
	
	private final ConcurrentHashMap<Point3D, Integer> lightSources = new ConcurrentHashMap<>();
	private Color4 lightedcolor = new Color4();
	private Color4 overlay=new Color4();
	Color4 pixel = new Color4();
	//Vector spawnpoint=new Vector();
	Point2D.Double centroid2D = new Point2D.Double();
	private float[] fractions = new float[]{0.5f,1.0f};
	private Color[] colors = new Color[]{new Color(0.0f,0.0f,0.0f,0.0f), Color.BLACK};
	//public static final HashMap<Vertex, Point> Cache = new HashMap<>();
	int[][] UVMap;
	int[][] clipUV,clipUV2;
	//UVZ[] uvz;
	
	float physicalRadius;
	
	static final long TICKS_PER_DAY = 72000;
	
	Model model;
	
	
	HashSet<BlockFace> SimpleOcclusions = new HashSet<>();
	HashSet<Point3D> CornerOcclusions = new HashSet<>();
	
	public Polygon3D(Vertex[] vertices,int[][] UVMapOfVertices, Surface s, Model model) {
		
		this.Vertices = vertices;
		this.UVMap = UVMapOfVertices;
		this.model=model;
		/*this.uvz = new UVZ[vertices.length];
		for(int i=0;i<uvz.length;i++) {
			uvz[i]=new UVZ(); 
		}*/
		clipUV=new int[20][2];
		clipUV2=new int[20][2];

		
		this.s = s;
		for(int i=0;i<clip.length;i++) {
			clip[i]=new Vertex(Vertex.NULL);
		}
		for(int i=0;i<clip2.length;i++) {
			clip2[i]=new Vertex(Vertex.NULL);
		}
		
		recalc(new Vector());
		recalcLightedColor();
		
		physicalRadius=new Vector().set(centroid).substract(Vertices[1]).getLength(); //feltetelezve h csak teglalap van
	}
	


	@Override
	protected boolean update(){
			
		// Ha bármelyik hamis, eltűnik. Csak akkor jelenik meg, ha az összes igaz.
			if(adjecentFilter) {
				if(!Main.GAME.locked) {
					if(Main.GAME.ViewBlock.Polygons.contains(this)) {
						faceFilter=true;
					}else {
						CameraToTriangle.set(Vertices[0]).substract(Main.GAME.PE.getPos());
						faceFilter = CameraToTriangle.DotProduct(normal) < 0;
					}
				}
				
				
				if(faceFilter) {
					//if(!Main.GAME.locked) {
						AvgDist = GetDist();
					//}
					if(AvgDist<=Config.renderDistance && !isAllBehind(RadiusVector)) {
						//clearClip();
						//clip(Main.GAME.ViewFrustum.znear);
						resetClipsTo(Vertices, UVMap, Vertices.length);
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
								//v.getUVZ(clipUV[i],uvz[i]);
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
	
	public void addSource(Point3D b, int intensity) {
		
		lightSources.put(b, intensity);
		
		recalcLightedColor();
	}
	
	void removeSource(Point3D b) {
		
		lightSources.remove(b);
		recalcLightedColor();
	}
	
	Integer checkSource(Point3D b) {
		return lightSources.get(b);
	}
	
	Set<Point3D> getSources(){
		return lightSources.keySet();
	}
	
	public Vector getNormal() {
		return normal;
	}
	
	public Vector getCentroid() {
		return centroid;
	}
	
	
	public Color4 getLightOverlay() {
		light=0;
		for(int intensity : lightSources.values()) {
			if(intensity>light) {
				light=intensity;
			}
		}
		int skylight = Main.GAME == null ? 0: getLightLevel(getTimePercent(Main.GAME.Engine.Tick));
		light = Math.max(light, skylight);
		return overlay.setAlpha((int)(Math.pow(0.8, light+1)*255));
	}
	

	
	private int getLightLevel (double percent) {
		
		double radians = percent*2*Math.PI;
		if(centroid.z<=0) {
			radians += Math.PI;
		}
			
		int skylight = (int)(14f*Math.sin(radians)); 
		return skylight;
	}
	
	public static int testLightLevel(double percent) {
		int skylight = (int)(14f*Math.sin(percent*2*Math.PI)); 
		return skylight;

	}
	
	public static double getTimePercent(long Tick) {
		
		//long day=Tick/ticksPerDay;
		long hours=Tick%TICKS_PER_DAY;
		double skyLightF=(hours*1f/TICKS_PER_DAY);
		return skyLightF;
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
		

	public void drawToBuffer(PixelData[][] ZBuffer) {
		
		// buffer init
		bufferXmin.clear();
		bufferXmax.clear();
		bufferUVZmin.clear();
		bufferUVZmax.clear();
		

		//vertexről vertexre körbemegyünk
		for(int i=0;i<clipSize;i++) {
			
			int index1= i;
			int index2= i != clipSize-1 ? i+1 : 0;
			
			Vertex v1 = clip[index1]; 
			Vertex v2 = clip[index2];
			
			UVZ tmpUVZ1 = v1.getUVZ(clipUV[index1]);
			UVZ tmpUVZ2 = v2.getUVZ(clipUV[index2]);
			
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
					bufferUVZmin.put(y, UVZ.interp(p1, p2, new Point((int) x, y), tmpUVZ1, tmpUVZ2));
				}
				
				if(bufferXmax.get(y) == null || x > bufferXmax.get(y)) {
					bufferXmax.put(y,(int) x);
					bufferUVZmax.put(y, UVZ.interp(p1, p2, new Point((int) x, y), tmpUVZ1, tmpUVZ2));
				}
				
				x+=m;
					
			}
				
			
			

		}
	
		//int imgx = Collections.min(bufferXmin.values());
		int imgw = Collections.max(bufferXmax.values()) -  Collections.min(bufferXmin.values());
		//int imgy = ymin;
		//int imgh = ymax-ymin;
		
		
		if(imgw>0) { //ha merőlegesen állunk ne rajzoljon
			
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
			 		synchronized(ZBuffer[x][y]) {
					 	if(ZBuffer[x][y].depth>iz) continue;
					 	ZBuffer[x][y].depth=iz;
					 	if(!Main.GAME.key[6]) {//depthmap-nál ne mutassa a kereteket
						 	if(y>ymin && y<ymax-1 &&
						 			(
							 			(x<bufferXmin.get(y+1) || x>bufferXmax.get(y+1) || x<bufferXmin.get(y-1) || x>bufferXmax.get(y-1)) || 
							 			x==xmin2 || x==xmax2-1
						 			)
						 		) {
						 		ZBuffer[x][y].color=0xFF000000;
						 		continue;
						 	}else if(y==ymax-1 || y==ymin) {
						 		ZBuffer[x][y].color=0xFF000000;
						 		continue;
						 	}
					 	}
					 	double uz=Util.interpSlope(xmin, x, uvzmin.uz, Suz);
					 	double vz=Util.interpSlope(xmin, x, uvzmin.vz, Svz);
					 	
					 	int rgb;
					 	if(Main.GAME.key[6]) {
					 		int px=Math.round(255*(float)(0.03/iz));
					 		rgb = (255 << 24) | (px << 16) | (px << 8) | px;
					 		
					 	}else {
						 	double u=uz/iz;
						 	double v=vz/iz;
						 	
						 	try {
						 		int px;
						 		if(s.color) {
						 			px=s.c.getRGB();
						 		}else {
						 			px=s.Texture.getRGB((int)u, (int)v);
						 		}
						 		px=px|0xFF000000;
							 	/*int previous = ZBuffer[x][y].color;
							 	int alpha =(px>>24)&0xFF; 
							 	if(alpha != 255) {
							 		rgb = Color4.blend(previous, px);//pixel.set(previous).blend(px).getRGB();
							 	}else {*/
						 			if(Main.GAME.key[8]) {
						 				rgb=px;
						 			}else {
						 				rgb=Color4.blend(px, overlay.getRGB());
						 			}
							 	//}
	
						 	}catch(Exception e) {
						 		e.printStackTrace();
						 		rgb=0;
						 	}
					 	}
					 	ZBuffer[x][y].color=rgb;
				 		
			 		}
				}
				
			}
			

		}
	}

	@Override
	protected void draw(BufferedImage FrameBuffer, Graphics g){
		boolean lighted=false;
		Graphics2D g2d=(Graphics2D)g;
			if(!s.paint) {
				g2d.setColor(lightedcolor.getColor());
				lighted=true;
			}else {
				g2d.setColor(s.c.getColor());
			}
			g2d.fillPolygon(polygon);
		
		if(s.paint){
			
			g2d.setPaint(s.p);
			g2d.fillPolygon(polygon);
		}
		
		if(!lighted) {
			
			Color4 lightedc=getLightOverlay();
			g2d.setColor(lightedc.getColor());
			g2d.fillPolygon(polygon);
			
		}



		
		boolean drawfog = (AvgDist > Config.renderDistance*(0.75f) && Config.fogEnabled);
		if(drawfog){
			//g2d.setClip(polygon);
			float totalFogSize = Config.renderDistance/4f;
			float foggyDist = Config.renderDistance-AvgDist;
			int ratio = (int) (255*(foggyDist/totalFogSize));
			if(ratio > 255)
				ratio=255;
			if(ratio < 0){
				ratio = 0;
			}

			Color customColor = new Color(Main.skyColor.getRed(), Main.skyColor.getGreen(), Main.skyColor.getBlue(), 255-ratio);
			g2d.setColor(customColor);

			g2d.fillPolygon(polygon);

			//g2d.setClip(null);
			g2d.setColor(new Color(0,0,0,ratio));
		}else{
			g2d.setColor(Color.BLACK);
		}
		
		
		

		if(Config.ambientOcclusion && model instanceof Block) {
			Block b = (Block)model;
			BlockFace currentFace = b.HitboxPolygons.get(this);

			
			for(BlockFace nearbyFace : SimpleOcclusions) {
				Point2D.Float[] values = GradientCalculator.getGradientOf(b.x, b.y, b.z, currentFace, nearbyFace, new Vector(), Main.GAME);
				Point2D.Float begin1 = values[0];
				Point2D.Float begin2 = values[1];
				Point2D.Float end = values[2];					
				
				
				Vector lineVec = new Vector(begin1.x-begin2.x,begin1.y-begin2.y,0).normalize();
				Vector pointVec = new Vector(end.x-begin2.x,end.y-begin2.y,0);
				lineVec.multiply(lineVec.DotProduct(pointVec));
				Point2D intersection = new Point2D.Float(lineVec.x+begin2.x,lineVec.y+begin2.y);
					
				g2d.setPaint(new GradientPaint(intersection, new Color4(0,0,0,Globals.AO_STRENGTH).getColor(), end,Color4.TRANSPARENT));
				g2d.fillPolygon(polygon);
				g2d.setColor(Color.black);
			}
			

			
			BlockFace face = currentFace;
			
			
			for(Point3D delta : CornerOcclusions) {
				
				
				
				Corner c = Corner.fromDelta(face, delta);
				
				
				int corner = c.toInt();
				
				
				Point2D.Float begin1 = Main.GAME.convert3Dto2D(Vertices[Math.floorMod(corner-1, 4)].cpy(), new Point2D.Float());
				Point2D.Float begin2 = Main.GAME.convert3Dto2D(Vertices[Math.floorMod(corner+1, 4)].cpy(), new Point2D.Float());
				Point2D.Float end = Main.GAME.convert3Dto2D(Vertices[Math.floorMod(corner, 4)].cpy(), new Point2D.Float());
				
				Vector lineVec = new Vector(begin1.x-begin2.x,begin1.y-begin2.y,0).normalize();
				Vector pointVec = new Vector(end.x-begin2.x,end.y-begin2.y,0);
				lineVec.multiply(lineVec.DotProduct(pointVec));
				Point2D intersection = new Point2D.Float(lineVec.x+begin2.x,lineVec.y+begin2.y);
				

				g2d.setPaint(new GradientPaint(intersection, Color4.TRANSPARENT, end, new Color4(0,0,0,Globals.AO_STRENGTH).getColor()));
				g2d.fillPolygon(polygon);
				g2d.setColor(Color.black);
				
			}

		}
			
		
		
		
		
		
		if(s.c.getAlpha() == 255){
			g2d.drawPolygon(polygon);
		}
		
		
		if(selected) {
			renderSelectOutline(g);
		}
		


	}
	

	void recalcSimpleOcclusions(World world){
		SimpleOcclusions.clear();
		Block b = (Block)model;
		if(b.transparent || b.lightLevel>0) {
			return;
		}
		BlockFace face = b.HitboxPolygons.get(this);
		
		for(Entry<BlockFace, Block> entry : world.get6Blocks(b.pos.cpy().add(face), false).entrySet()) {
			if(entry.getValue().transparent || entry.getValue().lightLevel>0) {
				continue;
			}
			
			BlockFace nearbyFace = entry.getKey();
			if(nearbyFace == face || nearbyFace == face.getOpposite()) {
				continue;
			}
			SimpleOcclusions.add(nearbyFace);
			
			
			
		}
		
	}
	
	void recalcCornerOcclusions(World world) {
		CornerOcclusions.clear();
		Block b = ((Block)model);
		if(b.transparent || b.lightLevel>0) {
			return;
		}
		BlockFace face = b.HitboxPolygons.get(this);

		for(Entry<Point3D, Block> entry : world.get4Blocks(b.pos, face, false).entrySet()) {
			Point3D delta = entry.getKey();
			if(world.getBlockAtP(b.pos.cpy().add(face).add(delta.x,0,0)) != Block.NOTHING || 
					world.getBlockAtP(b.pos.cpy().add(face).add(0,delta.y,0)) != Block.NOTHING || 
							world.getBlockAtP(b.pos.cpy().add(face).add(0,0,delta.z)) != Block.NOTHING) {
				continue;
			}
			if(entry.getValue().transparent || entry.getValue().lightLevel > 0) {
				continue;
			}
			CornerOcclusions.add(delta);
		}
	}
	
	
	public void renderSelectOutline(Graphics fb){
		if(!Main.GAME.showHUD) return;
		
		Graphics2D g2d=((Graphics2D)fb);
		if(Config.targetMarkerType == TargetMarkerType.SHADE) {
			g2d.setPaint(new RadialGradientPaint(
					calculateCentroid(polygon, centroid2D),
					getRadius(polygon),
					fractions ,
					colors ,
					CycleMethod.NO_CYCLE));
			g2d.fillPolygon(polygon);
		}else{
			g2d.setColor(Color.white);
			g2d.drawPolygon(polygon);
		}
	}


	public void recalc(Vector tmpVector) {
		if(Vertices.length>0) {
			Vector v0 = Vertices[0];
			Vector v1 = Vertices[1];
			Vector v2 = Vertices[2];
			normal.set(v2).substract(v0).CrossProduct(tmpVector.set(v1).substract(v0));
			//Plane p = new Plane(Vertices[0], Vertices[1], Vertices[2]);
			//normal.set(p.normal);
		}
		
		float dx=0, dy=0, dz=0;
		
	    int pointCount = Vertices.length;
		for(Vertex v : Vertices) {

	    
	        dx += v.x;
	        dy += v.y;
	        dz += v.z;
	    }

	    this.centroid.set(dx/pointCount, dy/pointCount, dz/pointCount);
	    //this.spawnpoint.set(centroid);
	}
	
	
	
	
	private boolean isAllBehind(Vector tmp2) { //7.1% -> 4.9%
		boolean result=true;

			tmp2.set(Main.GAME.ViewVector).multiply(physicalRadius); //radius vector
		
			if(tmp2.add(centroid).substract(Main.GAME.PE.getPos()).DotProduct(Main.GAME.ViewVector)<0) {
				result= true;
			}else {
				result=false;
			}
		
		return result;
		//centroid+radius*ViewVector-ViewFrom . ViewVector <0 akkor mogotte
			
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

	private void resetClipsTo(Vertex[] vertexArr, int[][] uvArr, int size) {
		for(int i=0;i<clip.length;i++) {
			clip[i].set(i<size ? vertexArr[i] : Vertex.NULL);
			clipUV[i] = i<size ? uvArr[i] : new int[]{0,0};
		}
		clipSize=size;
	}
	
	/*private void resetClipsTo(Vertex[] vertexArr, int[][] uvArr, int size) {
		for(int i=0;i<size;i++) {
			clip[i].set(vertexArr[i]);
			if(Config.useTextures) {
				clipUV[i][0] = uvArr[i][0];
				clipUV[i][1] = uvArr[i][1];
			}
		}
		clipSize=size;
	}*/
	
	
	
	
	
		
	private void clip(Plane P){
		
		//ArrayList<Vertex> tmp = new ArrayList<>(Arrays.asList(clip)); 
		clip2Size=0;
		for(int i=0;i<clipSize;i++){

				int index1=i;
				int index2= (i != clipSize-1) ? i+1 : 0;
				
				Vertex v1 = clip[index1];
				Vertex v2 = clip[index2];
				
				int[] uv1 = clipUV[index1];
				int[] uv2 = clipUV[index2];
				
				Vector a = v1;
				Vector b = v2;
				
				
				
				float da = a.DotProduct(P.normal) - P.distance;
				float db = b.DotProduct(P.normal) - P.distance;
				
				float s= da/(da-db);

				
				
				if(da > 0 && db > 0){ // mindkettő előtte
					
					clip2[clip2Size].set(v1);
					if(Config.useTextures)
						clipUV2[clip2Size]=uv1;
					clip2Size++;
					
				}else if(da < 0 && db < 0){ // mindkettő mögötte
					
				}else if(da < 0 && db > 0){
					
					clip2[clip2Size].set(tmp.set(b).substract(a).multiply(s).add(a));
					if(Config.useTextures)
						clipUV2[clip2Size] = Vertex.getUVInterp(a, tmp, b, uv1,uv2);
					clip2Size++;
					
				}else if(da >0 && db < 0){ // elölről vágja félbe
					
					clip2[clip2Size].set(v1);
					if(Config.useTextures)
						clipUV2[clip2Size]=uv1;
					clip2Size++;
					
					clip2[clip2Size].set(tmp.set(b).substract(a).multiply(s).add(a));
					if(Config.useTextures)
						clipUV2[clip2Size] = Vertex.getUVInterp(a, tmp, b, uv1,uv2);
					clip2Size++;
					
				}
		}
		
		resetClipsTo(clip2,clipUV2,clip2Size);
	}
	




	private float GetDist()
	{
			
			return Main.GAME.PE.getPos().distance(centroid);
	}
	
	/*public void recalcCentroid() {
		float dx=0, dy=0, dz=0;
		
	    int pointCount = Vertices.length;
		for(Vertex v : Vertices) {

	    
	        dx += v.x;
	        dy += v.y;
	        dz += v.z;
	    }

	    this.centroid.set(dx/pointCount, dy/pointCount, dz/pointCount);
	    this.spawnpoint.set(centroid).add(Vector.PLAYER);
	}*/

	



	@Override
	public String toString() {
		/*return "Polygon3D [adjecentFilter=" + adjecentFilter + ", faceFilter=" + faceFilter + ", s=" + s + ", Vertices="
				+ Arrays.toString(Vertices) + ", ymax=" + ymax + ", ymin=" + ymin + ", lights="+lightSources+"]";*/
		return s+","+lightSources.toString().replace(", ", "\r\n");
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
