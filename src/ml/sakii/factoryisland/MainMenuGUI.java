package ml.sakii.factoryisland;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

public class MainMenuGUI extends JPanel implements ActionListener{
	private static final long serialVersionUID = 894581305065092909L;

	private JButton sp;
	private JButton mp;
	private JButton opt;
	private JButton exit;
	
	private int menuL = (int) (Main.Frame.getWidth()*0.1f);
	private int menuT = (int) (Main.Frame.getHeight()*0.4f);
	private int W = (int) (Main.Frame.getWidth()*0.3f);
	private int H = (int) (Main.Frame.getHeight()*0.055f);
	private int spacing = (int) (Main.Frame.getHeight()*0.011f);
	
	ArrayList<JButton> Menu = new ArrayList<>();
		
	public MainMenuGUI(){
		


		this.addComponentListener( new ComponentAdapter() {
	        @Override
	        public void componentShown( ComponentEvent e ) {
	        	MainMenuGUI.this.requestFocusInWindow();
	        }
	    });
		
		sp = new MainMenuButton("Singleplayer" ,menuL, menuT, W, H);
		sp.setActionCommand("singleplayer");
		sp.addActionListener(this);
		Menu.add(sp);

		
		
		mp = new MainMenuButton("Multiplayer",menuL, menuT+spacing, W, H);
		mp.setActionCommand("multiplayer");
		mp.addActionListener(this);
		Menu.add(mp);

		
		
		opt = new MainMenuButton("Settings",menuL, menuT+spacing*2, W, H);
		opt.setActionCommand("settings");
		opt.addActionListener(this);
		Menu.add(opt);
		
		
		
		exit = new MainMenuButton("Exit Game",menuL, menuT+spacing*3, W, H);
		exit.setActionCommand("exit");
		exit.addActionListener(this);
		Menu.add(exit);
		
		
		
		
		
		SpringLayout layout = new SpringLayout();
		for(int i=0;i<Menu.size();i++) {
			JButton button = Menu.get(i);
			
			if(i>0) {
				JButton prevButton = Menu.get(i-1);
				layout.putConstraint(SpringLayout.NORTH, button, spacing, SpringLayout.SOUTH, prevButton);
				layout.putConstraint(SpringLayout.SOUTH, button, button.getHeight(), SpringLayout.NORTH, button);
				layout.putConstraint(SpringLayout.WEST, button, menuL, SpringLayout.WEST, this);
				layout.putConstraint(SpringLayout.EAST, button, button.getWidth(), SpringLayout.WEST, button);
				
				
			}else {
				layout.putConstraint(SpringLayout.NORTH, button, menuT, SpringLayout.NORTH, this);
				layout.putConstraint(SpringLayout.SOUTH, button, button.getHeight(), SpringLayout.NORTH, button);
				layout.putConstraint(SpringLayout.WEST, button, menuL, SpringLayout.WEST, this);
				layout.putConstraint(SpringLayout.EAST, button, button.getWidth(), SpringLayout.WEST, button);
				
			}
			
			add(button);
			
			
		}
		
		String data = "<html><body><center>Controls</center><ul><li>WASD - move</li><li>Ctrl - toggle fly</li><li>Space - jump / fly up</li><li>Shift - fly down</li><li>Escape - pause</li><li>G - add any item to inventory</li><li>Q - switch between inventories</li><li>F2 - save screenshot</li><li>F6 - open map to LAN multiplayer</li></ul>"
				+ "&nbsp;&nbsp;"+"Early Access Alpha v"+Main.MAJOR+"."+Main.MINOR+"."+Main.REVISION+"<br>"
				+ "&nbsp;&nbsp;&nbsp;&nbsp;Created by Sakii <br>"
				+ "&nbsp;&nbsp;&nbsp;&nbsp;http://sakii.itch.io/factoryisland<hr>";
		
		String data2 = "<center>System Info</center><br>"
				+ "&nbsp;&nbsp;"+System.getProperty("os.name")+"<br>"
				+ "&nbsp;&nbsp;"+System.getProperty("java.version")+" "+System.getProperty("sun.arch.data.model") +" bits JVM<br>"
				+ "&nbsp;&nbsp;"+Runtime.getRuntime().totalMemory()/1000/1000+"/"+Runtime.getRuntime().maxMemory()/1000/1000+" MB of memory<br>"
				+ "</body></html>";
		Main.log("---------------------------------------------------");
		String[] logged =data2.replaceAll("<br>","\n").replaceAll("<\\/?\\w+>", "").replaceAll("&nbsp;"," ").split("\n"); 
		for(String line : logged) {Main.log(line);}
		Main.log("  Early Access Alpha v"+Main.MAJOR+"."+Main.MINOR+"."+Main.REVISION);
		JLabel instructions = new JLabel(data+data2);
		instructions.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
		instructions.setForeground(Color.WHITE);
		instructions.setBackground(new Color(0.2f, 0.2f, 0.2f, 0.5f));
		instructions.setOpaque(true);
		layout.putConstraint(SpringLayout.WEST, instructions, 200, SpringLayout.EAST, sp);
		layout.putConstraint(SpringLayout.NORTH, instructions, 0, SpringLayout.NORTH, sp);
		add(instructions);
				
		this.setLayout(layout);
		
		
		if(Main.sound)
			Main.BGMusic.start();


	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String c = e.getActionCommand();
	    if (c.equals("singleplayer")) {
	    	Main.SwitchWindow("generate");

	    }
	    
	    if(c.equals("multiplayer")){
	    	Main.SwitchWindow("connect");
	    }
	    
	    if(c.equals("settings")){
	    	Main.SwitchWindow("settings");
	    	
	    }
	    
	    if (c.equals("exit")) {
	    	
	    	//Main.Frame.dispatchEvent(new WindowEvent(Main.Frame, WindowEvent.WINDOW_CLOSING));
	    	System.exit(0);
	    }
	} 
	
	
	@Override
    public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Rectangle2D area = centerImage(Main.Frame.getWidth(), Main.Frame.getHeight(), Main.MainMenuBG.getWidth(), Main.MainMenuBG.getHeight());
		g.drawImage(Main.MainMenuBG, (int)area.getX(), (int)area.getY(), (int)area.getWidth(), (int)area.getHeight(), null);
		float ratio = ((float)Main.Logo.getHeight()/Main.Logo.getWidth());
		int x = Main.Frame.getWidth()/15;
		int y = Main.Frame.getHeight()/15;
		
		int h = Main.Frame.getHeight()/4;
		int w = (int)(h/ratio);
		g.drawImage(Main.Logo, x, y,  w, h, null);
		
    }

	
	private static Rectangle2D.Float centerImage(int sw, int sh, int iw, int ih){
		float imageRatio = iw*1.0f/ih;
        float windowRatio = sw*1.0f/sh;
        if(windowRatio>imageRatio){
        	return new Rectangle2D.Float(0, -(sw/imageRatio-sh)/2, sw, sw/imageRatio);
        }
		return new Rectangle2D.Float(-(sh*imageRatio-sw)/2, 0, sh*imageRatio, sh);
		
	}
	
	
}