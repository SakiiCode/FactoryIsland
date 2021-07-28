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

import ml.sakii.factoryisland.Config;
import ml.sakii.factoryisland.Main;
import ml.sakii.factoryisland.MainMenuButton;
import ml.sakii.factoryisland.RenderMethod;

public class PauseGUI extends TexturedScreen implements ActionListener, KeyListener {
	private static final long serialVersionUID = 334783618749307739L;

	private JButton resumeButton, exitButton, settingsButton;
	private JLabel infoLabel;
	private Color infoBgColor =new Color(0.2f, 0.2f, 0.2f, 0.5f);
	private Color transparent = new Color(0,0,0,0);
	
	public PauseGUI(){
		super(Main.StandardBG);
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
		
		
		
		resumeButton = new MainMenuButton("Resume Game",Main.Frame.getWidth()/2-EntryWidth/2, (int)(Main.Frame.getHeight()/3.4-EntryHeight/2), EntryWidth, EntryHeight);
		resumeButton.setActionCommand("resume");
		resumeButton.setVisible(true);
		resumeButton.addActionListener(this);
		add(resumeButton);
		
		settingsButton = new MainMenuButton("Settings",Main.Frame.getWidth()/2-EntryWidth/2, (int)(Main.Frame.getHeight()/3.4+EntrySpacing+EntryHeight-EntryHeight/2), EntryWidth, EntryHeight);
		settingsButton.setActionCommand("settings");
		settingsButton.setVisible(true);
		settingsButton.addActionListener(this);
		add(settingsButton);
		
		exitButton = new MainMenuButton("Save & Exit to Main Menu",Main.Frame.getWidth()/2-EntryWidth/2, (int)(Main.Frame.getHeight()/3.4+EntrySpacing*2+EntryHeight*2-EntryHeight/2), EntryWidth, EntryHeight);
		exitButton.setActionCommand("exit");
		exitButton.setVisible(true);
		exitButton.addActionListener(this);
		add(exitButton);
		
		
		infoLabel = new JLabel(MainMenuGUI.CONTROLS_TEXT + "</body></html>");
		infoLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
		infoLabel.setForeground(Color.WHITE);
		//infoLabel.setBackground(infoBgColor);
		//infoLabel.setOpaque(true);
		infoLabel.setLocation(Main.Frame.getWidth()/2-infoLabel.getPreferredSize().width/2, exitButton.getY()+exitButton.getHeight()+Main.Frame.getHeight()/10);
		infoLabel.setSize(infoLabel.getPreferredSize());
		add(infoLabel);
		

		
	}
	
	@Override
	protected void paintComponent(Graphics g) {
	    
	    //.paintComponent(g);
	    if(Config.renderMethod==RenderMethod.DIRECT || Main.FreezeBG == Main.StandardBG) { //utobbi akkor ha most valtott at directrol masra
	    	super.paintComponent(g);
	    }else{
	    	g.drawImage(Main.FreezeBG, 0, 0, this.getWidth(), this.getHeight(), null);
	    	
	    }
	    
	    
	    int titleHeight = Main.Height/7;
	    int titleWidth = titleHeight*Main.PausedTitle.getWidth()/Main.PausedTitle.getHeight();
	    
	    g.drawImage(Main.PausedTitle, Main.Width/2-titleWidth/2, Main.Height/15, titleWidth, titleHeight, null);
	    
	    
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("resume")){
			Main.GAME.resume();
		}
		if(e.getActionCommand().equals("settings")){
			Main.SwitchWindow("settings");

		}
		if(e.getActionCommand().equals("exit")){
			if(Main.GAME != null) {
				Main.GAME.disconnect(null);
			}else {
				Main.SwitchWindow("mainmenu");
			}
		}
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
			Main.GAME.resume();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
	}
	
	
	
}
