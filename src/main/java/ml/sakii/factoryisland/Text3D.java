package ml.sakii.factoryisland;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

public class Text3D extends Object3D
{
	private String text;
	private Vector location;
	private int x, y;
	private final Vector ViewToPoint = new Vector();
	private final Point proj = new Point();

	public Text3D(String text, float x, float y, float z)
	{
		this.text = text;
		location = new Vector(x, y, z);
	}

	@Override
	protected boolean update(UpdateContext context)
	{
		
		ViewToPoint.set(location);
		ViewToPoint.substract(context.game.PE.getPos());
		
		AvgDist = ViewToPoint.getLength();
		
		if (ViewToPoint.DotProduct(context.game.ViewVector) > 0)
		{
			ViewToPoint.set(location);
			context.game.convert3Dto2D(ViewToPoint, proj);
			x = proj.x;
			y = proj.y;
			return true;
		}
		return false;
	}

	@Override
	public void draw(Graphics g, Game game)
	{

		g.setFont(new Font("Helvetica", Font.BOLD, (int) (20 * game.ratio)));
		FontMetrics fm = g.getFontMetrics();
		Rectangle rect = fm.getStringBounds(text, g).getBounds();

		int w0 = (int) (rect.getWidth() / 2);
		rect.grow(game.margin, game.margin);
		g.setColor(new Color(0.5f, 0.5f, 0.5f, 0.5f));
		g.fillRect(x - w0 - game.margin, y - fm.getAscent() - game.margin, (int) rect.getWidth(),
				(int) rect.getHeight());
		g.setColor(Color.RED);
		g.drawOval(x, y, 5, 5);
		g.setColor(Color.WHITE);
		g.drawString(text, x - w0, y);
	}
	
	public void setLocation(Vector v) {
		location.set(v);
	}


}
