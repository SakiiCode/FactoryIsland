package ml.sakii.factoryisland;

import java.awt.Graphics;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import ml.sakii.factoryisland.entities.PlayerMP;

public class Renderer {
	
	private Game game;
	
	int previousSkyLight;
	private Star[] Stars = new Star[200];

	PixelData[][] ZBuffer;

	private Polygon3D SelectedPolygon;

	private AtomicInteger VisibleCounter = new AtomicInteger(0);
	private MaskManager maskManager = new MaskManager();
	
	UVZ[][] uvzArrays;
	boolean[] uvzArrayTaken;
	
	public Renderer(Game game) {
		this.game=game;
		
		uvzArrays = new UVZ[(Runtime.getRuntime().availableProcessors()-1)*3][Config.getHeight()];
		for(int i=0;i<uvzArrays.length;i++)
		for(int j=0;j<uvzArrays[0].length;j++) {
			uvzArrays[i][j] = new UVZ();
		}
		
		uvzArrayTaken = new boolean[(Runtime.getRuntime().availableProcessors()-1)*3];
		
		
		for (int i = 0; i < Stars.length-1; i++)
		{
			Stars[i] = new Star();
		}
		
		Stars[Stars.length-1] = new Star(100);
	}
	
	Polygon3D render(Graphics g, List<Object3D> Objects, List<Sphere3D> Spheres, boolean F3) {
		
		GameEngine Engine = game.Engine;
		PlayerMP PE = game.PE;
		float centerX = game.centerX;
		float centerY = game.centerY;
		

		SelectedPolygon = null;

		Graphics fb = g;

		fb.setColor(AssetLibrary.skyColor);
		fb.fillRect(0, 0, Config.getWidth(), Config.getHeight());

		
		if(!Config.useTextures) {
			Spheres.sort((s1,s2)->Float.compare(s2.getCenterDist(),s1.getCenterDist()));
			for(Sphere3D sphere : Spheres) {
				if(sphere.isPlayerInside(PE)){
					fb.setColor(sphere.getColor().getColor());
					fb.fillRect(0, 0, Config.getWidth(), Config.getHeight());
				}
			}
		}
		
		double timeFraction = Engine.getTimePercent();
		
		Stars[Stars.length-1].pos.set((float)Math.cos(timeFraction*2*Math.PI), 0f, (float)Math.sin(timeFraction*2*Math.PI));
		
		for(Star star : Stars) {
			if(star.update(game)) {
				star.draw(g, game);
			}
		}
	
		if(Config.useTextures && !game.locked) {
			VisibleCounter.set(0);
			Objects.parallelStream().filter(o -> {return o.update(game) && o instanceof BufferRenderable br;}).forEach(o ->{
				Object[] min = getUVZArray(),max = getUVZArray();
				
				int minId = (int) min[0];
				UVZ[] bufferUVZmin=(UVZ[]) min[1];
				int maxId = (int) max[0];
				UVZ[] bufferUVZmax=(UVZ[]) max[1];
				((BufferRenderable)o).drawToBuffer(ZBuffer, game);
				releaseUVZArray(minId);
				releaseUVZArray(maxId);
				
				if(o instanceof Polygon3D p) {
					
					if (p.polygon.contains(centerX, centerY) &&
							((SelectedPolygon == null && p.AvgDist<5) || (SelectedPolygon!=null && p.AvgDist<SelectedPolygon.AvgDist)))
					{
						SelectedPolygon = p;
					}
					if(F3) {
						VisibleCounter.incrementAndGet();
					}
				}
			});

			game.VisibleCount=VisibleCounter.get();
			
			maskManager.clear();
			maskManager.copyParallel(ZBuffer, game.key[6]);
			maskManager.render(fb);


				
			Objects.parallelStream().filter(o->!(o instanceof BufferRenderable)).sorted().forEachOrdered(t ->{
				t.draw(fb, game); //nem kell image-t megadni text3d-hez
			});
			
			
		}else {
			Objects.parallelStream().filter(o -> o.update(game)).sorted().forEachOrdered(o->
			{
				o.draw(fb, game);
				
				if(o instanceof Polygon3D poly) {
					
					if (poly.AvgDist < 5 && poly.polygon.contains(centerX, centerY))
					{
						SelectedPolygon = poly;
					}
					game.VisibleCount++;
				}
			});
		}
		
		return SelectedPolygon;
	}
	
	

	public void resizeScreen(int w, int h) {
		maskManager.resizeScreen(w, h);
		ZBuffer=new PixelData[w+1][h];
		for(int x =0;x<w+1;x++) {
			for(int y=0;y<h;y++) {
				ZBuffer[x][y]=new PixelData();
			}
		}
	}
	
	public Object[] getUVZArray() {
		for(int i=0;i<uvzArrayTaken.length;i++) {
			if(!uvzArrayTaken[i]) {
				uvzArrayTaken[i]=true;
				return new Object[] {i,uvzArrays[i]};
			}
		}
		throw new ArrayIndexOutOfBoundsException();
	}
	
	public void releaseUVZArray(int id) {
		uvzArrayTaken[id]=false;
		
	}
	
	

}
