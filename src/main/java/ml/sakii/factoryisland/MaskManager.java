package ml.sakii.factoryisland;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.stream.IntStream;

public class MaskManager {
	private ArrayList<BufferedImage> buffers;
	private int threads;
	private int tileWidth;
	private int tileHeight;
	
	private AlphaComposite transparency = AlphaComposite.getInstance(AlphaComposite.CLEAR);
	
	public MaskManager() {
		buffers = new ArrayList<>();
	}
	
	
	void resizeScreen(int w, int h) {
		threads = Math.max(1, Runtime.getRuntime().availableProcessors()-1);
		buffers.clear();
		tileWidth = (int) Math.ceil(w*1f/threads);
		tileHeight = h;
		for(int i=0;i<threads;i++) {
			buffers.add(Main.toCompatibleImage(new BufferedImage(tileWidth, h, BufferedImage.TYPE_INT_ARGB)));
		}
	}
	
	private void setRGB(int x, int y, int color) {
		
		
		int mask = x/tileWidth;
		if(mask>=buffers.size()) {
			Main.err("x:"+x+",y:"+y+",mask:"+mask+",threads:"+threads);
		}else {
			buffers.get(mask).setRGB(x % tileWidth, y, color);
		}
	}
	
	void copyParallel(PixelData[][] ZBuffer, boolean depthMap) {
		IntStream.range(0, threads).parallel()
		.forEach(id -> {
			int start = id*tileWidth;
			int end = Math.min(ZBuffer.length, start+tileWidth);
			for(int x=start;x<end;x++) {
				
				for(int y=0;y<ZBuffer[x].length;y++) {
					if(depthMap) {
						int px=(int) Math.round(255*(Math.pow(1-ZBuffer[x][y].depth, 6)));
					 	int rgb = (255 << 24) | (px << 16) | (px << 8) | px;
						setRGB(x, y, rgb);
					}else {
						
						int color =ZBuffer[x][y].color; 
						if(ZBuffer[x][y].overlayColor!=0 && ZBuffer[x][y].overlayDepth>ZBuffer[x][y].depth) {
							color=Color4.blend(color, ZBuffer[x][y].overlayColor);
						}
						if(color != 0) { //color not set => transparent. Black is 0xFF000000
							setRGB(x, y, color);
						}
						
					}
					//ZBuffer[x][y].reset();
	
				}
			}
		});
	}
	
	void render(Graphics g) {
		int x=0;
		int diff = tileWidth;
		
		for(BufferedImage im : buffers) {
			g.drawImage(im, x, 0, diff, Config.getHeight(),  null);
			x+=diff;
		}
	}
	
	void clear() {
		for(BufferedImage im : buffers) {
			Graphics g = im.getGraphics();
			((Graphics2D) g).setComposite(transparency);
			g.fillRect(0, 0, tileWidth, tileHeight);
		}
	}
	
	
}
