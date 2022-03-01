package ml.sakii.factoryisland.screens;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JLabel;

import ml.sakii.factoryisland.AssetLibrary;
import ml.sakii.factoryisland.Config;
import ml.sakii.factoryisland.GUIManager;
import ml.sakii.factoryisland.Globals;
import ml.sakii.factoryisland.Main;
import ml.sakii.factoryisland.RenderMethod;

public class PauseGUI extends TexturedScreen implements ActionListener, KeyListener {
	private static final long serialVersionUID = 334783618749307739L;

	private JButton resumeButton, exitButton, settingsButton;
	private JLabel infoLabel;
	private Color infoBgColor =new Color(0.2f, 0.2f, 0.2f, 0.5f);
	private Color transparent = new Color(0,0,0,0);
	
	public PauseGUI(GUIManager guiManager){
		super(AssetLibrary.StandardBG, guiManager);
		setLayout(null);
		addKeyListener(this);
		this.addComponentListener( new ComponentAdapter() {
	        @Override
	        public void componentShown( ComponentEvent e ) {
	        	if(Config.renderMethod==RenderMethod.DIRECT) {
	        		infoLabel.setBackground(infoBgColor);
	    	    }else {
	    	    	infoLabel.setBackground(transparent);
	    	    }
	        	PauseGUI.this.repaint();
	        	PauseGUI.this.requestFocusInWindow();
	        }
	    });
		
		
		
		resumeButton = new MainMenuButton("Resume Game",Main.Width/2-EntryWidth/2, (int)(Main.Height/3.4-EntryHeight/2), EntryWidth, EntryHeight);
		resumeButton.setActionCommand("resume");
		resumeButton.setVisible(true);
		resumeButton.addActionListener(this);
		add(resumeButton);
		
		settingsButton = new MainMenuButton("Settings",Main.Width/2-EntryWidth/2, (int)(Main.Height/3.4+EntrySpacing+EntryHeight-EntryHeight/2), EntryWidth, EntryHeight);
		settingsButton.setActionCommand("settings");
		settingsButton.setVisible(true);
		settingsButton.addActionListener(this);
		add(settingsButton);
		
		exitButton = new MainMenuButton("Save & Exit to Main Menu",Main.Width/2-EntryWidth/2, (int)(Main.Height/3.4+EntrySpacing*2+EntryHeight*2-EntryHeight/2), EntryWidth, EntryHeight);
		exitButton.setActionCommand("exit");
		exitButton.setVisible(true);
		exitButton.addActionListener(this);
		add(exitButton);
		
		
		infoLabel = new JLabel(Globals.CONTROLS_TEXT + "</body></html>");
		infoLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
		infoLabel.setForeground(Color.WHITE);
		infoLabel.setLocation(Main.Width/2-infoLabel.getPreferredSize().width/2, exitButton.getY()+exitButton.getHeight()+Main.Height/10);
		infoLabel.setSize(infoLabel.getPreferredSize());
		add(infoLabel);
		

		
	}
	
	@Override
	protected void paintComponent(Graphics g) {
	    if(Config.renderMethod==RenderMethod.DIRECT || AssetLibrary.FreezeBG == AssetLibrary.StandardBG) { //utobbi akkor ha most valtott at directrol masra
	    	super.paintComponent(g);
	    }else{
	    	g.drawImage(AssetLibrary.FreezeBG, 0, 0, this.getWidth(), this.getHeight(), null);
	    	
	    }
	    
	    
	    int titleHeight = Main.Height/7;
	    int titleWidth = titleHeight*AssetLibrary.PausedTitle.getWidth()/AssetLibrary.PausedTitle.getHeight();
	    
	    g.drawImage(AssetLibrary.PausedTitle, Main.Width/2-titleWidth/2, Main.Height/15, titleWidth, titleHeight, null);
	    
	    
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("resume")){
			guiManager.GAME.resume();
		}
		if(e.getActionCommand().equals("settings")){
			guiManager.SwitchWindow("settings");

		}
		if(e.getActionCommand().equals("exit")){
			if(guiManager.GAME != null) {
				guiManager.GAME.disconnect(null);
			}else {
				guiManager.SwitchWindow("mainmenu");
			}
		}
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
			guiManager.GAME.resume();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
	}
	
	
	
}
