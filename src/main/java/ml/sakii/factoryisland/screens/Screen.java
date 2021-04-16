package ml.sakii.factoryisland.screens;

import javax.swing.JPanel;

import ml.sakii.factoryisland.Main;

public abstract class Screen extends JPanel {

	private static final long serialVersionUID = 4740687913638507236L;
	protected int EntryWidth=(int)(Main.Frame.getWidth()*0.3f);
	protected int EntryHeight=(int)(Main.Frame.getHeight()*0.055f);
	protected int EntrySpacing=(int)(Main.Frame.getHeight()*0.016f)+EntryHeight;
	
	public Screen() {
		
	}
}
