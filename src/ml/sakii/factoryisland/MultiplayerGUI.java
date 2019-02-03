package ml.sakii.factoryisland;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.TexturePaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Rectangle2D;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class MultiplayerGUI extends JPanel implements ActionListener, KeyListener{
	private static final long serialVersionUID = 2915856336542984790L;
	JTextField seedField;
	public MainMenuButton submitButton;
	private JLabel seedLabel;
	public JLabel statusLabel;
	
	
	public MultiplayerGUI(){
		
		this.setLayout(null);
		addKeyListener(this);
		this.addComponentListener( new ComponentAdapter() {
	        @Override
	        public void componentShown( ComponentEvent e ) {
	        	MultiplayerGUI.this.requestFocusInWindow();
	        }
	    });
		
		seedField = new JTextField(Config.multiAddr);
		seedField.setLocation(Main.Frame.getWidth()/6, Main.Frame.getHeight()/5);
		seedField.setSize(Main.Frame.getWidth()/5, Main.Frame.getHeight()/18);
		seedField.addActionListener(this);
		seedField.addKeyListener(this);
		seedField.setActionCommand("submit");
		seedField.setVisible(true);
		
		seedLabel = new JLabel("IP:");
		seedLabel.setLocation(seedField.getX(), seedField.getY()-30);
		seedLabel.setSize(seedLabel.getPreferredSize());
		seedLabel.setVisible(true);
		
		submitButton = new MainMenuButton("Join server",Main.Frame.getWidth()/3, Main.Frame.getHeight()/3*2, 400, 50);
		submitButton.setHorizontalTextPosition(SwingConstants.CENTER);
		submitButton.setVerticalTextPosition(SwingConstants.CENTER);
		//submitButton.setMnemonic(KeyEvent.VK_ENTER);
		submitButton.setActionCommand("submit");
		submitButton.addActionListener(this);
		submitButton.setVisible(true);
		
		statusLabel = new JLabel("");
		statusLabel.setLocation(submitButton.getX(), submitButton.getY()+70);
		statusLabel.setSize(400,20);
		statusLabel.setVisible(true);
		
		add(seedField);
		add(seedLabel);
		add(submitButton);
		add(statusLabel);
		
	}
	
	@Override
	public void paintComponent(Graphics g){
		g.clearRect(0, 0, Main.Frame.getWidth(), Main.Frame.getHeight());
		Graphics2D g2 = (Graphics2D)g;
		g2.setPaint(new TexturePaint(Main.GUIBG, new Rectangle2D.Float(0,0,4,4)));
		g2.fillRect(0, 0, Main.Frame.getWidth(), Main.Frame.getHeight());

	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String c = e.getActionCommand();
	    
	    if(c.equals("submit")){
	    	Config.multiAddr = seedField.getText();
	    	Config.save();
    		statusLabel.setText("Connecting...");
    		submitButton.setEnabled(false);
    		seedField.setEnabled(false);
    		
    		(new Thread() {
    			@Override
    			public void run() {
    				if(Main.joinServer(seedField.getText(), statusLabel)) {
    	    			statusLabel.setText("");
    	    			submitButton.setEnabled(true);
    	    			seedField.setEnabled(true);
    				}else {
    					MultiplayerGUI.this.requestFocusInWindow();
    				}

    			}
    		}).start();
	    }
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		if(arg0.getKeyCode() == KeyEvent.VK_ESCAPE){
			Main.SwitchWindow("mainmenu");
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		
	} 
}
