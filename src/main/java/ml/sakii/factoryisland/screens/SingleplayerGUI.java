package ml.sakii.factoryisland.screens;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

import ml.sakii.factoryisland.Config;
import ml.sakii.factoryisland.Main;
import ml.sakii.factoryisland.MainMenuButton;

public class SingleplayerGUI extends PaintedScreen implements ActionListener, KeyListener{
	private static final long serialVersionUID = -2653478459245096044L;
	JTextField seedField, nameField;
	MainMenuButton submitButton, generateButton, deleteButton;
	private JLabel seedLabel, nameLabel, joinLabel;
	JLabel statusLabel;
	private JList<String> worldsList;
	
	private int SPACING = 16;
	private int MARGIN = 10;
	
	String mapName;
	
	public SingleplayerGUI(){
		super(Main.GUIBG);
		this.setLayout(null);
		addKeyListener(this);

		this.addComponentListener( new ComponentAdapter() {
	        @Override
	        public void componentShown( ComponentEvent e ) {
	            if(refreshList().length>0) {
	            	deleteButton.setEnabled(true);
	            	getWorldsList().setEnabled(true);
	            	submitButton.setEnabled(true);
	            }else {
	            	deleteButton.setEnabled(false);
	            	getWorldsList().setEnabled(false);
	            	submitButton.setEnabled(false);
		            SingleplayerGUI.this.requestFocusInWindow();

	            	
	            }
	         //   worldsList.requestFocus();
	            

	        }
	    });
		
		
		nameLabel = new JLabel("Enter world name:");
		nameLabel.setSize(nameLabel.getPreferredSize());
		nameLabel.setLocation(Main.Frame.getWidth()/4-Main.Frame.getWidth()/10, Main.Frame.getHeight()/7);
		nameLabel.setVisible(true);
		
		
		nameField = new JTextField("");
		nameField.setSize(Main.Frame.getWidth()/5, nameField.getPreferredSize().height+2*MARGIN);
		nameField.setLocation(nameLabel.getX(), nameLabel.getY()+nameLabel.getHeight()+SPACING);
		nameField.addKeyListener(this);
		nameField.setVisible(true);
		
				
		seedLabel = new JLabel("Seed (optional):");
		seedLabel.setSize(seedLabel.getPreferredSize());
		seedLabel.setLocation(nameField.getX(), nameField.getY()+nameField.getHeight()+SPACING);
		seedLabel.setVisible(true);
		
		
		seedField = new JTextField("");
		seedField.setSize(Main.Frame.getWidth()/5, seedField.getPreferredSize().height+MARGIN);
		seedField.setLocation(seedLabel.getX(), seedLabel.getY()+seedLabel.getHeight()+SPACING);
		seedField.addKeyListener(this);
		seedField.setVisible(true);
		
		
		generateButton = new MainMenuButton("Generate World", seedField.getX(), seedField.getY()+seedField.getHeight()+SPACING, nameField.getWidth(), nameField.getHeight());
		//generateButton.setl
		generateButton.setHorizontalTextPosition(SwingConstants.CENTER);
		generateButton.setVerticalTextPosition(SwingConstants.CENTER);
		generateButton.setActionCommand("generate");
		generateButton.addActionListener(this);
		generateButton.addKeyListener(this);
		generateButton.setVisible(true);

		
		
		joinLabel = new JLabel("Or select a world to load:");
		joinLabel.setSize(joinLabel.getPreferredSize());
		joinLabel.setLocation(Main.Frame.getWidth()/4*3-seedField.getWidth()/2, nameLabel.getY());
		joinLabel.setVisible(true);
		
		setWorldsList(new JList<>(getWorlds()));
		getWorldsList().setSelectedValue(Config.selectedMap, true);
		getWorldsList().setSize(seedField.getWidth(), Main.Frame.getHeight()/4);
		getWorldsList().setLocation(joinLabel.getX(), nameField.getY());
		getWorldsList().addKeyListener(new KeyListener() {
			
			
			@Override
			public void keyPressed(KeyEvent arg0) {
				if(arg0.getKeyCode() == KeyEvent.VK_ESCAPE){
			    	Main.SwitchWindow("mainmenu");
				}
				if(arg0.getKeyCode() == KeyEvent.VK_ENTER){
			    	join(false);
				}
				
			}

			@Override
			public void keyReleased(KeyEvent arg0)
			{
				
			}

			@Override
			public void keyTyped(KeyEvent arg0)
			{
				
			}
			
		});
		getWorldsList().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		getWorldsList().addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent evt) {
			        if (evt.getClickCount() == 2 && !getWorldsList().isSelectionEmpty()) {
			        	join(false);
			        }
			        Config.selectedMap=getWorldsList().getSelectedValue();
			        Config.save();
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				
			}
			
			
		});
		getWorldsList().setVisible(true);

		
		deleteButton= new MainMenuButton("Delete", getWorldsList().getX(), getWorldsList().getY()+getWorldsList().getHeight()+SPACING, getWorldsList().getHeight(), generateButton.getHeight());
		deleteButton.setHorizontalTextPosition(SwingConstants.CENTER);
		deleteButton.setVerticalTextPosition(SwingConstants.CENTER);
		deleteButton.setActionCommand("delete");
		deleteButton.addActionListener(this);
		deleteButton.addKeyListener(this);
		deleteButton.setVisible(true);
		
		submitButton = new MainMenuButton("Load World",deleteButton.getX(), deleteButton.getY()+deleteButton.getHeight()+SPACING, deleteButton.getWidth(), deleteButton.getHeight());
		submitButton.setHorizontalTextPosition(SwingConstants.CENTER);
		submitButton.setVerticalTextPosition(SwingConstants.CENTER);
		submitButton.setActionCommand("submit");
		submitButton.addActionListener(this);
		submitButton.addKeyListener(this);
		submitButton.setVisible(true);
		
		
		
		
		statusLabel = new JLabel("");
		statusLabel.setLocation(submitButton.getX(), submitButton.getY()+70);
		statusLabel.setSize(submitButton.getWidth(),submitButton.getHeight()*2);
		statusLabel.setVisible(true);
		
		
		add(nameLabel);
		add(nameField);
		add(seedLabel);
		add(seedField);
		add(generateButton);
		add(joinLabel);
		add(getWorldsList());
		add(deleteButton);
		add(submitButton);
		
		add(statusLabel);
		
		
	}
	
	static String[] getWorlds() {
		
		File folder = new File("saves/");
		File[] listOfFiles = folder.listFiles();
		//String[] worlds = new String[listOfFiles.length];
		ArrayList<String> worlds = new ArrayList<>(listOfFiles.length);
	    for (int i = 0; i < listOfFiles.length; i++) {
	      if (listOfFiles[i].isDirectory()) {
	        if(new File("saves/"+listOfFiles[i].getName()+"/map.xml").exists()) {
	        	worlds.add(listOfFiles[i].getName());
	        	
	        }
	      }
	    }
		
		/*String[] worlds = new String[10];
		for(int i=0;i<worlds.length;i++){
			if(new File("saves/"+i+".xml").exists()){
				worlds[i] = "World "+i;
			}else{
				worlds[i] = "World "+i+" - Empty";
			}
			
		}*/
		
		return worlds.toArray(new String[] {});
		
		
	}


	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String c = e.getActionCommand();
	    
	    if(c.equals("submit")){
	    	
	    	   join(false);
	    		
	    }else if(c.equals("generate")){
	    	
	    	   join(true);
	    		
	    }else if(c.equals("delete")){
	        int dialogButton = JOptionPane.YES_NO_OPTION;

	    	int dialogResult = JOptionPane.showConfirmDialog (null, "Do you really want to delete this world?","Warning",dialogButton);
	    	if(dialogResult == JOptionPane.YES_OPTION){
	    		delete();
	    	}
	    	   
	    		
	    }
	}

	public void join(boolean generate) {
		if(generate && !nameField.getText().isEmpty()){
			
    		statusLabel.setText("Generating...");

    		if(seedField.getText().trim().equals("")){
    			Random rnd = new Random();
    			Main.seed = (rnd.nextInt(50)+1)*(rnd.nextInt(50)+1)*(rnd.nextInt(50)+1);
    		}else{
    			try{
    				Main.seed = Long.parseLong(seedField.getText());
    			}catch(@SuppressWarnings("unused") Exception ex){
    				Main.seed = seedField.getText().hashCode();	
    			}
    		}
    		
    		mapName=nameField.getText();
    		Config.selectedMap=mapName;
    		Config.save();
    		
    	}else if(generate) {
    		JOptionPane.showMessageDialog(Main.Frame, "Invalid map name!", "Error!", JOptionPane.ERROR_MESSAGE);
    		return;
    	}else if(!generate && !getWorldsList().isSelectionEmpty()){
    		statusLabel.setText("Loading...");
    		mapName=getWorldsList().getSelectedValue();
    		Config.selectedMap=mapName;
    		Config.save();
    		
    	}
		
    	getWorldsList().setEnabled(false);
		submitButton.setEnabled(false);
		nameField.setEnabled(false);
		seedField.setEnabled(false);
		generateButton.setEnabled(false);
		deleteButton.setEnabled(false);
		
		(new Thread() {
			  @Override
				public void run() {
				  if(!Main.launchWorld(mapName, generate, statusLabel)) {
					  SingleplayerGUI.this.requestFocusInWindow();
					  
				  }else {
				  statusLabel.setText("");
				  }
			    	seedField.setText("");
			    	nameField.setText("");
			    	submitButton.setEnabled(true);
			    	getWorldsList().setEnabled(true);
			    	nameField.setEnabled(true);
					seedField.setEnabled(true);
					generateButton.setEnabled(true);
					deleteButton.setEnabled(true);

	  }
	 }).start();
		
	    	
    		 
	}
	
	private void delete() {
		if(getWorldsList().isSelectionEmpty()) return;
		String name = getWorldsList().getSelectedValue();
		
		File index = new File("saves/"+name+"/");
		
		String[] entries = index.list();
		for(String s: entries){
		    File currentFile = new File(index.getPath(),s);
		    currentFile.delete();
		}
		
		index.delete();
		
		
		refreshList();
	}
	
	String[] refreshList() {
		
		String[] worlds = getWorlds();
		getWorldsList().setListData(worlds);
		getWorldsList().setSelectedValue(Config.selectedMap, true);
		//worldsList.setSelectedIndex(Math.min(Config.selectedMap, worlds.length));
		getWorldsList().requestFocusInWindow();
		//worldsList..requestFocusInWindow();
		//worldsList.grabFocus();
		return worlds;

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

	public JList<String> getWorldsList() {
		return worldsList;
	}

	public void setWorldsList(JList<String> worldsList) {
		this.worldsList = worldsList;
	} 
	
	
}
