package ml.sakii.factoryisland;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import ml.sakii.factoryisland.entities.PlayerMP;

public class Renderer {
	
	private Game game;
	
	private Star[] Stars = new Star[200];

	PixelData[][] ZBuffer;

	Polygon3D SelectedPolygon;

	private int VisibleCounter;
	private MaskManager maskManager = new MaskManager();
	private UpdateContext updateContext;
	
	ArrayList<ObjectUpdateThread> flatTasks = new ArrayList<>();
	ArrayList<TextureRenderThread> texturedTasks = new ArrayList<>();
	ArrayList<Object3D> filtered = new ArrayList<>();
	
	public Renderer(Game game) {
		this.game = game;
		this.updateContext = new UpdateContext(game);
		
		
		
		for (int i = 0; i < Stars.length-1; i++)
		{
			Stars[i] = new Star();
		}
		
		Stars[Stars.length-1] = new Star(100);
		
		ForkJoinPool pool = ForkJoinPool.commonPool();
		int threadCount = pool.getParallelism();
		for(int i=0;i<threadCount;i++) {
			flatTasks.add(new ObjectUpdateThread(game, i,threadCount));
		}
		for(int i=0;i<threadCount;i++) {
			texturedTasks.add(new TextureRenderThread(game, this, i,threadCount));
		}
		filtered = new ArrayList<>(8000);

	}
	
	Polygon3D render(Graphics g, List<Object3D> Objects, List<Sphere3D> Spheres) {
		
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
		
		if(Config.fogEnabled) {
			Stars[Stars.length-1].update(updateContext);
			Stars[Stars.length-1].draw(g,game);
		}else {
			for(Star star : Stars) {
				if(star.update(updateContext)) {
					star.draw(g, game);
				}
			}
		}
	
		if(Config.useTextures && !game.locked) {
			clearZBuffer();
			VisibleCounter = 0;
			
			updateRenderTextured();
			
			game.VisibleCount = VisibleCounter;
			
			maskManager.clear();
			maskManager.copyParallel(ZBuffer, game.key[6]);
			maskManager.render(fb);


				
			Objects.parallelStream().filter(o->!(o instanceof BufferRenderable)).sorted().forEachOrdered(t ->{
				t.draw(fb, game); //nem kell image-t megadni text3d-hez
			});
			
			
		}else {
				filtered.clear();
				
				updateFlat();
				
				Collections.sort(filtered);
				for(Object3D o : filtered) {
					o.draw(fb, game);
					
					if(o instanceof Polygon3D poly) {
						
						if (poly.AvgDist < 5 && poly.polygon.contains(centerX, centerY))
						{
							SelectedPolygon = poly;
						}
					}
				}
				game.VisibleCount=filtered.size();
		}
		
		return SelectedPolygon;
	}
	
	private void updateFlat() {
		for(ObjectUpdateThread task : flatTasks) {
			task.reinitialize();
			task.fork();
		}
		
		for(ObjectUpdateThread task : flatTasks) {
			synchronized(filtered) {
				filtered.addAll(task.join());
			}
		}
	}
	
	private void updateRenderTextured() {
		for(TextureRenderThread task : texturedTasks) {
			task.reinitialize();
			task.fork();
		}
		
		for(TextureRenderThread task : texturedTasks) {
			VisibleCounter += task.join();
		}
	}
	
	

	public void resizeScreen(int w, int h) {
		maskManager.resizeScreen(w, h);
		ZBuffer=new PixelData[w+1][h];
		for(int x =0;x<w+1;x++) {
			for(int y=0;y<h;y++) {
				ZBuffer[x][y]=new PixelData();
			}
		}
		for(TextureRenderThread texturedTask : texturedTasks) {
			texturedTask.resizeScreen();
		}
	}
	
	private void clearZBuffer() {
		for(int x=0;x<ZBuffer.length;x++) {
			
			for(int y=0;y<ZBuffer[x].length;y++) {
				ZBuffer[x][y].reset();
			}
		}
	}
	
	

}
