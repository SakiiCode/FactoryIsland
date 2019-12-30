package ml.sakii.factoryisland;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.SwingConstants;

public class MainMenuButton extends JButton{
	private static final long serialVersionUID = 6317521383259312584L;
	
	//private BufferedImage texture = Main.MenuButtonTexture;
	
	public MainMenuButton(String text,int x, int y, int width, int height){
		super(text);
		this.setBounds(x, y, width, height);
		//this.setSize(width, height);
		this.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
		//this.setText(text);
		//this.setIcon(null);
		this.setBackground(new Color4().set(Main.stone.c).darker().darker().getColor());
		this.setForeground(Color.WHITE);
		this.setHorizontalAlignment(SwingConstants.LEFT);
		this.setVerticalAlignment(SwingConstants.CENTER);
	}
	
	/*
	@Override
	public void paintComponent(Graphics g){
		

		super.paintComponent(g);
		//Graphics2D g2d = (Graphics2D) g;
		g.drawImage(texture, 0, 0, this.getWidth(), this.getHeight(), null);
		
		//g2d.setFont(this.getFont());
		
		//g.setColor(Color.white);
		//g.drawString(this.getText(), this.getVerticalTextPosition()+this.getIconTextGap(), this.getHorizontalTextPosition());
	}*/
	

}
