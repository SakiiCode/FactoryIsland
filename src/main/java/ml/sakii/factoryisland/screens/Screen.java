package ml.sakii.factoryisland.screens;

import javax.swing.JPanel;

import ml.sakii.factoryisland.GUIManager;
import ml.sakii.factoryisland.Main;

public abstract class Screen extends JPanel {

	private static final long serialVersionUID = 4740687913638507236L;
	protected int EntryWidth=(int)(Main.Width*0.3f);
	protected int EntryHeight=(int)(Main.Height*0.055f);
	protected int EntrySpacing=(int)(Main.Height*0.016f)+EntryHeight;
	
	protected GUIManager guiManager;
	public Screen(GUIManager guiManager) {
		this.guiManager=guiManager;
	}
}
