package ml.sakii.factoryisland.screens.components;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyListener;

import javax.swing.JTextField;

import ml.sakii.factoryisland.Main;

public class TextField extends JTextField{

	private static final long serialVersionUID = 7576105716573053539L;
	
	public TextField(KeyListener keyListener) {
		this.setPreferredSize(new Dimension(Main.Width/10, Main.Height/40));
		this.setAlignmentX(Component.LEFT_ALIGNMENT);
		this.addKeyListener(keyListener);
	}

}
