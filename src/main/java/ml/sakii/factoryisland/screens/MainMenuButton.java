package ml.sakii.factoryisland.screens;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.SwingConstants;

import ml.sakii.factoryisland.Color4;
import ml.sakii.factoryisland.Main;

public class MainMenuButton extends JButton{
	private static final long serialVersionUID = 6317521383259312584L;
	
	public MainMenuButton(String text,int x, int y, int width, int height){
		super(text);
		this.setBounds(x, y, width, height);
		this.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
		this.setBackground(new Color4().set(Main.stone.c).darker().darker().getColor());
		this.setForeground(Color.WHITE);
		this.setHorizontalAlignment(SwingConstants.LEFT);
		this.setVerticalAlignment(SwingConstants.CENTER);
	}
	

}
