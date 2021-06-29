package ml.sakii.factoryisland.screens;

import java.awt.Color;
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

import ml.sakii.factoryisland.Config;
import ml.sakii.factoryisland.Main;
import ml.sakii.factoryisland.MainMenuButton;
import ml.sakii.factoryisland.RenderMethod;
import ml.sakii.factoryisland.TargetMarkerType;

public class SettingsGUI extends TexturedScreen implements ActionListener, KeyListener {
	private static final long serialVersionUID = 334783618749307739L;

	private JButton okButton;

	JButton textureButton;

	JButton fogButton;
	JButton creativeButton, renderMethodButton, markerTypeButton;
	JButton resetButton;
	JSlider sensitivitySlider;//, viewportscaleSlider;
	JSlider brightnessSlider;

	JSlider renderDistanceSlider;

	JSlider fovSlider;
	JTextField NameTextField, widthField, heightField;
	/*private int HEIGHT = (int)(Main.Frame.getHeight()*0.05f/4*3);
	private int WIDTH=(int)(Main.Frame.getWidth()*0.35f);
	private int EntrySpacing=(int)(Main.Frame.getHeight()*0.016f);*/
	
	@SuppressWarnings("hiding")
	protected int EntryHeight  = (int)(Main.Frame.getHeight()*0.05f/4*3);

	boolean useTextures=Config.useTextures;

	boolean fogEnabled = Config.fogEnabled;
	boolean creative = Config.creative;
	RenderMethod renderMethod = Config.renderMethod;
	TargetMarkerType targetMarkerType = Config.targetMarkerType;
	

	public SettingsGUI(){
		super(Main.SettingsBG);
		
		//background = new BufferedImage(game.getWidth(), game.getHeight(), BufferedImage.TYPE_INT_ARGB);
		setLayout(null);
		addKeyListener(this);
		this.addComponentListener( new ComponentAdapter() {
	        @Override
	        public void componentShown( ComponentEvent e ) {
	        	SettingsGUI.this.requestFocusInWindow();
	        	NameTextField.setText(Config.username);
	        	sensitivitySlider.setValue(Config.sensitivity);
	        	renderDistanceSlider.setValue(Config.renderDistance);
	        	fovSlider.setValue(Config.zoom);
	        	
	        	widthField.setText(Config.width+"");
	        	heightField.setText(Config.height+"");
	        	
	        	brightnessSlider.setValue(Config.brightness);
	        	
	        	useTextures=Config.useTextures;
	        	fogEnabled = Config.fogEnabled;
	        	creative = Config.creative;
	        	
	        	//directRendering = Config.directRendering;
	        	renderMethod=Config.renderMethod;
	        	targetMarkerType=Config.targetMarkerType;
	        	
	        	updateButtons();
	        }
	    });
		
		
		NameTextField = new JTextField(50);
		NameTextField.setSize(EntryWidth, EntryHeight);
		NameTextField.setLocation(Main.Frame.getWidth()/2-EntryWidth/2, (int)(Main.Frame.getHeight()/3.4-NameTextField.getHeight()/2));
		NameTextField.addKeyListener(this);
		add(NameTextField);
		

		
		sensitivitySlider = new JSlider(SwingConstants.HORIZONTAL,1,16,Config.sensitivity);
		sensitivitySlider.setSize(EntryWidth, EntryHeight);
		sensitivitySlider.setLocation(Main.Frame.getWidth()/2-sensitivitySlider.getWidth()/2, NameTextField.getY()+EntryHeight+EntrySpacing);
		sensitivitySlider.setMajorTickSpacing(5);
		sensitivitySlider.setMinorTickSpacing(1);
		sensitivitySlider.setPaintLabels(true);
		sensitivitySlider.setPaintTicks(true);
		sensitivitySlider.addKeyListener(this);
		add(sensitivitySlider);
		
		renderDistanceSlider = new JSlider(SwingConstants.HORIZONTAL,16,130,Config.renderDistance);
		renderDistanceSlider.setSize(EntryWidth, EntryHeight);
		renderDistanceSlider.setLocation(Main.Frame.getWidth()/2-renderDistanceSlider.getWidth()/2, sensitivitySlider.getY()+EntryHeight+EntrySpacing);
		renderDistanceSlider.setMajorTickSpacing(16);
		renderDistanceSlider.setMinorTickSpacing(4);
		renderDistanceSlider.setPaintLabels(true);
		renderDistanceSlider.setPaintTicks(true);
		renderDistanceSlider.addKeyListener(this);
		add(renderDistanceSlider);
		
		fovSlider = new JSlider(SwingConstants.HORIZONTAL,200,1000,Config.zoom);
		fovSlider.setSize(EntryWidth, EntryHeight);
		fovSlider.setLocation((Main.Frame.getWidth()/2-renderDistanceSlider.getWidth()/2), (renderDistanceSlider.getY()+EntryHeight+EntrySpacing));
		fovSlider.setMajorTickSpacing(200);
		fovSlider.setMinorTickSpacing(25);
		fovSlider.setPaintLabels(true);
		fovSlider.setPaintTicks(true);
		fovSlider.addKeyListener(this);
		add(fovSlider);
		
		brightnessSlider = new JSlider(SwingConstants.HORIZONTAL,6,9,7);//Config.brightness);
		brightnessSlider.setSize(EntryWidth, EntryHeight);
		brightnessSlider.setLocation((Main.Frame.getWidth()/2-renderDistanceSlider.getWidth()/2), (fovSlider.getY()+EntryHeight+EntrySpacing));
		brightnessSlider.setMajorTickSpacing(1);
		brightnessSlider.setMinorTickSpacing(1);
		brightnessSlider.setPaintLabels(true);
		brightnessSlider.setPaintTicks(true);
		brightnessSlider.addKeyListener(this);
		brightnessSlider.setEnabled(false);
		add(brightnessSlider);
		

		
		widthField = new JTextField(20);
		widthField.setSize(EntryWidth/2-EntrySpacing, EntryHeight);
		widthField.setLocation(Main.Frame.getWidth()/2-EntryWidth/2, brightnessSlider.getY()+EntryHeight+EntrySpacing/3*2);
		widthField.addKeyListener(this);
		add(widthField);
		
		heightField = new JTextField(20);
		heightField.setSize(EntryWidth/2, EntryHeight);
		heightField.setLocation(Main.Frame.getWidth()/2, brightnessSlider.getY()+EntryHeight+EntrySpacing/3*2);
		heightField.addKeyListener(this);
		add(heightField);
		
		
		String[] labels = getButtonLabels();
		
		textureButton = new MainMenuButton(labels[0] ,Main.Frame.getWidth()/2-EntryWidth/2, heightField.getY()+EntryHeight+EntrySpacing/3*2, EntryWidth, EntryHeight);
		textureButton.setActionCommand("switchtexture");
		textureButton.addActionListener(this);
		textureButton.addKeyListener(this);
		add(textureButton);
		
		fogButton = new MainMenuButton(labels[1] ,Main.Frame.getWidth()/2-EntryWidth/2, textureButton.getY()+EntryHeight+EntrySpacing/3*2, EntryWidth, EntryHeight);
		fogButton.setActionCommand("switchfog");
		fogButton.addActionListener(this);
		fogButton.addKeyListener(this);
		add(fogButton);
		
		creativeButton = new MainMenuButton(labels[2] ,Main.Frame.getWidth()/2-EntryWidth/2, fogButton.getY()+EntryHeight+EntrySpacing, EntryWidth, EntryHeight);
		creativeButton.setActionCommand("switchcreative");
		creativeButton.addActionListener(this);
		creativeButton.addKeyListener(this);
		add(creativeButton);
		
		
		markerTypeButton = new MainMenuButton(labels[4] ,Main.Frame.getWidth()/2-EntryWidth/2, creativeButton.getY()+EntryHeight+EntrySpacing, EntryWidth, EntryHeight);
		markerTypeButton.setActionCommand("switchmarkertype");
		markerTypeButton.addActionListener(this);
		markerTypeButton.addKeyListener(this);
		add(markerTypeButton);
		
		renderMethodButton = new MainMenuButton(labels[4] ,Main.Frame.getWidth()/2-EntryWidth/2, markerTypeButton.getY()+EntryHeight+EntrySpacing, EntryWidth, EntryHeight);
		renderMethodButton.setActionCommand("switchrendermethod");
		renderMethodButton.addActionListener(this);
		renderMethodButton.addKeyListener(this);
		add(renderMethodButton);
		
		okButton = new MainMenuButton("Save",Main.Frame.getWidth()/2-EntryWidth/2, renderMethodButton.getY()+EntryHeight + EntrySpacing*5, EntryWidth, EntryHeight);
		okButton.setActionCommand("ok");
		okButton.addActionListener(this);
		okButton.addKeyListener(this);
		add(okButton);
		
		resetButton = new MainMenuButton("Reset",Main.Frame.getWidth()/2, okButton.getY()+EntryHeight + EntrySpacing, EntryWidth/2, EntryHeight);
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
		
		JLabel l5 = new JLabel("Resolution:");
		l5.setLocation(widthField.getX()-180-EntrySpacing, widthField.getY());
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
	}
	
	/*@Override
	  protected void paintComponent(Graphics g) {
	    super.paintComponent(g);
	    g.drawImage(Main.SettingsBG, 0, 0, this.getWidth(), this.getHeight(), null);
	}*/

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("ok")){
			if(!NameTextField.getText().trim().isEmpty() && !NameTextField.getText().equals("Guest")) {
		        	
		        
		        Config.sensitivity = sensitivitySlider.getValue();
		        Config.renderDistance = renderDistanceSlider.getValue();
		        //Config.viewportscale = viewportscaleSlider.getValue();
		        Config.useTextures = useTextures;
		        
		        Config.username = NameTextField.getText();
		        if(NameTextField.getText().equals("Guest")) {
					Config.username = "Guest"+new Random().nextInt(100000);
				}else {
					Config.username = NameTextField.getText();
				}
		        Config.fogEnabled = fogEnabled;
		        Config.zoom = fovSlider.getValue();
		        Config.width = Integer.parseInt(widthField.getText());
		        Config.height = Integer.parseInt(heightField.getText());
		        
		        Config.creative=creative;
		        Config.renderMethod=renderMethod;
		        Config.targetMarkerType=targetMarkerType;
		        
		        /*Config.skyEnabled = skyEnabled;
		        Config.fastQuality = fastQuality;*/
		        //Config.brightness=brightnessSlider.getValue();
		        Config.save();
		        Main.SwitchWindow(Main.PreviousCLCard);
			}else {
				JOptionPane.showMessageDialog(Main.Frame, "Invalid username", "Error!", JOptionPane.ERROR_MESSAGE);
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
			
			int dialogResult = JOptionPane.showConfirmDialog (Main.Frame, "Are you sure?","Reset Options",JOptionPane.YES_NO_OPTION);
			if(dialogResult == JOptionPane.YES_OPTION){
				Config.reset();
				Main.SwitchWindow(Main.PreviousCLCard);
			}
			
		}
		
		
	}
	
	void updateButtons() {
		if(renderMethod == RenderMethod.DIRECT) {
    		widthField.setText(Main.Frame.getWidth()+"");
    		widthField.setEnabled(false);
    		heightField.setText(Main.Frame.getHeight()+"");
    		heightField.setEnabled(false);
    		//useTextures=false;
    		//textureButton.setEnabled(false);
    	}else if(renderMethod==RenderMethod.VOLATILE){
    		widthField.setText(Config.width+"");
    		widthField.setEnabled(true);
    		heightField.setText(Config.height+"");
    		heightField.setEnabled(true);
    		//useTextures=false;
    		//textureButton.setEnabled(false);
    	}else {
    		widthField.setText(Config.width+"");
    		widthField.setEnabled(true);
    		heightField.setText(Config.height+"");
    		heightField.setEnabled(true);
    		//useTextures=Config.useTextures;
    		textureButton.setEnabled(true);
    	}
		String[] labels = getButtonLabels();
		textureButton.setText(labels[0]);
    	fogButton.setText(labels[1]);
    	creativeButton.setText(labels[2]);
    	markerTypeButton.setText(labels[3]);
    	renderMethodButton.setText(labels[4]);
    	
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
				"Rendering Method : " + renderMethod
		};
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE){
			Main.SwitchWindow(Main.PreviousCLCard);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
	}
	
	
	
}
