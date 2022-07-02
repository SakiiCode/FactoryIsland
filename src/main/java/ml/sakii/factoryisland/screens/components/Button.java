package ml.sakii.factoryisland.screens.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.SwingConstants;

import ml.sakii.factoryisland.AssetLibrary;
import ml.sakii.factoryisland.Color4;
import ml.sakii.factoryisland.Main;

public class Button extends JButton{
	private static final long serialVersionUID = 6317521383259312584L;
	
	public Button(String text, String command){
		super(text);
		this.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
		this.setPreferredSize(new Dimension(Main.Width/5, Main.Height/25));
		this.setMaximumSize(new Dimension(Main.Width/5, Main.Height/10));
		this.setBackground(new Color(53,53,53));
		this.setForeground(Color.WHITE);
		setHorizontalTextPosition(SwingConstants.CENTER);
		setVerticalTextPosition(SwingConstants.CENTER);
		this.setHorizontalAlignment(SwingConstants.LEFT);
		this.setVerticalAlignment(SwingConstants.CENTER);
		this.setAlignmentX(Component.LEFT_ALIGNMENT);
		setActionCommand(command);
	}
	
	public Button(String text,int x, int y, int width, int height){
		super(text);
		this.setBounds(x, y, width, height);
		this.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
		this.setBackground(new Color4().set(AssetLibrary.stone.c).darker().darker().getColor());
		this.setForeground(Color.WHITE);
		this.setHorizontalAlignment(SwingConstants.LEFT);
		this.setVerticalAlignment(SwingConstants.CENTER);
	}
	

}
