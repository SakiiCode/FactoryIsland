package ml.sakii.factoryisland;

import java.awt.Color;
import java.awt.Paint;
import java.awt.image.BufferedImage;

public class Surface {
	@Override
	public String toString() {
		return "Surface [Texture=" + Texture + ", c=" + c + ", p=" + p + ", color=" + color + ", paint=" + paint + "]";
	}

	public BufferedImage Texture;
	public Color c;
	public Paint p;
	public boolean color;
	public boolean paint;
	
	
	public Surface(Object o) {
		if(o instanceof Color) {
			this.c = (Color)o;
			color = true;
			paint = false;
		}else {
			this.Texture = (BufferedImage)o;
			this.c = averageColor((BufferedImage)o);
			color = false;
			paint = false;
		}
		
	}
	
	public Surface(Color c){
		this.c = c;
		color = true;
		paint = false;
	}
	
	public Surface(BufferedImage img){
		this.Texture = img;
		this.c = averageColor(img);
		color = false;
		paint = false;
	}
	
	public Surface(Color c, Paint p){
		this.c = c;
		color = true;
		this.p = p;
		paint = true;
	}
	
	public Surface(BufferedImage img, Paint p){
		this.Texture = img;
		this.c = averageColor(img);
		color = false;
		this.p = p;
		paint = true;
	}
	
	public Surface copy() {
		if(color) {
			if(paint) {
				return new Surface(c, p);
			}
			return new Surface(c);
		}
		if(paint) {
			return new Surface(Texture, p);
		}
		return new Surface(Texture);
	}
	
	static Color averageColor(BufferedImage bi) {
		int x0=0, y0=0, w=bi.getWidth(), h=bi.getHeight();

	    int x1 = x0 + w;
	    int y1 = y0 + h;
	    int suma=0, sumr = 0, sumg = 0, sumb = 0;
	    for (int x = x0; x < x1; x++) {
	        for (int y = y0; y < y1; y++) {
	            Color pixel = new Color(bi.getRGB(x, y), true);
	            suma += pixel.getAlpha();
	            sumr += pixel.getRed();
	            sumg += pixel.getGreen();
	            sumb += pixel.getBlue();
	        }
	    }
	    int num = w * h;
	    int avgr = sumr / num;
	    int avgg = sumg / num;
	    int avgb = sumb / num;
	    int avga = suma / num;
	    return new Color(avgr, avgg, avgb, avga);
	}

}
