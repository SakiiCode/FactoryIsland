package ml.sakii.factoryisland;

import java.awt.Color;

public class Color4 {
	
	private int r=0, g=0, b=0, a=0;
	private Color cache=null;
	private boolean dirty;
    
	public static final Color TRANSPARENT = new Color(0, 0, 0, 0);
	public static final Color AO_MAX_FLAT = new Color(0, 0, 0, 0.4f);
	public static final Color AO_MAX_TEXTURED = new Color(0, 0, 0, 0.6f);
	public static final Color CROSSHAIR_COLOR = new Color(1.0f, 1.0f, 1.0f, 0.2f);

	
	public Color4(float r, float g, float b) {
		construct((int)(r*255), (int)(g*255), (int)(b*255), 255);
	}
	
	public Color4(int r, int g, int b) {
		construct(r, g, b, 255);
	}
	
	public Color4(float r, float g, float b, float a) {
		construct((int)(r*255), (int)(g*255), (int)(b*255), (int)(a*255));
	}
	
	public Color4(int r, int g, int b, int a) {
		construct(r, g, b, a);
	}
	
	public Color4(Color c){
		construct(c.getRed(),c.getGreen(), c.getBlue(), c.getAlpha());
	}
	
	public Color4() {
		construct(0,0,0,0);
	}
	
	private void construct(int r, int g, int b, int a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
		dirty=true;
	}
	
	
	public int getAlpha() {
		return a;
	}	
	public int getRed() {
		return r;
	}
	public int getGreen() {
		return g;
	}
	public int getBlue() {
		return b;
	}
	
	public Color getColor() {
		if(dirty) {
			cache = new Color(r,g,b,a);
			dirty=false;
		}
		return cache;
	}
		

	
	public Color4 blend(Color4 c0) { //efölé c0
		float a0=c0.getAlpha()/255f;
		float r0=c0.getRed()/255f;
		float g0=c0.getGreen()/255f;
		float b0=c0.getBlue()/255f;
		return blend(r0, g0, b0, a0);
	}
		
	public static int blendShadow(int source, int dest) {
		float b0 = ((dest)&0xFF)/255f;
		float g0 = ((dest>>8)&0xFF)/255f;
		float r0 = ((dest>>16)&0xFF)/255f;
		float a0 = ((dest>>24)&0xFF)/255f;
		
		float b1 = ((source)&0xFF)/255f;
		float g1 = ((source>>8)&0xFF)/255f;
		float r1 = ((source>>16)&0xFF)/255f;
		float a1 = ((source>>24)&0xFF)/255f;
		

		
		float a01 = (1 - a0)*a1 + a0;

		float r01 = ((1 - a0)*a1*r1 + a0*r0) / a01;

		float g01 = ((1 - a0)*a1*g1 + a0*g0) / a01;

		float b01 = ((1 - a0)*a1*b1 + a0*b0) / a01;

		
		int red = ((int)(r01*255)<<16)& 0x00FF0000;
		int green = ((int)(g01*255)<<8) & 0x0000FF00; //Shift Green 8-bits and mask out other stuff
	    int blue = ((int)(b01*255)) & 0x000000FF; //Mask out anything not blue.
	    int alpha = ((int)(a1*255)<<24) & 0xFF000000; 
		
		return alpha | red | green | blue;
		

		
	}
	
	public static int blend(int source, int dest) {
		float b0 = ((dest)&0xFF)/255f;
		float g0 = ((dest>>8)&0xFF)/255f;
		float r0 = ((dest>>16)&0xFF)/255f;
		float a0 = ((dest>>24)&0xFF)/255f;
		
		float b1 = ((source)&0xFF)/255f;
		float g1 = ((source>>8)&0xFF)/255f;
		float r1 = ((source>>16)&0xFF)/255f;
		float a1 = ((source>>24)&0xFF)/255f;
		

		
		float a01 = (1 - a0)*a1 + a0;

		float r01 = ((1 - a0)*a1*r1 + a0*r0) / a01;

		float g01 = ((1 - a0)*a1*g1 + a0*g0) / a01;

		float b01 = ((1 - a0)*a1*b1 + a0*b0) / a01;

		
		int red = ((int)(r01*255)<<16)& 0x00FF0000;
		int green = ((int)(g01*255)<<8) & 0x0000FF00; //Shift Green 8-bits and mask out other stuff
	    int blue = ((int)(b01*255)) & 0x000000FF; //Mask out anything not blue.
	    int alpha = ((int)(a01*255)<<24) & 0xFF000000; 
		
		return alpha | red | green | blue;
		

		
	}
	
	public Color4 blend(int argb) {
		int b = (argb)&0xFF;
		int g = (argb>>8)&0xFF;
		int r = (argb>>16)&0xFF;
		int a = (argb>>24)&0xFF;
		return blend(r/255f, g/255f, b/255f, a/255f);
	}
	
	private Color4 blend(float r0, float g0, float b0, float a0) {
		Color4 c1=this;
		float a1=c1.getAlpha()/255f;
		float r1=c1.getRed()/255f;
		float g1=c1.getGreen()/255f;
		float b1=c1.getBlue()/255f;
		
		float a01 = (1 - a0)*a1 + a0;

		float r01 = ((1 - a0)*a1*r1 + a0*r0) / a01;

		float g01 = ((1 - a0)*a1*g1 + a0*g0) / a01;

		float b01 = ((1 - a0)*a1*b1 + a0*b0) / a01;

		if(r01>1) {
			Main.err(r01+","+g01+","+b01+","+a01);
		}
		this.set(r01, g01, b01, a01);
		
		return this;
	}
  
		
	public Color4 set(Color4 c) {
		//construct(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
		if(c.r != r || c.g != g || c.b != b || c.a != a) {
			dirty=true;
			r=c.r;
			g=c.g;
			b=c.b;
			a=c.a;
		}
		return this;
	}
	
	public Color4 set(Color c) {
		//construct(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
		if(c.getRed() != r || c.getGreen() != g || c.getBlue() != b || c.getAlpha() != a) {
			dirty=true;
			r=c.getRed();
			g=c.getGreen();
			b=c.getBlue();
			a=c.getAlpha();
		}
		return this;
	}
	
	public Color4 set(int argb) {
		int b = (argb)&0xFF;
		int g = (argb>>8)&0xFF;
		int r = (argb>>16)&0xFF;
		int a = (argb>>24)&0xFF;
		if(this.r != r || this.g != g || this.b != b || this.a != a) {
			dirty=true;
			this.r=r;
			this.g=g;
			this.b=b;
			this.a=a;
		}
		//construct(r, g, b, a);
		return this;
	}
	
	public Color4 set(float r, float g, float b, float a) {
		int r2 = (int)(r*255);
		int g2 = (int)(g*255);
		int b2 = (int)(b*255);
		int a2 = (int)(a*255);
		if(this.r != r2 || this.g != g2 || this.b != b2 || this.a != a2) {
			dirty=true;
			this.r=r2;
			this.g=g2;
			this.b=b2;
			this.a=a2;
		}
		//construct((int)(r*255), (int)(g*255), (int)(b*255), (int)(a*255));
		return this;
	}
	
	public int getRGB() {
		return (a << 24) | (r << 16) | (g << 8) | b;
	}
	
	
	
	public static int getAlpha(int argb) {
		return (argb>>24)&0xFF;
	}
	
	
	@Override
	public String toString(){
		return "Color4("+getRed()+","+getGreen()+","+getBlue()+","+getAlpha()+","+cache+")";
		
	}

	public Color4 setAlpha(int pow)
	{
		//construct(r, g, b, pow);
		if(a!=pow) {
			dirty=true;
			a=pow;
		}
		return this;
	}
	
	public static int getRGB(int r, int g, int b, int a) {
		return (a << 24) | (r << 16) | (g << 8) | b;
	}

	
	  
}
