package ml.sakii.factoryisland;

import java.awt.Color;
import java.awt.Paint;
import java.awt.image.BufferedImage;

public class Surface {
	
	public static final Surface EMPTY = new Surface(new Color4(0,0,0,0),null);
	
	@Override
	public String toString() {
		//return "Surface [Texture=" + Texture + ", c=" + c + ", p=" + p + ", color=" + color + ", paint=" + paint + "]";
		return c+""+p;
	}

	public BufferedImage Texture;
	public Color4 c=new Color4();
	public Paint p;
	public boolean color;
	public boolean paint;
	
	//ModBlock-hoz kell
	public Surface(Object o) {
		if(o instanceof Color4) {
			this.c.set((Color4) o);
			color = true;
			paint = false;
		}else if(o instanceof BufferedImage){
			this.Texture = (BufferedImage)o;
			this.c = averageColor((BufferedImage)o);
			color = false;
			paint = false;
		}else if(o instanceof Color) {
			this.c.set(new Color4((Color)o));
			color = true;
			paint = false;
		} else {
			try
			{
				throw new ClassCastException("Invalid Surface initializer:"+o);
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	/*public Surface(Color c){
		this.c = new Color4(c);
		color = true;
		paint = false;
	}
	
	public Surface(Color4 c){
		this.c = c;
		color = true;
		paint = false;
	}
	
	public Surface(BufferedImage img){
		this.Texture = img;
		this.c = averageColor(img);
		color = false;
		paint = false;
	}*/
	
	public Surface(Color4 c, Paint p){
		this.c.set(c);
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
	
	static Color4 averageColor(BufferedImage bi) {
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
	    return new Color4(avgr, avgg, avgb, avga);
	}

}
