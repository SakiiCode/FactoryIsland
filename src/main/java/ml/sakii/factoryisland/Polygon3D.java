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
	
	private Vector tmpVector = new Vector();
	
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

	private ArrayList<BlockFace> SimpleOcclusions = new ArrayList<>();
	private ArrayList<Point3D> CornerOcclusions = new ArrayList<>();
	private ArrayList<GradientPaint> OcclusionPaints = new ArrayList<>();

	
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
		recalc(new Vector());
		if(model.Engine != null) { //inditaskor
			recalcLightedColor();
		}
		
		physicalRadius=new Vector().set(centroid).substract(Vertices[1]).getLength(); //feltetelezve h csak teglalap van
	}
	


	@Override
	protected boolean update(Game game, Vector[][] clip2, double[][][] clipUV2){
				
		if(!game.locked) {
			if(game.insideBlock(this) || (Config.useTextures && game.insideSphere(this))) {
				faceFilter = true;
			}else {
				faceFilter = tmpVector.set(Vertices[0]).substract(game.PE.getPos()).DotProduct(normal) < 0;
			}
		}
		
		if(!faceFilter) {
			return false;
		}
			
			
		AvgDist = game.PE.getPos().distance(centroid);
		if(AvgDist > Config.renderDistance) {
			return false;
		}
		
		if(isAllInvisible(game, new Vector())) {
			return false;
		}
		
		resetClipsTo(Vertices, UVMap, Vertices.length);
		if(!isAllVisible(game, new Vector())) {
			clip(clip2, clipUV2, game.ViewFrustum.sides);
		}
			
		if(game.locked){
			clip(clip2, clipUV2, game.ViewFrustum.tmpnear);
		}
		
		if(clipSize == 0){
			return false;
		}
			
		game.convert3Dto2D(clip, result, clipSize);
		
		polygon.reset();
		for(int i=0;i<clipSize;i++) {
			polygon.addPoint(result[i].x, result[i].y);
		}
		
		if(Config.useTextures) {
		
			ymin = result[0].y;
			ymax = result[0].y;
			for(int i=0;i<clipSize;i++) {
				ymin = Math.min(ymin,result[i].y);
				ymax = Math.max(ymax,result[i].y);
			}
			
			ymin=Math.max(ymin, 0);
			ymax=Math.max(ymax, 0);
			ymin=Math.min(ymin, game.FrameBuffer.getHeight());
			ymax=Math.min(ymax, game.FrameBuffer.getHeight());
			
			if(ymin==ymax) {
				return false;
			}
		
		}else if(Config.ambientOcclusion) {
			recalcOcclusionPaints(game);
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
	public void drawToBuffer(PixelData[][] ZBuffer, Game game, UVZ[] bufferUVZmin, UVZ[] bufferUVZmax) {
		
		// buffer init
		int[] bufferXmin = new int[ymax-ymin+2];
		for(int i=0;i<bufferXmin.length;i++) {
			bufferXmin[i]=Config.getWidth()+1;
		}
		int[] bufferXmax = new int[ymax-ymin+2];
		for(int i=0;i<bufferXmax.length;i++) {
			bufferXmax[i]=-1;
		}
		
		UVZ tmpUVZ1 = new UVZ();
		UVZ tmpUVZ2 = new UVZ();
		

		//vertexről vertexre körbemegyünk
		for(int i=0;i<clipSize;i++) {
			
			int index1= i;
			int index2= i != clipSize-1 ? i+1 : 0;
			
			Vector v1 = clip[index1]; 
			Vector v2 = clip[index2];
			
			getUVZ(tmpVector.set(v1), clipUV[index1], game, tmpUVZ1);
			getUVZ(tmpVector.set(v2), clipUV[index2], game, tmpUVZ2);
			
			
			final Point p1 = result[index1];
			final Point p2 = result[index2];
			
			//p1 és p2 meredeksége
			final double m=(p2.y==p1.y) ? 0.0 : (p2.x-p1.x)*1.0/(p2.y-p1.y);
			
			// mindenképpen y pozitív irányban szeretnénk végigmenni a polygon oldalán, de lehet, hogy p2 van feljebb.
			final int xmin = (p1.y<p2.y) ? p1.x : p2.x;
			final int ymin = (p1.y<p2.y) ? p1.y : p2.y; // y határértékei adott szakaszon
			final int ymax = (p1.y<p2.y) ? p2.y : p1.y;

			
			


			double x  = xmin; //aktuális x érték

			
			
			// megkeressük az adott sor bal és jobb szélét, társítunk a két ponthoz UVZ-t is
			// y-t 1-gyel, x-et m-mel léptetjük
			for(int y=ymin;y<ymax;y++) {
				
				// TODO ezek ki lettek javitva de debuggolashoz jo lehet
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
			double Sao=0;
			if(Config.ambientOcclusion) {
				Sao = Util.getSlope(xmin, xmax, uvzmin.ao, uvzmax.ao);
			}
			
			

			int xmin2=Math.max(xmin, 0);
			int xmax2=Math.min(xmax, Config.getWidth());
					
			for(int x=xmin2;x<xmax2;x++)
			{
			 
		 		double iz=Util.interpSlope(xmin, x, uvzmin.iz, Siz);
		 		//TODO megnezni miert van eltolva x+1
		 		synchronized(ZBuffer[x+1][y]) {
				 	
				 	double uz=Util.interpSlope(xmin, x, uvzmin.uz, Suz);
				 	double vz=Util.interpSlope(xmin, x, uvzmin.vz, Svz);
				 	
				 	double u=uz/iz;
				 	double v=vz/iz;
				 	
				 	
				 	double aoz=0, ao=0;
				 	if(Config.ambientOcclusion) {
				 		aoz=Util.interpSlope(xmin, x, uvzmin.ao, Sao);
				 		ao=Util.limit(aoz/iz,0,1);
				 	}
				 	
				 	int rgb;
				 	
				 	try {
				 		int px;
				 		if(s.color) {
				 			px=s.c.getRGB();
				 		}else {
				 			int u2 = Util.limit((int)u, 0, s.Texture.getWidth()-1);
				 			int v2 = Util.limit((int)v, 0, s.Texture.getHeight()-1);
				 			px=s.Texture.getRGB(u2, v2);
				 		}
				 		//px=px|0xFF000000;
				 		rgb = Color4.blend(px, overlay.getRGB());
				 		if(Config.ambientOcclusion) {
				 			rgb=Color4.blend(rgb,Color4.getRGB(0, 0, 0, (int)(ao*255)));
				 		}


				 	}catch(Exception e) {
				 		System.out.println(e.getMessage()+" (u:"+u+",v:"+v+",ao:"+ao);
				 		rgb=0xFF000000;
				 	}
				 	
				 	if(Color4.getAlpha(rgb)==255) {
				 		if(ZBuffer[x+1][y].depth<iz) {
				 			ZBuffer[x+1][y].depth=iz;
				 			ZBuffer[x+1][y].color=rgb;
				 		}
				 	}else {
				 		if(ZBuffer[x+1][y].overlayDepth<iz) {
				 			ZBuffer[x+1][y].overlayDepth=iz;
				 			ZBuffer[x+1][y].overlayColor=rgb;
				 		}
				 	}
			 		
		 		}
			}
				
		}
		
		if(s.c.getAlpha() == 255){
			
			for(int i=0;i<clipSize;i++) {
				
				int index1= i;
				int index2= i != clipSize-1 ? i+1 : 0;
				
				Vector v1 = clip[index1]; 
				Vector v2 = clip[index2];
				
				getUVZ(tmpVector.set(v1), clipUV[index1], game, tmpUVZ1);
				getUVZ(tmpVector.set(v2), clipUV[index2], game, tmpUVZ2);
				
				final Point p1 = result[index1];
				final Point p2 = result[index2];
				
				Bresenham.plotLine(p1.x, p1.y, p2.x, p2.y, ZBuffer, tmpUVZ1.iz, tmpUVZ2.iz);
			}
			
			if(Config.getHeight()>=1440) {
				for(int i=0;i<clipSize;i++) {
					
					int index1= i;
					int index2= i != clipSize-1 ? i+1 : 0;
					
					Vector v1 = clip[index1]; 
					Vector v2 = clip[index2];
					
					getUVZ(tmpVector.set(v1), clipUV[index1], game, tmpUVZ1);
					getUVZ(tmpVector.set(v2), clipUV[index2], game, tmpUVZ2);
					
					final Point p1 = result[index1];
					final Point p2 = result[index2];
					
					Bresenham.plotLine(p1.x+1, p1.y, p2.x+1, p2.y, ZBuffer, tmpUVZ1.iz, tmpUVZ2.iz);
				}	
			}
		}
	}
	
	private static void getUVZ(Vector v, double[] uv, Game game, UVZ uvz) {
		// 1/z , u/z , v/z kiszámítása
		// z nem a kamera és a pont távolsága, hanem a kamera helyének, és a pontnak a kamera irányára vetített helyének távolsága
		// (egyszerû skalárszorzat)
		double z = v.substract(game.PE.getPos()).DotProduct(game.ViewVector);
		uvz.iz=1/z;
		uvz.uz=uv[0]/z;
		uvz.vz=uv[1]/z;
		uvz.ao=uv[2]/z;
	}

	@Override
	protected void draw(Graphics g, Game game){
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
		SimpleOcclusions.clear();
		CornerOcclusions.clear();
	}
	
	public void addSimpleOcclusion(BlockFace face) {
		SimpleOcclusions.add(face);
	}
	
	public void addCornerOcclusion(Point3D p) {
		CornerOcclusions.add(p);
	}
	
	public void recalcTexturedOcclusions() {
		for(int i=0;i<Vertices.length;i++) {
			UVMap[i][2]=0;
		}
		if(model instanceof Block) {
			BlockFace currentFace = ((Block)model).HitboxPolygons.get(this);
			for(Point3D p : CornerOcclusions) {
				Corner c = Corner.fromDelta(currentFace, p);
				UVMap[c.id][2]=Color4.AO_MAX_TEXTURED.getAlpha()/255.0;
			}
			
			int x = ((Block) model).x;
			int y = ((Block) model).y;
			int z = ((Block) model).z;
			
			for(BlockFace nearbyFace : SimpleOcclusions) {
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
	
	private void recalcOcclusionPaints(Game game) {
		if(!(model instanceof Block)){
			return;
		}
		Block b = (Block) model;
		BlockFace currentFace = b.HitboxPolygons.get(this);
		OcclusionPaints.clear();
		
		Vector2D lineVec = new Vector2D();
		Vector2D pointVec = new Vector2D();
		
		for(BlockFace nearbyFace : SimpleOcclusions) {
			Point2D.Float[] values = GradientCalculator.getGradientOf(b.x, b.y, b.z, currentFace, nearbyFace, game);				
			
			GradientCalculator.getPerpendicular(values, pointVec, lineVec);

			OcclusionPaints.add(new GradientPaint(lineVec,	Color4.AO_MAX_FLAT, values[2], Color4.TRANSPARENT));
		}
		

		
		BlockFace face = currentFace;
		
		Point2D.Float[] values = new Point2D.Float[3];
		for(int i=0;i<values.length;i++) {
			values[i] = new Point2D.Float();
		}
		
		Vector[] input = new Vector[3];
		for(int i=0;i<values.length;i++) {
			input[i] = new Vector();
		}
		
		for(Point3D delta : CornerOcclusions) {
			
			
			Corner c = Corner.fromDelta(face, delta);
			
			
			int corner = c.toInt();
			
			input[0].set(Vertices[Math.floorMod(corner-1, 4)]);
			input[1].set(Vertices[Math.floorMod(corner+1, 4)]);
			input[2].set(Vertices[Math.floorMod(corner, 4)]);
			game.convert3Dto2D(input, values, 3);
			
			
			GradientCalculator.getPerpendicular(values, pointVec, lineVec);
			

			OcclusionPaints.add(new GradientPaint(lineVec, Color4.TRANSPARENT, values[2], Color4.AO_MAX_FLAT));
			
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
		for(Vector v : Vertices) {
	        dx += v.x;
	        dy += v.y;
	        dz += v.z;
	    }

	    this.centroid.set(dx/pointCount, dy/pointCount, dz/pointCount);
	}
	
	
	
	
	
	private boolean isAllInvisible(Game game, Vector tmpVector2) {
		Vector pos = tmpVector.set(game.ViewVector).multiply(-physicalRadius*3).add(game.PE.getPos());
		return !Util.sphereCone(centroid, physicalRadius, pos, game.ViewVector, game.ViewFrustum.bigSinAngle, game.ViewFrustum.bigTanSq, tmpVector2);

	}
	
	private boolean isAllVisible(Game game, Vector tmpVector2) {
		Vector pos = tmpVector.set(game.ViewVector).multiply(physicalRadius*3).add(game.PE.getPos());
		return Util.sphereCone(centroid, physicalRadius, pos, game.ViewVector, game.ViewFrustum.smallSinAngle, game.ViewFrustum.smallTanSq, tmpVector2);
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
	
	private void clip(Vector[][] clip2, double[][][] clipUV2, Plane... planes){
		
		
		
		int clip2Size=clipSize;
		
		/*Vector[][] clip2 = new Vector[planes.length+1][8];
		double[][][] clipUV2=new double[planes.length+1][8][3];

		for(int i=0;i<clip2.length;i++) {
			for(int j=0;j<clip2[0].length;j++) {
				clip2[i][j]=new Vector();
			}
		}*/
		
		for(int j=0;j<clipSize;j++) {
			clip2[0][j].set(clip[j]);
			clipUV2[0][j]=clipUV[j];
		}
		
		int pIndex=0;
		while(pIndex<planes.length && clip2Size > 0) {
			Plane P = planes[pIndex];

			clip2Size = clip(clip2[pIndex],clipUV2[pIndex],clip2[pIndex+1],clipUV2[pIndex+1],P,clip2Size,tmpVector);
			pIndex++;
		}
		resetClipsTo(clip2[pIndex],clipUV2[pIndex],clip2Size);

	}
	
	private static int clip(Vector[] vecInput, double[][] uvInput, Vector[] vecOutput, double[][] uvOutput, Plane P, int inputSize, Vector tmpVector) {
		int clip3Size=0;
		for(int i=0;i<inputSize;i++){

			int index1 = i;
			int index2 = (i+1) % inputSize;
			
			Vector a = vecInput[index1];
			Vector b = vecInput[index2];
			
			float da = a.DotProduct(P.normal) - P.distance;
			float db = b.DotProduct(P.normal) - P.distance;
			
			
			if(da > 0) {
				vecOutput[clip3Size].set(a);
				if(Config.useTextures)
					uvOutput[clip3Size]=uvInput[index1];
				clip3Size++;
			}
			
			if(da * db < 0) {
				float s= da/(da-db);
				vecOutput[clip3Size].set(tmpVector.set(b).substract(a).multiply(s).add(a));
				if(Config.useTextures) {
					double[] uv1 = uvInput[index1];
					double[] uv2 = uvInput[index2];
					uvOutput[clip3Size] = UVZ.interpUV(a, tmpVector, b, uv1, uv2);
					
				}
				clip3Size++;
			}
		}
		return clip3Size;
	}
	
	
	

	@Override
	public String toString() {
		return s+",light:"+getLight()+",simple:"+SimpleOcclusions+",corner:"+CornerOcclusions+","+lightSources;
	}


	
}

