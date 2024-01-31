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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import ml.sakii.factoryisland.blocks.Block;
import ml.sakii.factoryisland.blocks.BlockFace;
import ml.sakii.factoryisland.blocks.Corner;
import ml.sakii.factoryisland.blocks.GradientCalculator;

public class Polygon3D extends Object3D implements BufferRenderable{
	Polygon polygon = new Polygon();
	private boolean faceFilter = true;
	private Vector normal=new Vector();
	Vector centroid=new Vector();
	public Surface s;
	Model model;
	
	
	public final Vector[] Vertices;
	private final Vector[] clip = new Vector[8];
	private final Point[] result = new Point[8];
	private int clipSize;

	
	private int ymax, ymin;
	private int light=0;
	
	private final HashMap<Point3D, Integer> lightSources = new HashMap<>();
	private Color4 lightedcolor = new Color4();
	private Color4 overlay=new Color4();
	private static final float[] fractions = new float[]{0.5f,1.0f};
	private static final Color[] colors = new Color[]{new Color(0.0f,0.0f,0.0f,0.0f), Color.BLACK};
	private double[][] UVMap;
	private double[][] clipUV;
	
	private float physicalRadius;

	//private ArrayList<BlockFace> SimpleOcclusions = new ArrayList<>();
	private boolean[] SimpleOcclusions = new boolean[BlockFace.values().length];
	private boolean[] CornerOcclusions = new boolean[Corner.values().length];
	//private ArrayList<Point3D> CornerOcclusions = new ArrayList<>();
	private ArrayList<GradientPaint> OcclusionPaints = new ArrayList<>();
	private boolean hasAO = false;

	
	public Polygon3D(Vector[] vertices,int[][] UVMapOfVertices, Surface s, Model model) {
		
		this.Vertices = vertices;
		
		this.UVMap = new double[UVMapOfVertices.length][UVMapOfVertices[0].length+1];
		for(int i=0;i<UVMapOfVertices.length;i++) {
			for(int j=0;j<UVMapOfVertices[0].length;j++) {
				UVMap[i][j]=UVMapOfVertices[i][j];
			}
		}
		this.model=model;

		
		clipUV=new double[8][3];

		
		this.s = s;
		for(int i=0;i<clip.length;i++) {
			clip[i]=new Vector();
		}
		for(int i=0;i<result.length;i++) {
			result[i] = new Point();
		}
		recalc();
		if(model.Engine != null) { //inditaskor
			recalcLightedColor();
		}
		
		physicalRadius=new Vector().set(centroid).substract(Vertices[1]).getLength(); //feltetelezve h csak teglalap van
	}
	


	@Override
	protected boolean update(UpdateContext context){
		Game game = context.game;
		Vector PEPos = game.PE.getPos();
		if(!game.locked) {
			if(game.insideBlock(this) || (Config.useTextures && game.insideSphere(this))) {
				faceFilter = true;
			}else {
				faceFilter = (Vertices[0].x-PEPos.x)*normal.x+(Vertices[0].y-PEPos.y)*normal.y+(Vertices[0].z-PEPos.z)*normal.z < 0;
			}
		}
		
		if(!faceFilter) {
			return false;
		}
			
			
		AvgDist = PEPos.distance(centroid);
		if(AvgDist > Config.renderDistance) {
			return false;
		}
		
		if(isAllInvisible(context)) {
			return false;
		}
		
		resetClipsTo(Vertices, UVMap, Vertices.length);
		if(!isAllVisible(context)) {
			clip(context, game.ViewFrustum.sides);
		}
			
		if(game.locked){
			clip(context, game.ViewFrustum.tmpnear);
		}
		
		if(clipSize == 0){
			return false;
		}
		
		game.convert3Dto2D(clip, result, clipSize);
		
		ymin = result[0].y;
		ymax = result[0].y;
		for(int i=0;i<clipSize;i++) {
			ymin = Math.min(ymin,result[i].y);
			ymax = Math.max(ymax,result[i].y);
		}
		
		ymin = Util.limit(ymin, 0, Config.getHeight());
		ymax = Util.limit(ymax, 0, Config.getHeight());
		
		if(ymax<ymin+2) {
			return false;
		}
		
		polygon.reset();
		for(int i=0;i<clipSize;i++) {
			polygon.addPoint(result[i].x, result[i].y);
		}
		
		if(hasAO && !Config.useTextures && Config.ambientOcclusion) {
			recalcOcclusionPaints(context);
		}
		
		return true;
	}
	
	void addSource(Point3D b, int intensity) {
		lightSources.put(b, intensity);
	}
	
	void removeSource(Point3D b) {
		lightSources.remove(b);
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
		
	@Override
	public void drawToBuffer(TextureRenderThread context) {
		
		// buffer init
		int[] bufferXmin = context.bufferXmin;
		for(int i=0;i<bufferXmin.length;i++) {
			bufferXmin[i]=Config.getWidth()+1;
		}
		
		int[]bufferXmax = context.bufferXmax;
		for(int i=0;i<bufferXmax.length;i++) {
			bufferXmax[i]=-1;
		}
		
		UVZ tmpUVZ1 = context.tmpUVZ1;
		UVZ tmpUVZ2 = context.tmpUVZ2;
		
		UVZ[] bufferUVZmin = context.bufferUVZmin;
		UVZ[] bufferUVZmax = context.bufferUVZmax;
		
		Game game = context.game;
		
		//vertexről vertexre körbemegyünk
		for(int i=0;i<clipSize;i++) {
			setupXBuffers(bufferXmin, bufferXmax, bufferUVZmin, bufferUVZmax, tmpUVZ1, tmpUVZ2, game);
		}
		
		int[][] Texture = s.TextureRGB;
	
		
		// átmeneti kép, erre rajzoljuk a polygont, és ezt rajzoljuk az ablakra

		
		//a polygon minden során végigmegyünk
		for(int y=ymin;y<ymax;y++)
		{
			//a polygon adott sorának két szélének adatai
			
			int xmin=bufferXmin[y-ymin];
			int xmax=bufferXmax[y-ymin];
			UVZ uvzmin = bufferUVZmin[y-ymin];
			UVZ uvzmax = bufferUVZmax[y-ymin];
		 
			if(uvzmin == null) {
				Main.err("uvzmin is null - y: "+y+", ymin: "+ymin+", length: "+bufferUVZmin.length+", index: "+(y-ymin));
				Main.err(bufferUVZmin);
				return;
			}
			
			if(uvzmax == null) {
				Main.err("uvzmax is null - y: "+y+", ymin: "+ymin+", length: "+bufferUVZmax.length+", index: "+(y-ymin));
				Main.err(bufferUVZmax);
				return;
			}

			double Siz = Util.getSlope(xmin, xmax, uvzmin.iz, uvzmax.iz);
			double Suz = Util.getSlope(xmin, xmax, uvzmin.uz, uvzmax.uz);
			double Svz = Util.getSlope(xmin, xmax, uvzmin.vz, uvzmax.vz);
			double Sao = Util.getSlope(xmin, xmax, uvzmin.ao, uvzmax.ao);
			
			drawScanline(xmin, xmax, uvzmin, Siz, Suz, Svz, Sao, y, Texture, context.renderer.ZBuffer);
		 		
				
		}
		
		drawTexturedOutline(context);
		
	}
	
	private void drawScanline(int xmin, int xmax, UVZ uvzmin, double Siz, double Suz, double Svz, double Sao, int y, int[][] Texture, PixelData[][] ZBuffer) {
		for(int x=xmin;x<xmax;x++)
		{
			double iz  = Util.interpSlope(xmin, x, uvzmin.iz, Siz);
		 	double uz  = Util.interpSlope(xmin, x, uvzmin.uz, Suz);
		 	double vz  = Util.interpSlope(xmin, x, uvzmin.vz, Svz);
		 	double aoz = Util.interpSlope(xmin, x, uvzmin.ao, Sao);
		 	
		 	int u  = (int) (uz/iz);
		 	int v  = (int) (vz/iz);
		 	int ao = (int) (aoz/iz*255);
		 	
		 	int rgb;
		 	
	 		if(s.color) {
	 			rgb = s.c.getRGB();
	 		}else {
	 			rgb = Texture[v][u];
	 		}
	 		if(Color4.getAlpha(rgb) > 0) {
	 			rgb = Color4.blendShadow(rgb, overlay.getRGB());
	 		}
 			rgb = Color4.blend(rgb,Color4.getRGB(0, 0, 0, ao));

		 	setPixel(ZBuffer, x, y, rgb, iz);
		}
	}
	
	private static void setPixel(PixelData[][] ZBuffer, int x, int y, int rgb, double iz) {
	 	PixelData pixel = ZBuffer[x+1][y];  // +1 offset is needed for outlines to be in align
 		synchronized(pixel) {
		 	if(Color4.getAlpha(rgb)==255) {
		 		if(pixel.depth<iz) {
		 			pixel.depth=iz;
		 			pixel.color=rgb;
		 		}
		 	}else {
		 		if(pixel.overlayDepth<iz) {
		 			pixel.overlayDepth=iz;
		 			pixel.overlayColor=rgb;
		 		}
		 	}
 		}
	}
	
	
	private void setupXBuffers(int[] bufferXmin, int[] bufferXmax, UVZ[] bufferUVZmin, UVZ[] bufferUVZmax, UVZ tmpUVZ1, UVZ tmpUVZ2, Game game) {
		for(int i=0;i<clipSize;i++) {
			int index1= i;
			int index2= i != clipSize-1 ? i+1 : 0;
			
			Vector v1 = clip[index1]; 
			Vector v2 = clip[index2];
			
			getUVZ(v1, clipUV[index1], game, tmpUVZ1);
			getUVZ(v2, clipUV[index2], game, tmpUVZ2);
			
			if(!Config.ambientOcclusion) {
				tmpUVZ1.ao=0;
				tmpUVZ2.ao=0;
			}
			
			
			final Point p1 = result[index1];
			final Point p2 = result[index2];
			
			//p1 és p2 meredeksége
			final double m=(p2.y==p1.y) ? 0.0 : (p2.x-p1.x)*1.0/(p2.y-p1.y);
			
			// mindenképpen y pozitív irányban szeretnénk végigmenni a polygon oldalán, de lehet, hogy p2 van feljebb.
			int xmin = (p1.y<p2.y) ? p1.x : p2.x;
			int ymin = (p1.y<p2.y) ? p1.y : p2.y; // y határértékei adott szakaszon
			int ymax = (p1.y<p2.y) ? p2.y : p1.y;
	
			xmin = Util.limit(xmin, 0, Config.getWidth()-1);
			ymin = Util.limit(ymin, 0, Config.getHeight()-1);
			ymax = Util.limit(ymax, 0, Config.getHeight()-1);
			
	
	
			double x  = xmin; //aktuális x érték
	
			
			
			// megkeressük az adott sor bal és jobb szélét, társítunk a két ponthoz UVZ-t is
			// y-t 1-gyel, x-et m-mel léptetjük
			for(int y=ymin;y<ymax;y++) {
				
				// feljebb a limit() nem engedi ezeket
				if(y<this.ymin) {
					Main.err("y<ymin  "+ y + " < "+this.ymin);
					return;
				}
				
				
				if(y-this.ymin>=bufferXmin.length) {
					Main.err("Invalid bufferXmin - y: "+y+", ymin: "+ this.ymin+", length: "+bufferXmin.length+", index:"+(y-this.ymin));
					Main.err(Arrays.toString(bufferXmin));
					return;
				}
				if(x < bufferXmin[y-this.ymin]) {
					bufferXmin[y-this.ymin] = (int)x;
					UVZ.interp(p1, p2, x, y, tmpUVZ1, tmpUVZ2, bufferUVZmin[y-this.ymin]);
				}
				
				if(y-this.ymin>=bufferXmax.length) {
					Main.err("Invalid bufferXmax - y: "+y+", ymin: "+ this.ymin+", length: "+bufferXmax.length+", index:"+(y-this.ymin));
					Main.err(Arrays.toString(bufferXmax));
					return;
				}
				
				if(x > bufferXmax[y-this.ymin]) {
					bufferXmax[y-this.ymin] = (int)x;
					UVZ.interp(p1, p2, x, y, tmpUVZ1, tmpUVZ2, bufferUVZmax[y-this.ymin]);
				}
				
				x+=m;
					
			}
		}
		
	}
	
	private void drawTexturedOutline(TextureRenderThread context) {
		Game game = context.game;
		if(s.c.getAlpha() == 255){
			
			for(int i=0;i<clipSize;i++) {
				
				int index1= i;
				int index2= i != clipSize-1 ? i+1 : 0;
				
				Vector v1 = clip[index1]; 
				Vector v2 = clip[index2];
				
				double iz1 = getIZ(v1, game);
				double iz2 = getIZ(v2, game);
				
				final Point p1 = result[index1];
				final Point p2 = result[index2];
				
				Bresenham.plotLine(p1.x, p1.y, p2.x, p2.y, context.renderer.ZBuffer, iz1, iz2);
				if(Config.getHeight()>=1440) {
					Bresenham.plotLine(p1.x+1, p1.y, p2.x+1, p2.y, context.renderer.ZBuffer, iz1, iz2);
				}

			}
		}
	}
	
	private static void getUVZ(Vector v, double[] uv, Game game, UVZ uvz) {
		// 1/z , u/z , v/z kiszámítása
		// z nem a kamera és a pont távolsága, hanem a kamera helyének, és a pontnak a kamera irányára vetített helyének távolsága
		// (egyszerû skalárszorzat)
		Vector PEPos = game.PE.getPos();
		Vector ViewVector = game.ViewVector;
		double z = (v.x-PEPos.x)*ViewVector.x+(v.y-PEPos.y)*ViewVector.y+(v.z-PEPos.z)*ViewVector.z;
		uvz.iz=1/z;
		uvz.uz=uv[0]/z;
		uvz.vz=uv[1]/z;
		uvz.ao=uv[2]/z;
	}
	
	private static double getIZ(Vector v, Game game) {
		Vector PEPos = game.PE.getPos();
		Vector ViewVector = game.ViewVector;
		double z = (v.x-PEPos.x)*ViewVector.x+(v.y-PEPos.y)*ViewVector.y+(v.z-PEPos.z)*ViewVector.z;
		return 1/z;
	}

	@Override
	protected void draw(Graphics g, Game game){
		Graphics2D g2d=(Graphics2D)g;
		
		
		drawSurface(g2d);
		
		if(Config.ambientOcclusion) {
			drawAO(g2d);
		}
		
		
		boolean drawfog = (Config.fogEnabled && AvgDist > Config.renderDistance*(0.75f));
		if(drawfog){
			drawFog(g2d);
		}else{
			g2d.setColor(Color.BLACK);
		}
		
		
		if(s.c.getAlpha() == 255){
			g2d.drawPolygon(polygon);
		}
		
		
		
		drawSpheres(g2d, game);
		
	}
	
	private void drawSurface(Graphics2D g2d) {
		if(!s.paint) {
			g2d.setColor(lightedcolor.getColor());
			g2d.fillPolygon(polygon);
		} else {
			g2d.setColor(s.c.getColor());
			g2d.fillPolygon(polygon);
			g2d.setPaint(s.p);
			g2d.fillPolygon(polygon);
			Color4 lightedc=getLightOverlay();
			g2d.setColor(lightedc.getColor());
			g2d.fillPolygon(polygon);
		}
	}
	
	private void drawAO(Graphics2D g2d){
		for(GradientPaint p : OcclusionPaints) {
			g2d.setPaint(p);
			g2d.fillPolygon(polygon);
		}
	}
	
	private void drawFog(Graphics2D g2d) {
		float totalFogSize = Config.renderDistance/4f;
		float foggyDist = Config.renderDistance-AvgDist;
		int ratio = Util.limit((int) (255*(foggyDist/totalFogSize)),0,255);

		Color customColor = new Color(AssetLibrary.skyColor.getRed(), AssetLibrary.skyColor.getGreen(), AssetLibrary.skyColor.getBlue(), 255-ratio);
		g2d.setColor(customColor);

		g2d.fillPolygon(polygon);
		g2d.setColor(new Color(0,0,0,ratio));
	}
	
	private void drawSpheres(Graphics2D g2d, Game game) {
		for(Sphere3D sphere : game.Spheres) {
			if(sphere.isPlayerInside(game.PE)) { //player is in the sphere
				// polygon is outside the sphere
				if(!sphere.isModelInside(model)){
					g2d.setColor(sphere.getColor().getColor());
					g2d.fillPolygon(polygon);
				}
			}else { // player is outside the sphere
				//polygon is inside the sphere but closer to the player than the center
				if(sphere.isModelInside(model) && AvgDist < sphere.getCenterDist()) {
					g2d.setColor(sphere.getColor().getColor());
					g2d.fillPolygon(polygon);
				}
			}
		}
		
	}
	
	
	static void renderSelectOutline(Graphics fb, Polygon polygon, Point2D.Double centroid2D){
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
	
	public void clearOcclusions() {
		for(int i=0;i<SimpleOcclusions.length;i++) {
			SimpleOcclusions[i] = false;
		}
		for(int i=0;i<CornerOcclusions.length;i++) {
			CornerOcclusions[i] = false;
		}
		hasAO=false;
		OcclusionPaints.clear();
	}
	
	public void addSimpleOcclusion(BlockFace face) {
		SimpleOcclusions[face.id] = true;
		hasAO=true;
	}
	
	public void addCornerOcclusion(Corner c) {
		CornerOcclusions[c.id] = true;
		hasAO=true;
	}
	
	public void recalcTexturedOcclusions() {
		for(int i=0;i<Vertices.length;i++) {
			UVMap[i][2]=0;
		}
		if(model instanceof Block) {
			BlockFace currentFace = ((Block)model).HitboxPolygons.get(this);
			for(int c=0;c<CornerOcclusions.length;c++) {
				if(CornerOcclusions[c]) {
					UVMap[c][2]=Color4.AO_MAX_TEXTURED.getAlpha()/255.0;
				}
			}
			
			int x = ((Block) model).x;
			int y = ((Block) model).y;
			int z = ((Block) model).z;
			for(int s=0;s<SimpleOcclusions.length;s++) {
				if(!SimpleOcclusions[s]) continue;
				
				BlockFace nearbyFace = BlockFace.values[s];
				float[][] values = GradientCalculator.calculate(currentFace, nearbyFace);
				float[] begin1 = values[0];
				float[] begin = values[1];

				if(begin[0] ==0.5) begin[0]=1-begin1[0];
				if(begin[1] ==0.5) begin[1]=1-begin1[1];
				if(begin[2] ==0.5) begin[2]=1-begin1[2];
				
				
				for(int i=0;i<Vertices.length;i++) {
					if(Vertices[i].equals(x+begin1[0], y+begin1[1], z+begin1[2])) {
						UVMap[i][2]=Color4.AO_MAX_TEXTURED.getAlpha()/255.0;
					}
					if(Vertices[i].equals(x+begin[0], y+begin[1], z+begin[2])) {
						UVMap[i][2]=Color4.AO_MAX_TEXTURED.getAlpha()/255.0;
					}
				}
			}
		}
	}
	
	private void recalcOcclusionPaints(UpdateContext context) {
		
		Block b = (Block) model;
		BlockFace currentFace = b.HitboxPolygons.get(this);
		OcclusionPaints.clear();
		
		for(int s=0;s<SimpleOcclusions.length;s++) {
			if(SimpleOcclusions[s]) {
				BlockFace nearbyFace = BlockFace.values[s];
				GradientCalculator.getGradientOf(b.x, b.y, b.z, currentFace, nearbyFace, context.tmpArr, context.values, context.game);				
				GradientCalculator.getPerpendicular(context.values, context.pointVec, context.lineVec);
				OcclusionPaints.add(new GradientPaint(context.lineVec,	Color4.AO_MAX_FLAT, context.values[2], Color4.TRANSPARENT));
			}
		}
		
		for(int corner=0;corner<CornerOcclusions.length;corner++) {
			if(CornerOcclusions[corner]) {
				
				context.tmpArr[0].set(Vertices[Math.floorMod(corner-1, 4)]);
				context.tmpArr[1].set(Vertices[Math.floorMod(corner+1, 4)]);
				context.tmpArr[2].set(Vertices[Math.floorMod(corner, 4)]);
				
				context.game.convert3Dto2D(context.tmpArr, context.values, 3);
				
				GradientCalculator.getPerpendicular(context.values, context.pointVec, context.lineVec);
				OcclusionPaints.add(new GradientPaint(context.lineVec, Color4.TRANSPARENT, context.values[2], Color4.AO_MAX_FLAT));
			}			
		}
	}


	public void recalc() {
		if(Vertices.length>0) {
			Vector v0 = Vertices[0];
			Vector v1 = Vertices[1];
			Vector v2 = Vertices[2];
			normal.set(v2).substract(v0).CrossProduct(centroid.set(v1).substract(v0));
		}
		
		float dx=0, dy=0, dz=0;
		
	    int pointCount = Vertices.length;
		for(Vector v : Vertices) {
	        dx += v.x;
	        dy += v.y;
	        dz += v.z;
	    }

	    centroid.set(dx/pointCount, dy/pointCount, dz/pointCount);
	}
	
	
	
	
	
	private boolean isAllInvisible(UpdateContext context) {
		Vector ViewVector = context.game.ViewVector;
		Vector PEPos = context.game.PE.getPos();
		float x = ViewVector.x*(-physicalRadius*3)+PEPos.x;
		float y = ViewVector.y*(-physicalRadius*3)+PEPos.y;
		float z = ViewVector.z*(-physicalRadius*3)+PEPos.z;
		context.tmpVector.set(x, y, z);
		return !Util.sphereCone(centroid,
				physicalRadius,
				context.tmpVector,
				context.game.ViewVector,
				context.game.ViewFrustum.bigSinAngle,
				context.game.ViewFrustum.bigTanSq);

	}
	
	private boolean isAllVisible(UpdateContext context) {
		Vector ViewVector = context.game.ViewVector;
		Vector PEPos = context.game.PE.getPos();
		float x = ViewVector.x*(physicalRadius*3)+PEPos.x;
		float y = ViewVector.y*(physicalRadius*3)+PEPos.y;
		float z = ViewVector.z*(physicalRadius*3)+PEPos.z;
		context.tmpVector.set(x, y, z);
		return Util.sphereCone(centroid,
				physicalRadius,
				context.tmpVector,
				context.game.ViewVector,
				context.game.ViewFrustum.smallSinAngle,
				context.game.ViewFrustum.smallTanSq);
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

	private void resetClipsTo(Vector[] vertexArr, double[][] uvArr, int size) {
		for(int i=0;i<size;i++) {
			clip[i].set(vertexArr[i]);
			clipUV[i]=uvArr[i];
		}
		clipSize=size;
	}
	
	private void clip(UpdateContext context, Plane... planes){
		Vector[] clip2 = context.clip2;
		double[][] clipUV2 = context.clipUV2;
		int clip2Size=clipSize;
		int pIndex=0;
		while(pIndex<planes.length && clipSize > 0) {
			Plane P = planes[pIndex];
			clip2Size=0;
			for(int i=0;i<clipSize;i++){
	
				int index1 = i;
				int index2 = (i+1) % clipSize;
				
				Vector a = clip[index1];
				Vector b = clip[index2];
				
				
				float da = a.x * P.normal.x + a.y * P.normal.y + a.z * P.normal.z - P.distance;
				float db = b.x * P.normal.x + b.y * P.normal.y + b.z * P.normal.z - P.distance;
				
				if(da > 0) {
					clip2[clip2Size].set(a);
					if(Config.useTextures)
						clipUV2[clip2Size]=clipUV[index1];
					clip2Size++;
				}
				
				if(da * db < 0) {
					float s = da / (da-db);
					float x = (b.x-a.x) * s + a.x;
					float y = (b.y-a.y) * s + a.y;
					float z = (b.z-a.z) * s + a.z;
					clip2[clip2Size].set(x,y,z);
					if(Config.useTextures) {
						double[] uv1 = clipUV[index1];
						double[] uv2 = clipUV[index2];
						clipUV2[clip2Size] = UVZ.interpUV(a, clip2[clip2Size], b, uv1, uv2);
					}
					clip2Size++;
				}
					
					
			}
			
			resetClipsTo(clip2,clipUV2,clip2Size);
			pIndex++;
		}
	}
	
	
	
	

	@Override
	public String toString() {
		return s+",light:"+getLight()+",simple:"+SimpleOcclusions+",corner:"+CornerOcclusions+","+lightSources;
	}


	
}

