package ml.sakii.factoryisland.screens;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import ml.sakii.factoryisland.AssetLibrary;
import ml.sakii.factoryisland.Color4;
import ml.sakii.factoryisland.Config;
import ml.sakii.factoryisland.GUIManager;
import ml.sakii.factoryisland.Main;
import ml.sakii.factoryisland.screens.components.Button;

import javax.swing.SpringLayout;
import ml.sakii.factoryisland.screens.components.Label;
import ml.sakii.factoryisland.screens.components.TextField;

public class SingleplayerGUI extends PaintedScreen implements ActionListener, KeyListener, ComponentListener{
	private static final long serialVersionUID = -2653478459245096044L;
	private TextField seedField, nameField;
	private Button submitButton, generateButton, deleteButton;
	private Label seedLabel, nameLabel, joinLabel;
	private Label statusLabel;
	private JList<String> worldsList;
	
	private String mapName;
	
	public SingleplayerGUI(GUIManager guiManager){
		super(AssetLibrary.GUIBG, guiManager);
		
		
		nameLabel = new Label("Enter world name:");
		
		nameField = new TextField((KeyListener)null);
				
		seedLabel = new Label("Seed (optional):");
		
		seedField = new TextField((KeyListener)null);
		
		generateButton = new Button("Generate World", "generate");
		generateButton.addActionListener(this);
		generateButton.addKeyListener(this);
		
		JPanel generatePanel = new JPanel();
		generatePanel.setBackground(Color4.TRANSPARENT);
		generatePanel.setLayout(new BoxLayout(generatePanel, BoxLayout.PAGE_AXIS));
		generatePanel.setOpaque(true);
		generatePanel.add(nameLabel);
		generatePanel.add(Box.createVerticalStrut(10));
		generatePanel.add(nameField);
		generatePanel.add(Box.createVerticalStrut(10));
		generatePanel.add(seedLabel);
		generatePanel.add(Box.createVerticalStrut(10));
		generatePanel.add(seedField);
		generatePanel.add(Box.createVerticalStrut(10));
		generatePanel.add(generateButton);
				SpringLayout springLayout = new SpringLayout();
				springLayout.putConstraint(SpringLayout.NORTH, generatePanel, 200, SpringLayout.NORTH, this);
				springLayout.putConstraint(SpringLayout.WEST, generatePanel, 300, SpringLayout.WEST, this);
				setLayout(springLayout);
				add(generatePanel);
		
		
		joinLabel = new Label("Or select a world to load:");
		joinLabel.setForeground(Color.white);
		

		
		worldsList = new JList<>();
		

		
		worldsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		worldsList.addKeyListener(this);
		worldsList.addKeyListener(new KeyListener() {
			
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER){
			    	join(false);
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
			}
		});
		worldsList.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2 && !worldsList.isSelectionEmpty()) {
		        	join(false);
		        }
		        Config.selectedMap=worldsList.getSelectedValue();
		        Config.save();
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
			}
		});
		
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(worldsList);
		scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		Dimension wld=scrollPane.getPreferredSize();
		wld.height=Main.Height/3;
		scrollPane.setPreferredSize(wld);
		scrollPane.revalidate();
		scrollPane.setVisible(true);
		
				
		deleteButton= new Button("Delete", "delete");
		deleteButton.addActionListener(this);
		deleteButton.addKeyListener(this);
		
		submitButton = new Button("Load World","submit");
		submitButton.addActionListener(this);
		submitButton.addKeyListener(this);
				
		
		statusLabel = new Label("");
		
		
		JPanel joinPanel = new JPanel();
		springLayout.putConstraint(SpringLayout.NORTH, joinPanel, 0, SpringLayout.NORTH, generatePanel);
		joinPanel.setOpaque(true);
		joinPanel.setBackground(Color4.TRANSPARENT);
		springLayout.putConstraint(SpringLayout.EAST, joinPanel, -400, SpringLayout.EAST, this);
		joinPanel.setLayout(new BoxLayout(joinPanel, BoxLayout.PAGE_AXIS));
		
				joinPanel.add(joinLabel);
				joinPanel.add(Box.createVerticalStrut(10));
				joinPanel.add(scrollPane);
				joinPanel.add(Box.createVerticalStrut(10));
				joinPanel.add(deleteButton);
				joinPanel.add(Box.createVerticalStrut(10));
				joinPanel.add(submitButton);
				joinPanel.add(Box.createVerticalStrut(10));
				joinPanel.add(statusLabel);
				add(joinPanel);


		
		this.addComponentListener(this);
		this.addKeyListener(this);
		
	}
	
	private static String[] getWorlds() {
		
		File folder = new File("saves/");
		File[] listOfFiles = folder.listFiles();
		ArrayList<String> worlds = new ArrayList<>(listOfFiles.length);
	    for (int i = 0; i < listOfFiles.length; i++) {
	      if (listOfFiles[i].isDirectory()) {
	        if(new File("saves/"+listOfFiles[i].getName()+"/map.xml").exists()) {
	        	worlds.add(listOfFiles[i].getName());
	        	
	        }
	      }
	    }
		
		return worlds.toArray(new String[] {});
	}


	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String c = e.getActionCommand();
	    
	    if(c.equals("submit")){
	    	if(!worldsList.isSelectionEmpty()) {
	    	   join(false);
	    	}
	    		
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
		if(generate && !nameField.getText().isBlank()){
			
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
    		GUIManager.showMessageDialog("Invalid map name!", "Error!", JOptionPane.ERROR_MESSAGE);
    		return;
    	}else if(!generate && !worldsList.isSelectionEmpty()){
    		statusLabel.setText("Loading...");
    		mapName=worldsList.getSelectedValue();
    		Config.selectedMap=mapName;
    		Config.save();
    		
    	}
		
    	worldsList.setEnabled(false);
		submitButton.setEnabled(false);
		nameField.setEnabled(false);
		seedField.setEnabled(false);
		generateButton.setEnabled(false);
		deleteButton.setEnabled(false);
		
		(new Thread() {
			  @Override
				public void run() {
				  if(!guiManager.launchWorld(mapName, generate, statusLabel)) {
					  SingleplayerGUI.this.requestFocusInWindow();
					  
				  }else {
				  statusLabel.setText("");
				  }
			    	seedField.setText("");
			    	nameField.setText("");
			    	submitButton.setEnabled(true);
			    	worldsList.setEnabled(true);
			    	nameField.setEnabled(true);
					seedField.setEnabled(true);
					generateButton.setEnabled(true);
					deleteButton.setEnabled(true);

	  }
	 }).start();
		
	    	
    		 
	}
	
	private void delete() {
		if(worldsList.isSelectionEmpty()) return;
		String name = worldsList.getSelectedValue();
		
		File index = new File("saves/"+name+"/");
		
		String[] entries = index.list();
		for(String s: entries){
		    File currentFile = new File(index.getPath(),s);
		    currentFile.delete();
		}
		
		index.delete();
		
		
		refreshList();
	}
	
	private String[] refreshList() {
		
		String[] worlds = getWorlds();
		worldsList.setListData(worlds);
		
		if(!Arrays.asList(worlds).contains(Config.selectedMap)) {
			worldsList.setSelectedIndex(0);
		}else {
			worldsList.setSelectedValue(Config.selectedMap, true);
		}
		worldsList.requestFocusInWindow();
		return worlds;

	}
	@Override
	public void keyPressed(KeyEvent arg0) {
		if(arg0.getKeyCode() == KeyEvent.VK_ESCAPE){
			guiManager.SwitchWindow("mainmenu");
		}
		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		
	}

	@Override
	public void componentResized(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentShown(ComponentEvent e) {
		if(refreshList().length>0) {
        	deleteButton.setEnabled(true);
        	worldsList.setEnabled(true);
        	submitButton.setEnabled(true);
        }else {
        	deleteButton.setEnabled(false);
        	worldsList.setEnabled(false);
        	submitButton.setEnabled(false);
            SingleplayerGUI.this.requestFocusInWindow();

        	
        }
	}

	@Override
	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	
}
