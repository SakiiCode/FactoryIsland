package ml.sakii.factoryisland;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JPanel;

public class DeadGUI extends JPanel implements ActionListener, KeyListener {
	private static final long serialVersionUID = 334783618749307739L;

	private JButton respawnButton, exitButton;
	private int WIDTH=(int)(Main.Frame.getWidth()*0.3f);
	private int HEIGHT=(int)(Main.Frame.getHeight()*0.055f);
	private int SPACING=(int)(Main.Frame.getHeight()*0.016f)+HEIGHT;	
	
	public DeadGUI(){
		
		setLayout(null);
		addKeyListener(this);
		this.addComponentListener( new ComponentAdapter() {
	        @Override
	        public void componentShown( ComponentEvent e ) {
	        	DeadGUI.this.requestFocusInWindow();
	        }
	    });
		

		respawnButton = new MainMenuButton("Respawn",Main.Frame.getWidth()/2-WIDTH/2, (int)(Main.Frame.getHeight()/3.4-HEIGHT/2), WIDTH, HEIGHT);
		respawnButton.setActionCommand("respawn");
		respawnButton.setVisible(true);
		respawnButton.addActionListener(this);
		add(respawnButton);
		
		
		exitButton = new MainMenuButton("Save & Exit to Main Menu",Main.Frame.getWidth()/2-WIDTH/2, (int)(Main.Frame.getHeight()/3.4+SPACING-HEIGHT/2), WIDTH, HEIGHT);
		exitButton.setActionCommand("exit");
		exitButton.setVisible(true);
		exitButton.addActionListener(this);
		add(exitButton);
		
		

		
	}
	
	@Override
	  protected void paintComponent(Graphics g) {
	    
	    super.paintComponent(g);
	    g.drawImage(Main.PausedBG, 0, 0, this.getWidth(), this.getHeight(), null);

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if(e.getActionCommand().equals("respawn")){
			Main.GAME.respawn();
			Main.GAME.resume();
		}
		if(e.getActionCommand().equals("exit")) {
			//TODO ilyenkor mpben nem menti a poziciot es inventoryt
			Main.GAME.disconnect(null);
		}
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		/*if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
			Main.GAME.resume();
		}*/
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
	}
	
	
	
}
