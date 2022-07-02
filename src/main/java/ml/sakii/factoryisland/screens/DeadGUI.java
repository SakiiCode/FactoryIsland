package ml.sakii.factoryisland.screens;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;

import ml.sakii.factoryisland.AssetLibrary;
import ml.sakii.factoryisland.GUIManager;
import ml.sakii.factoryisland.Main;
import ml.sakii.factoryisland.screens.components.Button;

public class DeadGUI extends PaintedScreen implements ActionListener, KeyListener {
	private static final long serialVersionUID = 334783618749307739L;

	private JButton respawnButton, exitButton;
	
	
	public DeadGUI(GUIManager guiManager){
		super(AssetLibrary.PausedBG, guiManager);
		
		setLayout(null);
		addKeyListener(this);
		this.addComponentListener( new ComponentAdapter() {
	        @Override
	        public void componentShown( ComponentEvent e ) {
	        	DeadGUI.this.requestFocusInWindow();
	        }
	    });
		

		respawnButton = new Button("Respawn",Main.Width/2-EntryWidth/2, (int)(Main.Height/3.4-EntryHeight/2), EntryWidth, EntryHeight);
		respawnButton.setActionCommand("respawn");
		respawnButton.setVisible(true);
		respawnButton.addActionListener(this);
		add(respawnButton);
		
		
		exitButton = new Button("Save & Exit to Main Menu",Main.Width/2-EntryWidth/2, (int)(Main.Height/3.4+EntrySpacing-EntryHeight/2), EntryWidth, EntryHeight);
		exitButton.setActionCommand("exit");
		exitButton.setVisible(true);
		exitButton.addActionListener(this);
		add(exitButton);
		
		

		
	}
	


	@Override
	public void actionPerformed(ActionEvent e) {

		if(e.getActionCommand().equals("respawn")){
			guiManager.GAME.respawn();
			guiManager.GAME.resume();
		}
		if(e.getActionCommand().equals("exit")) {
			//TODO ilyenkor mpben nem menti a poziciot es inventoryt
			guiManager.GAME.disconnect(null);
		}
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void keyPressed(KeyEvent e) {

	}

	@Override
	public void keyReleased(KeyEvent e) {
		
	}
	
	
	
}
