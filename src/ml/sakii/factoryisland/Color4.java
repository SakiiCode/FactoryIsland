package ml.sakii.factoryisland;

import java.awt.Color;

public class Color4 extends Color{
	private static final long serialVersionUID = -4015148584668646478L;

	//public int r, g, b, a;
	
	public Color4(float r, float g, float b) {
		super(r, g, b);
	}
	
	public Color4(int r, int g, int b) {
		super(r, g, b);
	}
	
	public Color4(float r, float g, float b, float a) {
		super(r, g, b, a);
	}
	
	public Color4(int r, int g, int b, int a) {
		super(r, g, b, a);
	}
	
	
	public Color4(Color c, float alpha){
		super(c.getRed(),c.getGreen(), c.getBlue(), (int) (alpha*255));
		
	}
	
	public Color4(Color c, int alpha){
		super(c.getRed(),c.getGreen(), c.getBlue(), alpha);

	}
	
	
	public Color4(Color c1) {
		super(c1.getRGB());
	}

	public Color4 blend(Color4 c1) {
		double weight0 = getAlpha()/255d;
		double weight1 = 1-(getAlpha()/255d);
		  
		  
		double r = this.getRed() * weight0 + c1.getRed() * weight1;
		double g = this.getGreen() * weight0 + c1.getGreen() * weight1;
		double b = this.getBlue() * weight0 + c1.getBlue() * weight1;
		double a = Math.min(this.getAlpha() + c1.getAlpha(),255);

		return new Color4((int) r, (int) g, (int) b, (int) a);
	}
	
	public Color4 blend2(Color4 c1) {
		Color4 c0=this;
		float a1=c1.getAlpha()/255f;
		float a0=1-a1;
		float r0=c0.getRed()/255f;
		float r1=c1.getRed()/255f;
		float g0=c0.getGreen()/255f;
		float g1=c1.getGreen()/255f;
		float b0=c0.getBlue()/255f;
		float b1=c1.getBlue()/255f;
		
		float a01 = (1 - a0)*a1 + a0;

		float r01 = ((1 - a0)*a1*r1 + a0*r0) / a01;

		float g01 = ((1 - a0)*a1*g1 + a0*g0) / a01;

		float b01 = ((1 - a0)*a1*b1 + a0*b0) / a01;

		return new Color4(r01, g01, b01, 1);
	}
	
	public Color4 blend3(Color4 c1) {
		Color4 c0=this;
		float a0=c0.getAlpha()/255f;
		float a1=c1.getAlpha()/255f;
		float r0=c0.getRed()/255f;
		float r1=c1.getRed()/255f;
		float g0=c0.getGreen()/255f;
		float g1=c1.getGreen()/255f;
		float b0=c0.getBlue()/255f;
		float b1=c1.getBlue()/255f;
		
		float a01 = (1 - a0)*a1 + a0;

		float r01 = ((1 - a0)*a1*r1 + a0*r0) / a01;

		float g01 = ((1 - a0)*a1*g1 + a0*g0) / a01;

		float b01 = ((1 - a0)*a1*b1 + a0*b0) / a01;

		return new Color4(r01, g01, b01, a01);
	}
		
  
	public Color4 blend(Color c1) {
		return blend(new Color4(c1));
	}
		
	
	public Color4 interpolate(Color4 c1, double ratio) {
	  
		double r = this.getRed() + (c1.getRed() - this.getRed()) * ratio;
		double g = this.getGreen() + (c1.getGreen() - this.getGreen()) * ratio;
		double b = this.getBlue() + (c1.getBlue() - this.getBlue()) * ratio;
		double a = this.getAlpha() + (c1.getAlpha() - this.getAlpha()) * ratio;

		return new Color4((int) r, (int) g, (int) b, (int) a);
	}
	

	
	@Override
	public String toString(){
		return "Color4("+getRed()+","+getGreen()+","+getBlue()+","+getAlpha()+")";
		
	}
	  
}
