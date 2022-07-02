package ml.sakii.factoryisland.screens.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JLabel;

import ml.sakii.factoryisland.Main;

public class Label extends JLabel{

	private static final long serialVersionUID = -8859686891857260859L;
	
	public Label(String text) {
		super(text);
		this.setPreferredSize(new Dimension(Main.Width/10, Main.Height/40));
		this.setAlignmentX(Component.LEFT_ALIGNMENT);
		setForeground(Color.white);
	}

}
