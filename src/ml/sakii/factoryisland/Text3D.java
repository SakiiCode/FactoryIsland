package ml.sakii.factoryisland;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

public class Text3D extends Object3D
{
	public String text;
	public Vector location;
	public int x, y;
	private final Vector ViewToPoint = new Vector();
	private final Point2D.Float proj = new Point2D.Float();

	public Text3D(String text, float x, float y, float z)
	{
		this.text = text;
		location = new Vector(x, y, z);
	}

	@Override
	boolean update()
	{
		
		ViewToPoint.set(location);
		ViewToPoint.substract(Main.GAME.PE.getPos());
		
		AvgDist = ViewToPoint.getLength();
		
		if (ViewToPoint.DotProduct(Main.GAME.ViewVector) > 0)
		{
			ViewToPoint.set(location);
			Main.GAME.convert3Dto2D(ViewToPoint, proj);
			x = (int) proj.getX();
			y = (int) proj.getY();
			return true;
		}
		return false;
	}

	@Override
	public void draw(BufferedImage fb, Graphics g)
	{

		g.setFont(new Font("Helvetica", Font.BOLD, (int) (20 * Main.GAME.ratio)));
		FontMetrics fm = g.getFontMetrics();
		Rectangle rect = fm.getStringBounds(text, g).getBounds();

		int w0 = (int) (rect.getWidth() / 2);
		rect.grow(Main.GAME.margin, Main.GAME.margin);
		g.setColor(new Color(0.5f, 0.5f, 0.5f, 0.5f));
		g.fillRect(x - w0 - Main.GAME.margin, y - fm.getAscent() - Main.GAME.margin, (int) rect.getWidth(),
				(int) rect.getHeight());
		g.setColor(Color.RED);
		g.drawOval(x, y, 5, 5);
		g.setColor(Color.WHITE);
		g.drawString(text, x - w0, y);
	}


}
