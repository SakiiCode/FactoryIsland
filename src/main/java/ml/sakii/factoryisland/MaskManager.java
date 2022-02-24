package ml.sakii.factoryisland;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class MaskManager {
	private ArrayList<BufferedImage> buffers;
	int threads;
	int tileWidth;
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
	
	void setRGB(int x, int y, int color) {
		
		
		int mask = x/tileWidth;
		if(mask>=buffers.size()) {
			Main.err("x:"+x+",y:"+y+",mask:"+mask+",threads:"+threads);
		}else {
			buffers.get(mask).setRGB(x % tileWidth, y, color);
		}
	}
	
	void render(Graphics g) {
		int x=0;
		int diff = tileWidth;
		for(BufferedImage im : buffers) {
			g.drawImage(im, x, 0, diff, (int)(Main.Height*Config.resolutionScaling),  null);
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
