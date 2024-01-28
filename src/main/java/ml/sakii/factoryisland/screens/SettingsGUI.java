package ml.sakii.factoryisland.screens;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import ml.sakii.factoryisland.AssetLibrary;
import ml.sakii.factoryisland.Config;
import ml.sakii.factoryisland.GUIManager;
import ml.sakii.factoryisland.Main;
import ml.sakii.factoryisland.RenderMethod;
import ml.sakii.factoryisland.TargetMarkerType;
import ml.sakii.factoryisland.screens.components.Button;

public class SettingsGUI extends TexturedScreen implements ActionListener, KeyListener {
	private static final long serialVersionUID = 334783618749307739L;

	private JButton okButton;

	private JButton textureButton;

	private JButton fogButton;
	private JButton creativeButton, renderMethodButton, markerTypeButton, ambientOcclusionButton;
	private JButton resetButton;
	private JSlider sensitivitySlider;
	private JSlider brightnessSlider;

	private JSlider renderDistanceSlider, resolutionScalingSlider;

	private JSlider fovSlider;
	private JTextField NameTextField;

	
	@SuppressWarnings("hiding")
	private int EntryHeight  = (int)(Main.Height*0.05f/4*3);

	private boolean useTextures=Config.useTextures;

	private boolean fogEnabled = Config.fogEnabled;
	private boolean creative = Config.creative;
	private RenderMethod renderMethod = Config.renderMethod;
	private TargetMarkerType targetMarkerType = Config.targetMarkerType;
	private boolean ambientOcclusion = Config.ambientOcclusion;
	

	public SettingsGUI(GUIManager guiManager){
		super(AssetLibrary.StandardBG, guiManager);
		
		setLayout(null);
		addKeyListener(this);
		this.addComponentListener( new ComponentAdapter() {
	        @Override
	        public void componentShown( ComponentEvent e ) {
	        	SettingsGUI.this.requestFocusInWindow();
	        	NameTextField.setText(Config.username);
	        	sensitivitySlider.setValue(Config.sensitivity);
	        	renderDistanceSlider.setValue(Config.renderDistance);
	        	fovSlider.setValue(Config.FOV);

	        	resolutionScalingSlider.setValue((int)(Config.resolutionScaling*100));
	        	
	        	brightnessSlider.setValue(Config.brightness);
	        	
	        	useTextures=Config.useTextures;
	        	fogEnabled = Config.fogEnabled;
	        	creative = Config.creative;
	        	
	        	renderMethod=Config.renderMethod;
	        	targetMarkerType=Config.targetMarkerType;
	        	
	        	ambientOcclusion=Config.ambientOcclusion;
	        	
	        	updateButtons();
	        }
	    });
		
		
		NameTextField = new JTextField(50);
		NameTextField.setSize(EntryWidth, EntryHeight);
		NameTextField.setLocation(Main.Width/2-EntryWidth/2, (int)(Main.Height/3.4-NameTextField.getHeight()/2));
		NameTextField.addKeyListener(this);
		add(NameTextField);
		

		
		sensitivitySlider = new JSlider(SwingConstants.HORIZONTAL,1,16,Config.sensitivity);
		sensitivitySlider.setSize(EntryWidth, EntryHeight);
		sensitivitySlider.setLocation(Main.Width/2-sensitivitySlider.getWidth()/2, NameTextField.getY()+EntryHeight+EntrySpacing);
		sensitivitySlider.setMajorTickSpacing(5);
		sensitivitySlider.setMinorTickSpacing(1);
		sensitivitySlider.setPaintLabels(true);
		sensitivitySlider.setPaintTicks(true);
		sensitivitySlider.addKeyListener(this);
		add(sensitivitySlider);
		
		renderDistanceSlider = new JSlider(SwingConstants.HORIZONTAL,16,130,Config.renderDistance);
		renderDistanceSlider.setSize(EntryWidth, EntryHeight);
		renderDistanceSlider.setLocation(Main.Width/2-renderDistanceSlider.getWidth()/2, sensitivitySlider.getY()+EntryHeight+EntrySpacing);
		renderDistanceSlider.setMajorTickSpacing(16);
		renderDistanceSlider.setMinorTickSpacing(4);
		renderDistanceSlider.setPaintLabels(true);
		renderDistanceSlider.setPaintTicks(true);
		renderDistanceSlider.addKeyListener(this);
		add(renderDistanceSlider);
		
		fovSlider = new JSlider(SwingConstants.HORIZONTAL,50,130,Config.FOV);
		fovSlider.setSize(EntryWidth, EntryHeight);
		fovSlider.setLocation((Main.Width/2-renderDistanceSlider.getWidth()/2), (renderDistanceSlider.getY()+EntryHeight+EntrySpacing));
		fovSlider.setMajorTickSpacing(10);
		fovSlider.setMinorTickSpacing(2);
		fovSlider.setPaintLabels(true);
		fovSlider.setPaintTicks(true);
		fovSlider.addKeyListener(this);
		add(fovSlider);
		
		brightnessSlider = new JSlider(SwingConstants.HORIZONTAL,6,9,7);//Config.brightness);
		brightnessSlider.setSize(EntryWidth, EntryHeight);
		brightnessSlider.setLocation((Main.Width/2-renderDistanceSlider.getWidth()/2), (fovSlider.getY()+EntryHeight+EntrySpacing));
		brightnessSlider.setMajorTickSpacing(1);
		brightnessSlider.setMinorTickSpacing(1);
		brightnessSlider.setPaintLabels(true);
		brightnessSlider.setPaintTicks(true);
		brightnessSlider.addKeyListener(this);
		brightnessSlider.setEnabled(false);
		add(brightnessSlider);
		
		resolutionScalingSlider = new JSlider(SwingConstants.HORIZONTAL,25,200,(int)(Config.resolutionScaling*100));
		resolutionScalingSlider.setSize(EntryWidth, EntryHeight);
		resolutionScalingSlider.setLocation((Main.Width/2-renderDistanceSlider.getWidth()/2), (brightnessSlider.getY()+EntryHeight+EntrySpacing));
		resolutionScalingSlider.setMajorTickSpacing(25);
		resolutionScalingSlider.setMinorTickSpacing(5);
		resolutionScalingSlider.setPaintLabels(true);
		resolutionScalingSlider.setPaintTicks(true);
		resolutionScalingSlider.addKeyListener(this);
		add(resolutionScalingSlider);

		
		
		String[] labels = getButtonLabels();
		
		textureButton = new Button(labels[0] ,Main.Width/2-EntryWidth/2, resolutionScalingSlider.getY()+EntryHeight+EntrySpacing/3*2, EntryWidth, EntryHeight);
		textureButton.setActionCommand("switchtexture");
		textureButton.addActionListener(this);
		textureButton.addKeyListener(this);
		add(textureButton);
		
		fogButton = new Button(labels[1] ,Main.Width/2-EntryWidth/2, textureButton.getY()+EntryHeight+EntrySpacing/3*2, EntryWidth, EntryHeight);
		fogButton.setActionCommand("switchfog");
		fogButton.addActionListener(this);
		fogButton.addKeyListener(this);
		add(fogButton);
		
		creativeButton = new Button(labels[2] ,Main.Width/2-EntryWidth/2, fogButton.getY()+EntryHeight+EntrySpacing, EntryWidth, EntryHeight);
		creativeButton.setActionCommand("switchcreative");
		creativeButton.addActionListener(this);
		creativeButton.addKeyListener(this);
		add(creativeButton);
		
		
		markerTypeButton = new Button(labels[3] ,Main.Width/2-EntryWidth/2, creativeButton.getY()+EntryHeight+EntrySpacing, EntryWidth, EntryHeight);
		markerTypeButton.setActionCommand("switchmarkertype");
		markerTypeButton.addActionListener(this);
		markerTypeButton.addKeyListener(this);
		add(markerTypeButton);
		
		ambientOcclusionButton = new Button(labels[4] ,Main.Width/2-EntryWidth/2, markerTypeButton.getY()+EntryHeight+EntrySpacing, EntryWidth, EntryHeight);
		ambientOcclusionButton.setActionCommand("switchambientocclusion");
		ambientOcclusionButton.addActionListener(this);
		ambientOcclusionButton.addKeyListener(this);
		add(ambientOcclusionButton);
		
		renderMethodButton = new Button(labels[5] ,Main.Width/2-EntryWidth/2, ambientOcclusionButton.getY()+EntryHeight+EntrySpacing, EntryWidth, EntryHeight);
		renderMethodButton.setActionCommand("switchrendermethod");
		renderMethodButton.addActionListener(this);
		renderMethodButton.addKeyListener(this);
		add(renderMethodButton);
		
		okButton = new Button("Save",Main.Width/2-EntryWidth/2, renderMethodButton.getY()+EntryHeight + EntrySpacing*5, EntryWidth, EntryHeight);
		okButton.setActionCommand("ok");
		okButton.addActionListener(this);
		okButton.addKeyListener(this);
		add(okButton);
		
		resetButton = new Button("Reset",Main.Width/2, okButton.getY()+EntryHeight + EntrySpacing, EntryWidth/2, EntryHeight);
		resetButton.setActionCommand("reset");
		resetButton.addActionListener(this);
		resetButton.addKeyListener(this);
		add(resetButton);
		
		JLabel l1 = new JLabel("Username:");
		l1.setLocation(NameTextField.getX()-180-EntrySpacing, NameTextField.getY());
		l1.setSize(180, EntryHeight);
		l1.setHorizontalAlignment(SwingConstants.RIGHT);
		l1.setForeground(Color.WHITE);
		add(l1);
		
		JLabel l2 = new JLabel("Mouse Sensitivity:");
		l2.setLocation(sensitivitySlider.getX()-180-EntrySpacing, sensitivitySlider.getY());
		l2.setSize(180, EntryHeight);
		l2.setHorizontalAlignment(SwingConstants.RIGHT);
		l2.setForeground(Color.WHITE);
		add(l2);
		
		JLabel l3 = new JLabel("Render Distance:");
		l3.setLocation(sensitivitySlider.getX()-180-EntrySpacing, renderDistanceSlider.getY());
		l3.setSize(180, EntryHeight);
		l3.setHorizontalAlignment(SwingConstants.RIGHT);
		l3.setForeground(Color.WHITE);
		add(l3);
		
		JLabel l4 = new JLabel("Field Of View:");
		l4.setLocation(fovSlider.getX()-180-EntrySpacing, fovSlider.getY());
		l4.setSize(180, EntryHeight);
		l4.setHorizontalAlignment(SwingConstants.RIGHT);
		l4.setForeground(Color.WHITE);
		add(l4);
		
		JLabel l5 = new JLabel("Resolution scaling:");
		l5.setLocation(resolutionScalingSlider.getX()-180-EntrySpacing, resolutionScalingSlider.getY());
		l5.setSize(180, EntryHeight);
		l5.setHorizontalAlignment(SwingConstants.RIGHT);
		l5.setForeground(Color.WHITE);
		add(l5);

		JLabel l6 = new JLabel("<html><body align='right'>World restart is required for<br>creative setting to take effect</body></html>");
		l6.setLocation(creativeButton.getX()-250-EntrySpacing, creativeButton.getY());
		l6.setSize(250, EntryHeight);
		l6.setHorizontalAlignment(SwingConstants.RIGHT);
		l6.setForeground(Color.WHITE);
		add(l6);
		
		JLabel l7 = new JLabel("Brightness: ");
		l7.setLocation(brightnessSlider.getX()-180-EntrySpacing, brightnessSlider.getY());
		l7.setSize(180, EntryHeight);
		l7.setHorizontalAlignment(SwingConstants.RIGHT);
		l7.setForeground(Color.WHITE);
		add(l7);
		
		JLabel l8 = new JLabel("<html><body align='right'>Buffered rendering is recommended for small resolutions.<br>Volatile should be good for everything else</body></html>");
		l8.setLocation(textureButton.getX()-500-EntrySpacing, renderMethodButton.getY());
		l8.setSize(500, EntryHeight);
		l8.setHorizontalAlignment(SwingConstants.RIGHT);
		l8.setForeground(Color.WHITE);
		add(l8);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
	    
	    
	    if(Config.renderMethod==RenderMethod.DIRECT || guiManager.GAME == null) {
	    	super.paintComponent(g);
	    }else {
	    	g.drawImage(AssetLibrary.FreezeBG, 0, 0, this.getWidth(), this.getHeight(), null);
	    }
	    
	    int titleWidth = Main.Width/2;
	    int titleHeight = titleWidth*AssetLibrary.SettingsTitle.getHeight()/AssetLibrary.SettingsTitle.getWidth();
	    
	    g.drawImage(AssetLibrary.SettingsTitle, Main.Width/2-titleWidth/2, Main.Height/15, titleWidth, titleHeight, null);
	    
	    
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("ok")){
			if(!NameTextField.getText().isBlank() && !NameTextField.getText().equals("Guest")) {
		        	
		        
		        Config.sensitivity = sensitivitySlider.getValue();
		        Config.renderDistance = renderDistanceSlider.getValue();
		        Config.useTextures = useTextures;
		        
		        Config.username = NameTextField.getText();
		        if(NameTextField.getText().equals("Guest")) {
					Config.username = "Guest"+new Random().nextInt(100000);
				}else {
					Config.username = NameTextField.getText();
				}
		        Config.fogEnabled = fogEnabled;
		        Config.FOV = fovSlider.getValue();
		        Config.resolutionScaling=resolutionScalingSlider.getValue()/100f;
		        
		        Config.creative=creative;
		        Config.renderMethod=renderMethod;
		        Config.targetMarkerType=targetMarkerType;

		        Config.ambientOcclusion=ambientOcclusion;
		        Config.save();
		        if(guiManager.GAME != null) {
		        	guiManager.GAME.resizeScreen(Config.getWidth(), Config.getHeight());
		        }
		        guiManager.SwitchBack();
			}else {
				GUIManager.showMessageDialog("Invalid username", "Error!", JOptionPane.ERROR_MESSAGE);
			}
		}else if(e.getActionCommand().equals("switchtexture")){
	        useTextures = !useTextures;
	        updateButtons();
		}else if(e.getActionCommand().equals("switchfog")){
	        fogEnabled = !fogEnabled;
	        updateButtons();
		}else if(e.getActionCommand().equals("switchcreative")){
	        creative = !creative;
	        updateButtons();
		}else if(e.getActionCommand().equals("switchrendermethod")){
	        int nextMethod = renderMethod.id +1;
	        if(nextMethod>2) {
	        	nextMethod=0;
	        }
	        renderMethod=RenderMethod.values()[nextMethod];
	        updateButtons();
		}else if(e.getActionCommand().equals("switchmarkertype")){
	        int nextType = targetMarkerType.id +1;
	        if(nextType>1) {
	        	nextType=0;
	        }
	        targetMarkerType=TargetMarkerType.values()[nextType];
	        updateButtons();
		}else if(e.getActionCommand().equals("reset")){
			
			int dialogResult = GUIManager.showConfirmDialog("Are you sure?","Reset Options",JOptionPane.YES_NO_OPTION);
			if(dialogResult == JOptionPane.YES_OPTION){
				Config.reset();
				Config.load();
				Config.save();
				guiManager.SwitchBack();
			}
			
		}else if(e.getActionCommand().equals("switchambientocclusion")){
			ambientOcclusion = !ambientOcclusion;
	        updateButtons();
		}
		
		
	}
	
	private void updateButtons() {
		if(renderMethod == RenderMethod.DIRECT) {
			resolutionScalingSlider.setValue(100);
			resolutionScalingSlider.setEnabled(false);
    	}else if(renderMethod==RenderMethod.VOLATILE){
    		resolutionScalingSlider.setValue((int)(Config.resolutionScaling*100));
			resolutionScalingSlider.setEnabled(true);
    	}else {
    		resolutionScalingSlider.setValue((int)(Config.resolutionScaling*100));
			resolutionScalingSlider.setEnabled(true);
    	}
		String[] labels = getButtonLabels();
		textureButton.setText(labels[0]);
    	fogButton.setText(labels[1]);
    	creativeButton.setText(labels[2]);
    	markerTypeButton.setText(labels[3]);
    	ambientOcclusionButton.setText(labels[4]);
    	renderMethodButton.setText(labels[5]);
	}
	
	private String[] getButtonLabels() {
		return new String[] {
				"Textures: " + (useTextures?"ON":"OFF"),
				"Show Fog: " + (fogEnabled?"ON":"OFF"),
				"Creative Mode: " + (creative?"ON":"OFF"),
				"Target Marker: " + switch(targetMarkerType) {
				case SHADE -> "Transparent circle";
				case OUTLINE -> "White Square";
				},
				"Ambient Occlusion: " + (ambientOcclusion?"ON":"OFF"),
				"Rendering Method: " + switch(renderMethod) {
				case BUFFERED -> "BUFFERED";
				case VOLATILE -> "VOLATILE (default)";
				case DIRECT -> "DIRECT";
				}
				
		};
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
			guiManager.SwitchBack();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
	}
	
	
	
}
