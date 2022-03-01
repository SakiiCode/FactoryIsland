package ml.sakii.factoryisland.screens;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SpringLayout;

import ml.sakii.factoryisland.AssetLibrary;
import ml.sakii.factoryisland.GUIManager;
import ml.sakii.factoryisland.Globals;
import ml.sakii.factoryisland.Main;

public class MainMenuGUI extends TexturedScreen implements ActionListener{
	private static final long serialVersionUID = 894581305065092909L;

	private JButton sp, mp, opt, benchmark, exit;
	
	private int menuL = (int) (Main.Width*0.1f);
	private int menuT = (int) (Main.Height*0.4f);

	
	private ArrayList<JButton> Menu = new ArrayList<>();
	
	private BufferedImage Logo;
		
	public MainMenuGUI(GUIManager guiManager){
		super(AssetLibrary.MainMenuBG, guiManager);
		this.Logo=AssetLibrary.Logo;


		this.addComponentListener( new ComponentAdapter() {
	        @Override
	        public void componentShown( ComponentEvent e ) {
	        	MainMenuGUI.this.requestFocusInWindow();
	        }
	    });
		
		sp = new MainMenuButton("Singleplayer", menuL, menuT, EntryWidth, EntryHeight);
		sp.setActionCommand("singleplayer");
		sp.addActionListener(this);
		Menu.add(sp);

		
		
		mp = new MainMenuButton("Multiplayer", menuL, menuT+EntrySpacing, EntryWidth, EntryHeight);
		mp.setActionCommand("multiplayer");
		mp.addActionListener(this);
		mp.setEnabled(false);
		Menu.add(mp);

		
		
		opt = new MainMenuButton("Settings", menuL, menuT+EntrySpacing*1, EntryWidth, EntryHeight);
		opt.setActionCommand("settings");
		opt.addActionListener(this);
		Menu.add(opt);
		
		benchmark = new MainMenuButton("Run Benchmark", menuL, menuT+EntrySpacing*2, EntryWidth, EntryHeight);
		benchmark.setActionCommand("benchmark");
		benchmark.addActionListener(this);
		benchmark.setEnabled(false);
		Menu.add(benchmark);
		
		
		
		exit = new MainMenuButton("Exit Game", menuL, menuT+EntrySpacing*3, EntryWidth, EntryHeight);
		exit.setActionCommand("exit");
		exit.addActionListener(this);
		Menu.add(exit);
		
		
		
		
		
		SpringLayout layout = new SpringLayout();
		for(int i=0;i<Menu.size();i++) {
			JButton button = Menu.get(i);
			
			if(i>0) {
				JButton prevButton = Menu.get(i-1);
				layout.putConstraint(SpringLayout.NORTH, button, EntrySpacing, SpringLayout.SOUTH, prevButton);
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
		
		String data = Globals.CONTROLS_TEXT+"""
				&nbsp;&nbsp; Early Access Alpha v"""+Main.MAJOR+"."+Main.MINOR+"."+Main.REVISION+"""
				<br>
		&nbsp;&nbsp;&nbsp;&nbsp;Created by Sakii <br>
		&nbsp;&nbsp;&nbsp;&nbsp;http://sakii.itch.io/factoryisland<hr>""";
		
		String data2 = "<center>System Info</center><br>"
				+ "&nbsp;&nbsp;"+System.getProperty("os.name")+"<br>"
				+ "&nbsp;&nbsp;"+System.getProperty("java.version")+" "+System.getProperty("sun.arch.data.model") +" bits JVM<br>"
				+ "&nbsp;&nbsp;"+Runtime.getRuntime().totalMemory()/1000/1000+"/"+Runtime.getRuntime().maxMemory()/1000/1000+" MB of memory<br>"
				+ "</body></html>";
		String[] logged =data2.replaceAll("<br>","\n").replaceAll("<\\/?\\w+>", "").replaceAll("&nbsp;"," ").split("\n"); 
		for(String line : logged) {Main.log("    "+line);}
		Main.log("      Early Access Alpha v"+Main.MAJOR+"."+Main.MINOR+"."+Main.REVISION);
		JLabel instructions = new JLabel(data+data2);
		instructions.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
		instructions.setForeground(Color.WHITE);
		instructions.setBackground(new Color(0.2f, 0.2f, 0.2f, 0.5f));
		instructions.setOpaque(true);
		layout.putConstraint(SpringLayout.WEST, instructions, 200, SpringLayout.EAST, sp);
		layout.putConstraint(SpringLayout.NORTH, instructions, 0, SpringLayout.NORTH, sp);
		add(instructions);
				
		this.setLayout(layout);
		
		
		/*if(Main.sound)
			AssetLibrary.BGMusic.start();*/


	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String c = e.getActionCommand();
	    if (c.equals("singleplayer")) {
	    	guiManager.SwitchWindow("generate");

	    }
	    
	    if(c.equals("multiplayer")){
	    	guiManager.SwitchWindow("connect");
	    }
	    
	    if(c.equals("settings")){
	    	guiManager.SwitchWindow("settings");
	    	
	    }
	    
	    if(c.equals("benchmark")){
	    	guiManager.SwitchWindow("benchmark");
	    	
	    }
	    
	    if (c.equals("exit")) {
	    	//System.exit(0);
	    	guiManager.exit();
	    }
	} 
	
	
	@Override
    public void paintComponent(Graphics g) {
		super.paintComponent(g);
		float ratio = ((float)Logo.getHeight()/Logo.getWidth());
		int x = Main.Width/15;
		int y = Main.Height/15;
		
		int h = Main.Height/4;
		int w = (int)(h/ratio);
		g.drawImage(Logo, x, y,  w, h, null);
		
    }

	

	
	
}