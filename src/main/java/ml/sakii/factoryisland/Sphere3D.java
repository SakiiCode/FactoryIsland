package ml.sakii.factoryisland;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

public class Sphere3D extends Object3D{

	private float radius=0, centerDist=0;
	private final Vector pos=new Vector();
	private int x, y;
	private int renderRadiusLeft=0,renderRadiusRight=0,renderRadiusTop=0,renderRadiusBottom=0;
	private final Color4 color=new Color4();
	private final Point2D.Float proj = new Point2D.Float();
	private final Vector ViewToPoint = new Vector();
	private final Vector left = new Vector(), right = new Vector(), top = new Vector(), bottom = new Vector();
	
	Point2D.Float resultLeft = new Point2D.Float();
	Point2D.Float resultRight = new Point2D.Float();
	Point2D.Float resultTop = new Point2D.Float();
	Point2D.Float resultBottom = new Point2D.Float();

	private boolean prevVisible=true;
	
	private boolean drawTopLeft, drawTopRight, drawBottomLeft, drawBottomRight, drawCenter;
	
	private Vector viewToPos = new Vector(), posRight = new Vector();
	
	
	public Sphere3D(float x, float y, float z, float radius, Color4 color) {
		this.pos.set(x, y, z);
		this.radius=radius;
		this.getColor().set(color);
	}
	
	
	@Override
	protected boolean update(Game game) {
		if(game.locked) {
			return prevVisible; 
		}
		
		centerDist = game.PE.getPos().distance(pos); 
		if(game.key[8]) {
			AvgDist = 4;
		}else {
			AvgDist = centerDist;
		}
		
		
		ViewToPoint.set(pos);
		ViewToPoint.substract(game.PE.getPos());
		
		if(ViewToPoint.getLength() <= radius) {
			prevVisible=false;
			return false;
		}
		
		
		
		
		
		viewToPos.set(pos).substract(game.PE.ViewFrom).normalize();
		posRight.set(viewToPos).CrossProduct(game.PE.VerticalVector);

		left.set(posRight).multiply(-radius).add(pos);			
		right.set(posRight).multiply(radius).add(pos);			
		top.set(posRight).CrossProduct(viewToPos).multiply(radius).add(pos);			
		bottom.set(posRight).CrossProduct(viewToPos).multiply(-radius).add(pos);
		
		boolean leftVisible = isVisible(left, game);
		boolean rightVisible = isVisible(right, game);
		boolean topVisible = isVisible(top, game);
		boolean bottomVisible = isVisible(bottom, game);
		
		
		drawTopLeft=true;
		drawTopRight=true;
		drawBottomLeft=true;
		drawBottomRight=true;
		
		
		if(!leftVisible) {
			drawTopLeft=false;
			drawBottomLeft=false;
		}
		
		if(!rightVisible) {
			drawTopRight=false;
			drawBottomRight=false;
		}
		
		if(!topVisible) {
			drawTopLeft=false;
			drawTopRight=false;
		}
		
		if(!bottomVisible) {
			drawBottomLeft=false;
			drawBottomRight=false;
		}
		
		drawCenter = drawTopLeft && drawTopRight && drawBottomLeft && drawBottomRight; // workaround mert a kozepso neha felvillan

		
		if(!leftVisible && !rightVisible && !topVisible && !bottomVisible) {
			prevVisible=false;
			return false;
		}
		
		
		if(leftVisible) {
			game.convert3Dto2D(left.cpy(), resultLeft);
		}
		if(rightVisible) {
			game.convert3Dto2D(right.cpy(), resultRight);
		}
		if(topVisible) {
			game.convert3Dto2D(top.cpy(), resultTop);
		}
		if(bottomVisible) {
			game.convert3Dto2D(bottom.cpy(), resultBottom);
		}
		
		
		
		
		
		/*System.out.println(left.cpy().substract(game.PE.getPos()).normalize().DotProduct(game.ViewVector));
		System.out.println(right.cpy().substract(game.PE.getPos()).normalize().DotProduct(game.ViewVector));
		System.out.println(top.cpy().substract(game.PE.getPos()).normalize().DotProduct(game.ViewVector));
		System.out.println(bottom.cpy().substract(game.PE.getPos()).normalize().DotProduct(game.ViewVector));
		System.out.println("----");*/
		
		
		ViewToPoint.set(pos);
		game.convert3Dto2D(ViewToPoint, proj);
		x = (int) proj.getX();
		y = (int) proj.getY();
			
		
		prevVisible=true;
		return true;
	}
	
	private static boolean isVisible(Vector point, Game game){
		return point.cpy().substract(game.PE.getPos()).normalize().DotProduct(game.ViewVector) > 0;
	}

	@Override
	protected void draw(BufferedImage FrameBuffer, Graphics g, Game game) {
		g.setColor(getColor().getColor());
		int[][] points = getClipPoints();
		int[] xPoints = points[0];
		int[] yPoints = points[1];
		

		if(drawTopLeft) {
			Rectangle clipBoundsTopLeft = getClipBoundsTopLeft(xPoints, yPoints);
			if(game.key[8]) {
				
				g.setColor(getColor().getColor());
				g.fillRect(clipBoundsTopLeft.x,clipBoundsTopLeft.y,clipBoundsTopLeft.width,clipBoundsTopLeft.height);
			}else {
				g.setClip(clipBoundsTopLeft);
				g.fillOval(clipBoundsTopLeft.x,
						clipBoundsTopLeft.y,
						clipBoundsTopLeft.width*2,
						clipBoundsTopLeft.height*2);
			}
		}
		
		
		if(drawTopRight) {
			Rectangle clipBoundsTopRight = getClipBoundsTopRight(xPoints, yPoints);
			if(game.key[8]) {
				g.setColor(new Color(0,1,0,0.25f));
				g.fillRect(clipBoundsTopRight.x,clipBoundsTopRight.y,clipBoundsTopRight.width,clipBoundsTopRight.height);
			}else {
				g.setClip(clipBoundsTopRight);
				g.fillOval(clipBoundsTopRight.x-clipBoundsTopRight.width,
						clipBoundsTopRight.y,
						clipBoundsTopRight.width*2,
						clipBoundsTopRight.height*2);
			}
		}
		
		if(drawBottomLeft) {
		
			Rectangle clipBoundsBottomLeft = getClipBoundsBottomLeft(xPoints, yPoints);
			if(game.key[8]) {
				g.setColor(new Color(1,1,1,0.25f));
				g.fillRect(clipBoundsBottomLeft.x,clipBoundsBottomLeft.y,clipBoundsBottomLeft.width,clipBoundsBottomLeft.height);
			}else {
				g.setClip(clipBoundsBottomLeft);
				g.fillOval(clipBoundsBottomLeft.x,
						clipBoundsBottomLeft.y-clipBoundsBottomLeft.height,
						clipBoundsBottomLeft.width*2,
						clipBoundsBottomLeft.height*2);
			}
		}
		
		if(drawBottomRight) {
		
			Rectangle clipBoundsBottomRight = getClipBoundsBottomRight(xPoints, yPoints);
			if(game.key[8]) {
				g.setColor(new Color(1,0,0,0.25f));
				g.fillRect(clipBoundsBottomRight.x,clipBoundsBottomRight.y,clipBoundsBottomRight.width,clipBoundsBottomRight.height);
			}else {
				g.setClip(clipBoundsBottomRight);
				g.fillOval(clipBoundsBottomRight.x-clipBoundsBottomRight.width,
						clipBoundsBottomRight.y-clipBoundsBottomRight.height,
						clipBoundsBottomRight.width*2,
						clipBoundsBottomRight.height*2);
			}
		}
		
		if(drawCenter) {
			g.setClip(null);
			Rectangle clipBoundsCenter = getClipBoundsCenter(xPoints, yPoints);
			if(game.key[8]) {
				g.setColor(new Color(0,1,1,0.25f));
			}
			g.fillRect(clipBoundsCenter.x,clipBoundsCenter.y,clipBoundsCenter.width,clipBoundsCenter.height);
		}
		
		g.setClip(null);
		
	}
	
	public Rectangle getClipBounds() {
		return new Rectangle(x-renderRadiusLeft, y-renderRadiusTop, renderRadiusLeft+renderRadiusRight, renderRadiusTop);
	}
	
	public int[][] getClipPoints(){
		int[][] result = {
				{(int) resultLeft.x,(int) resultTop.x,(int) resultBottom.x,(int) resultRight.x},
				{(int) resultTop.y, (int) resultRight.y,(int) resultLeft.y, (int) resultBottom.y}
		};
		result[0][1]=Util.limit(result[0][1],result[0][0], result[0][3]); // limit x1 between x0 and x3
		result[0][2]=Util.limit(result[0][2],result[0][0], result[0][3]); // limit x2 between x0 and x3
		result[1][1]=Util.limit(result[1][1],result[1][0], result[1][3]); // limit y1 between y0 and y3
		result[1][2]=Util.limit(result[1][2],result[1][0], result[1][3]); // limit y2 between y0 and y3
		for(int i=0;i<4;i++) {
			result[0][i] -=resultLeft.x;
		}
		for(int i=0;i<4;i++) {
			result[1][i] -=resultTop.y;
		}
		
		if((result[0][1] < result[0][2] && result[1][2] < result[1][1]) ||
				(result[0][2] < result[0][1] && result[1][1] < result[1][2])
				
				) {
			result[0][2] = result[0][1];
		}
			
		return result;
	}
	
	public Rectangle getClipBoundsTopLeft(int[] x, int[] y) {
		return getRectangle(x[0],y[0],x[1]-x[0],y[2]-y[0]);
	}
	public Rectangle getClipBoundsTopRight(int[] x, int[] y) {
		return getRectangle(x[1],y[0],x[3]-x[1],y[1]-y[0]);
	}
	
	public Rectangle getClipBoundsBottomLeft(int[] x, int[] y) {
		return getRectangle(x[0],y[2],x[2]-x[0],y[3]-y[2]);
	}
	
	public Rectangle getClipBoundsBottomRight(int[] x, int[] y) {
		return getRectangle(x[2],y[1],x[3]-x[2],y[3]-y[1]);
	}
	
	public Rectangle getClipBoundsCenter(int[] x, int[] y) {
		return getRectangle(
				Math.min(x[1], x[2]),
				Math.min(y[1], y[2]),
				Math.abs(x[1]-x[2]),
				Math.abs(y[1]-y[2]));
	}
	
	private Rectangle getRectangle(int x, int y, int w, int h) {
		return new Rectangle((int)resultLeft.x+x, (int)resultTop.y+y, w, h);
	}
	
	@Override
	public String toString() {
		return AvgDist+","+renderRadiusLeft+","+renderRadiusRight+","+renderRadiusTop+","+renderRadiusBottom;
	}

	public Color4 getColor() {
		return color;
	}

	public Vector getPos() {
		return pos;
	}

	public float getRadius() {
		return radius;
	}

	public float getCenterDist() {
		return centerDist;
	}
	
	

}


