package ml.sakii.factoryisland;

import java.awt.Color;

public class Color4 {
	//private static final long serialVersionUID = -4015148584668646478L;

	private int r, g, b, a;
	private Color buffer;
    private static final double FACTOR = 0.7;
	
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
	
	private void construct(int r, int g, int b, int a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
		this.buffer=new Color(r, g, b, a);
	}
	
	public Color4(Color c){
		construct(c.getRed(),c.getGreen(), c.getBlue(), c.getAlpha());

	}

	
	/*public Color4(Color c, float alpha){
		construct(c.getRed(),c.getGreen(), c.getBlue(), (int) (alpha*255));
		
	}
	
	public Color4(Color c, int alpha){
		construct(c.getRed(),c.getGreen(), c.getBlue(), alpha);

	}*/
	
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
		return buffer;
	}
		

	
	public Color4 blend(Color4 c0) { //efölé c0
		float a0=c0.getAlpha()/255f;
		float r0=c0.getRed()/255f;
		float g0=c0.getGreen()/255f;
		float b0=c0.getBlue()/255f;
		return blend(r0, g0, b0, a0);
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

		construct((int)(r01*255), (int)(g01*255), (int)(b01*255), (int)(a01*255));
		
		return this;
	}
  
		
	public Color4 set(Color4 c) {
		construct(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
		return this;
		//this.buffer = new Color(this)
	}
	
	public Color4 set(Color c) {
		construct(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
		return this;
	}
	
	public Color4 set(int argb) {
		int b = (argb)&0xFF;
		int g = (argb>>8)&0xFF;
		int r = (argb>>16)&0xFF;
		int a = (argb>>24)&0xFF;
		construct(r, g, b, a);
		return this;
	}
	
	public int getRGB() {
		return buffer.getRGB();
	}
	
	public Color4() {
		this.buffer = Color.BLACK;
	}
	

	
	/**
     * Creates a new <code>Color</code> that is a brighter version of this
     * <code>Color</code>.
     * <p>
     * This method applies an arbitrary scale factor to each of the three RGB
     * components of this <code>Color</code> to create a brighter version
     * of this <code>Color</code>.
     * The {@code alpha} value is preserved.
     * Although <code>brighter</code> and
     * <code>darker</code> are inverse operations, the results of a
     * series of invocations of these two methods might be inconsistent
     * because of rounding errors.
     * @return     a new <code>Color</code> object that is
     *                 a brighter version of this <code>Color</code>
     *                 with the same {@code alpha} value.
     * @see        java.awt.Color#darker
     * @since      JDK1.0
     */
	public Color4 brighter() {
        int r = getRed();
        int g = getGreen();
        int b = getBlue();
        int alpha = getAlpha();

        /* From 2D group:
         * 1. black.brighter() should return grey
         * 2. applying brighter to blue will always return blue, brighter
         * 3. non pure color (non zero rgb) will eventually return white
         */
        int i = (int)(1.0/(1.0-FACTOR));
        if ( r == 0 && g == 0 && b == 0) {
            construct(i, i, i, alpha);
            return this;
        }
        if ( r > 0 && r < i ) r = i;
        if ( g > 0 && g < i ) g = i;
        if ( b > 0 && b < i ) b = i;

        construct(Math.min((int)(r/FACTOR), 255),
                         Math.min((int)(g/FACTOR), 255),
                         Math.min((int)(b/FACTOR), 255),
                         alpha);
        
        return this;
    }
	

    /**
     * Creates a new <code>Color</code> that is a darker version of this
     * <code>Color</code>.
     * <p>
     * This method applies an arbitrary scale factor to each of the three RGB
     * components of this <code>Color</code> to create a darker version of
     * this <code>Color</code>.
     * The {@code alpha} value is preserved.
     * Although <code>brighter</code> and
     * <code>darker</code> are inverse operations, the results of a series
     * of invocations of these two methods might be inconsistent because
     * of rounding errors.
     * @return  a new <code>Color</code> object that is
     *                    a darker version of this <code>Color</code>
     *                    with the same {@code alpha} value.
     * @see        java.awt.Color#brighter
     * @since      JDK1.0
     */
    public Color4 darker() {
        construct(Math.max((int)(getRed()  *FACTOR), 0),
                         Math.max((int)(getGreen()*FACTOR), 0),
                         Math.max((int)(getBlue() *FACTOR), 0),
                         getAlpha());
        return this;
    }
	
	@Override
	public String toString(){
		return "Color4("+getRed()+","+getGreen()+","+getBlue()+","+getAlpha()+")";
		
	}

	public Color4 setAlpha(int pow)
	{
		construct(r, g, b, pow);
		return this;
	}
	  
}
