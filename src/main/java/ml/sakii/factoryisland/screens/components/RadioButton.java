package ml.sakii.factoryisland.screens.components;

import java.awt.Color;
import javax.swing.JRadioButton;

public class RadioButton extends JRadioButton{

	private static final long serialVersionUID = -4428813931453871705L;

	public RadioButton(String text, boolean selected) {
		super(text, selected);
		this.setOpaque(false);
		this.setForeground(Color.WHITE);
	}
}
