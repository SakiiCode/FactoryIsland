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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import ml.sakii.factoryisland.blocks.Block;
import ml.sakii.factoryisland.blocks.BlockFace;
import ml.sakii.factoryisland.blocks.Corner;
import ml.sakii.factoryisland.blocks.GradientCalculator;

public class Polygon3D extends Object3D{
	Polygon polygon = new Polygon();
	boolean adjecentFilter = true;
	private boolean faceFilter = true;
	public boolean selected;
	private Vector normal=new Vector();
	Vector centroid=new Vector();
	public Surface s;
	
	
	public final Vertex[] Vertices;
	private final Vertex[] clip = new Vertex[20];
	private final Vertex[] clip2 = new Vertex[20];
	private int clipSize, clip2Size;
	
	private HashMap<Integer, Integer> bufferXmin = new HashMap<>(Config.getHeight());
	private HashMap<Integer, Integer> bufferXmax = new HashMap<>(Config.getHeight());
	
	private HashMap<Integer, UVZ> bufferUVZmin = new HashMap<>(Config.getHeight());
	private HashMap<Integer, UVZ> bufferUVZmax = new HashMap<>(Config.getHeight());
	
	private Plane tmpnear=new Plane();
	private Vector RadiusVector=new Vector();
	private Vector CameraToTriangle = new Vector();
	private Vector tmp=new Vector();
	private int ymax, ymin;
	private int light=0;
	
	private final ConcurrentHashMap<Point3D, Integer> lightSources = new ConcurrentHashMap<>();
	private Color4 lightedcolor = new Color4();
	private Color4 overlay=new Color4();
	private Point2D.Double centroid2D = new Point2D.Double();
	private float[] fractions = new float[]{0.5f,1.0f};
	private Color[] colors = new Color[]{new Color(0.0f,0.0f,0.0f,0.0f), Color.BLACK};
	private int[][] UVMap;
	private int[][] clipUV,clipUV2;
	
	private float physicalRadius;
	
	
	Model model;

	public CopyOnWriteArrayList<BlockFace> SimpleOcclusions = new CopyOnWriteArrayList<>();
	public CopyOnWriteArrayList<Point3D> CornerOcclusions = new CopyOnWriteArrayList<>();
	private CopyOnWriteArrayList<GradientPaint> OcclusionPaints = new CopyOnWriteArrayList<>();
	
	private Vector lineVec = new Vector();
	private Vector pointVec = new Vector();
	
	private Point2D.Float begin1=new Point2D.Float();
	private Point2D.Float begin2=new Point2D.Float();
	private Point2D.Float end=new Point2D.Float();
	
	public Polygon3D(Vertex[] vertices,int[][] UVMapOfVertices, Surface s, Model model) {
		
		this.Vertices = vertices;
		this.UVMap = UVMapOfVertices;
		this.model=model;

		
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
		if(model.Engine != null) { //inditaskor
			recalcLightedColor();
		}
		
		physicalRadius=new Vector().set(centroid).substract(Vertices[1]).getLength(); //feltetelezve h csak teglalap van
	}
	


	@Override
	protected boolean update(Game game){
			
		// Ha bármelyik hamis, eltűnik. Csak akkor jelenik meg, ha az összes igaz.
			if(adjecentFilter) {
				if(!game.locked) {
					if(game.insideBlock(this)) {
						faceFilter=true;
					}else {
						CameraToTriangle.set(Vertices[0]).substract(game.PE.getPos());
						faceFilter = CameraToTriangle.DotProduct(normal) < 0;
					}
				}
				
				
				if(faceFilter) {
					AvgDist = game.PE.getPos().distance(centroid);
					if(AvgDist<=Config.renderDistance && !isAllBehind(RadiusVector, game)) {
						resetClipsTo(Vertices, UVMap, Vertices.length);
						clip(game.ViewFrustum.sides[0]);
						clip(game.ViewFrustum.sides[1]);
						clip(game.ViewFrustum.sides[3]);
						clip(game.ViewFrustum.sides[2]);
							
						if(game.locked){
							tmpnear.normal.set(game.ViewVector);
							tmpnear.normal.multiply(0.01f);
							tmpnear.normal.add(game.PE.getPos());
							tmpnear.distance = game.ViewVector.DotProduct(tmpnear.normal);
							tmpnear.normal.set(game.ViewVector);
							clip(tmpnear);
						}
						
						if(clipSize>0){
							
							polygon.reset();

							
							for(int i=0;i<clipSize;i++) {
								Vertex v=clip[i];
								

								v.update(game);
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
								ymin=Math.min(ymin, game.FrameBuffer.getHeight());
								ymax=Math.min(ymax, game.FrameBuffer.getHeight());
								
								if(ymin==ymax) {
									return false;
								}
							
							}
							
							if(!Config.useTextures) {
								recalcOcclusionPaints(game);
							}
							
							return true;
	
						
						}
					}
				}
			}
			
			return false;
		}
	
	void addSource(Point3D b, int intensity) {
		
		lightSources.put(b, intensity);
		
		recalcLightedColor();
	}
	
	void removeSource(Point3D b) {
		
		lightSources.remove(b);
		recalcLightedColor();
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
		int skylight = getLightLevel(model.Engine.getTimePercent());
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
	
	
	public Color4 getLightedColor() {
		return lightedcolor;
	}
	
	public void recalcLightedColor() {
		lightedcolor.set(s.c).blend(getLightOverlay());
	}
	
	public int getLight() {
		return light;
	}
		

	void drawToBuffer(PixelData[][] ZBuffer, Game game) {
		
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
			
			UVZ tmpUVZ1 = v1.getUVZ(clipUV[index1], game);
			UVZ tmpUVZ2 = v2.getUVZ(clipUV[index2], game);
			
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
	
		int imgw = Collections.max(bufferXmax.values()) -  Collections.min(bufferXmin.values());
		
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
				int xmax2=Math.min(xmax, Config.getWidth());
				for(int x=xmin2;x<xmax2;x++)
				{
				 
			 		double iz=Util.interpSlope(xmin, x, uvzmin.iz, Siz);
			 		synchronized(ZBuffer[x][y]) {
					 	if(ZBuffer[x][y].depth>iz) continue;
					 	ZBuffer[x][y].depth=iz;
					 	if(!game.key[6]) {//depthmap-nál ne mutassa a kereteket
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
					 	if(game.key[6]) {
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
				 				rgb=Color4.blend(px, overlay.getRGB());

	
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
	protected void draw(BufferedImage FrameBuffer, Graphics g, Game game){
		Graphics2D g2d=(Graphics2D)g;
		
		g2d.setColor(s.paint ? s.c.getColor() : lightedcolor.getColor());
		g2d.fillPolygon(polygon);

		if(s.paint) {
			g2d.setPaint(s.p);
			g2d.fillPolygon(polygon);
			Color4 lightedc=getLightOverlay();
			g2d.setColor(lightedc.getColor());
			g2d.fillPolygon(polygon);
		}
		
		if(Config.ambientOcclusion) {
			for(GradientPaint p : OcclusionPaints) {
				g2d.setPaint(p);
				g2d.fillPolygon(polygon);
			}
		}
		
		boolean drawfog = (AvgDist > Config.renderDistance*(0.75f) && Config.fogEnabled);
		if(drawfog){
			float totalFogSize = Config.renderDistance/4f;
			float foggyDist = Config.renderDistance-AvgDist;
			int ratio = (int) (255*(foggyDist/totalFogSize));
			if(ratio > 255)
				ratio=255;
			if(ratio < 0){
				ratio = 0;
			}

			Color customColor = new Color(AssetLibrary.skyColor.getRed(), AssetLibrary.skyColor.getGreen(), AssetLibrary.skyColor.getBlue(), 255-ratio);
			g2d.setColor(customColor);

			g2d.fillPolygon(polygon);
			g2d.setColor(new Color(0,0,0,ratio));
		}else{
			g2d.setColor(Color.BLACK);
		}
		
		
		if(s.c.getAlpha() == 255){
			g2d.drawPolygon(polygon);
		}
		
		
		if(selected && game.showHUD) {
			renderSelectOutline(g);
		}
		
	}
	
	
	void renderSelectOutline(Graphics fb){
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
	
	private void recalcOcclusionPaints(Game game) {
		if(!(model instanceof Block)){
			return;
		}
		Block b = (Block) model;
		BlockFace currentFace = b.HitboxPolygons.get(this);
		OcclusionPaints.clear();
		
		for(BlockFace nearbyFace : SimpleOcclusions) {
			Point2D.Float[] values = GradientCalculator.getGradientOf(b.x, b.y, b.z, currentFace, nearbyFace, lineVec, game);
			begin1 = values[0];
			begin2 = values[1];
			end = values[2];					
			
			pointVec.set(end.x-begin2.x,end.y-begin2.y,0);
			lineVec.set(begin1.x-begin2.x,begin1.y-begin2.y,0)
				.normalize()
				.multiply(lineVec.DotProduct(pointVec))
				.add(begin2.x, begin2.y, 0);

			OcclusionPaints.add(new GradientPaint(lineVec.x, lineVec.y,	Color4.AO_MAX, end.x, end.y, Color4.TRANSPARENT));
		}
		

		
		BlockFace face = currentFace;
		
		
		
		for(Point3D delta : CornerOcclusions) {
			
			
			Corner c = Corner.fromDelta(face, delta);
			
			
			int corner = c.toInt();
			
			
			game.convert3Dto2D(tmp.set(Vertices[Math.floorMod(corner-1, 4)]), begin1);
			game.convert3Dto2D(tmp.set(Vertices[Math.floorMod(corner+1, 4)]), begin2);
			game.convert3Dto2D(tmp.set(Vertices[Math.floorMod(corner, 4)]), end);
			
			
			
			pointVec.set(end.x-begin2.x,end.y-begin2.y,0);
			lineVec.set(begin1.x-begin2.x,begin1.y-begin2.y,0)
				.normalize()
				.multiply(lineVec.DotProduct(pointVec))
				.add(begin2.x, begin2.y, 0);
			

			OcclusionPaints.add((new GradientPaint(lineVec.x, lineVec.y, Color4.TRANSPARENT, end.x, end.y, Color4.AO_MAX)));
			
		}

	}


	public void recalc(Vector tmpVector) {
		if(Vertices.length>0) {
			Vector v0 = Vertices[0];
			Vector v1 = Vertices[1];
			Vector v2 = Vertices[2];
			normal.set(v2).substract(v0).CrossProduct(tmpVector.set(v1).substract(v0));
		}
		
		float dx=0, dy=0, dz=0;
		
	    int pointCount = Vertices.length;
		for(Vertex v : Vertices) {

	    
	        dx += v.x;
	        dy += v.y;
	        dz += v.z;
	    }

	    this.centroid.set(dx/pointCount, dy/pointCount, dz/pointCount);
	}
	
	
	
	
	private boolean isAllBehind(Vector tmp2, Game game) { //7.1% -> 4.9%
		boolean result=true;

			tmp2.set(game.ViewVector).multiply(physicalRadius); //radius vector
		
			if(tmp2.add(centroid).substract(game.PE.getPos()).DotProduct(game.ViewVector)<0) {
				result= true;
			}else {
				result=false;
			}
		
		return result;
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
		for(int i=0;i<size;i++) {
			clip[i].set(vertexArr[i]);
			clipUV[i]=uvArr[i];
		}
		clipSize=size;
	}
	
	private void clip(Plane P){
		
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
						clipUV2[clip2Size] = getUVInterp(a, tmp, b, uv1,uv2);
					clip2Size++;
					
				}else if(da >0 && db < 0){ // elölről vágja félbe
					
					clip2[clip2Size].set(v1);
					if(Config.useTextures)
						clipUV2[clip2Size]=uv1;
					clip2Size++;
					
					clip2[clip2Size].set(tmp.set(b).substract(a).multiply(s).add(a));
					if(Config.useTextures)
						clipUV2[clip2Size] = getUVInterp(a, tmp, b, uv1,uv2);
					clip2Size++;
					
				}
		}
		
		resetClipsTo(clip2,clipUV2,clip2Size);
	}
	
	private static int[] getUVInterp(Vector p1, Vector pos, Vector p2, int[]uv1, int[] uv2) {
		double distanceratio = p1.distance(pos) / p1.distance(p2);
		int u = (int) Util.interp(0, 1, distanceratio, uv1[0], uv2[0]);
		int v = (int) Util.interp(0, 1, distanceratio, uv1[1], uv2[1]);
		return new int[] {u,v};
	}
	

	@Override
	public String toString() {
		return s+",light:"+getLight()+",simple:"+SimpleOcclusions+",corner:"+CornerOcclusions+",paints:"
				+", "+OcclusionPaints;
	}


	
}
