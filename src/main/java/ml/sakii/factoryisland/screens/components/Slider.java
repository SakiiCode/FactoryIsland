package ml.sakii.factoryisland.screens.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JSlider;

import ml.sakii.factoryisland.Main;

public class Slider extends JSlider{
	
	private static final long serialVersionUID = -7262877207285016104L;

	public Slider(int min, int max, int value) {
		super(min, max, value);
		
		this.setPreferredSize(new Dimension(Main.Width/10, Main.Height/20));
		this.setAlignmentX(Component.LEFT_ALIGNMENT);
		setOpaque(false);
		setForeground(Color.white);
		
	}

}
