package ml.sakii.factoryisland;

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
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class SettingsGUI extends JPanel implements ActionListener, KeyListener {
	private static final long serialVersionUID = 334783618749307739L;

	private JButton okButton;

	JButton textureButton;

	JButton fogButton;
	JButton creativeButton, directRenderingButton;
	JButton resetButton;
	JSlider sensitivitySlider;//, viewportscaleSlider;
	JSlider brightnessSlider;

	JSlider renderDistanceSlider;

	JSlider fovSlider;
	JTextField NameTextField, widthField, heightField;
	private int HEIGHT = (int)(Main.Frame.getHeight()*0.05f/4*3);
	private int WIDTH=(int)(Main.Frame.getWidth()*0.35f);
	private int SPACING=(int)(Main.Frame.getHeight()*0.016f);
	boolean useTextures=Config.useTextures;

	boolean fogEnabled = Config.fogEnabled;
	boolean creative = Config.creative;
	boolean directRendering = Config.directRendering;
	

	public SettingsGUI(){
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
	        	directRendering = Config.directRendering;
	        	updateButtons();
	        }
	    });
		
		
		NameTextField = new JTextField(50);
		NameTextField.setSize(WIDTH, HEIGHT);
		NameTextField.setLocation(Main.Frame.getWidth()/2-WIDTH/2, (int)(Main.Frame.getHeight()/3.4-NameTextField.getHeight()/2));
		NameTextField.addKeyListener(this);
		add(NameTextField);
		

		
		sensitivitySlider = new JSlider(SwingConstants.HORIZONTAL,1,16,Config.sensitivity);
		sensitivitySlider.setSize(WIDTH, HEIGHT);
		sensitivitySlider.setLocation(Main.Frame.getWidth()/2-sensitivitySlider.getWidth()/2, NameTextField.getY()+HEIGHT+SPACING);
		sensitivitySlider.setMajorTickSpacing(5);
		sensitivitySlider.setMinorTickSpacing(1);
		sensitivitySlider.setPaintLabels(true);
		sensitivitySlider.setPaintTicks(true);
		sensitivitySlider.addKeyListener(this);
		add(sensitivitySlider);
		
		renderDistanceSlider = new JSlider(SwingConstants.HORIZONTAL,16,130,Config.renderDistance);
		renderDistanceSlider.setSize(WIDTH, HEIGHT);
		renderDistanceSlider.setLocation(Main.Frame.getWidth()/2-renderDistanceSlider.getWidth()/2, sensitivitySlider.getY()+HEIGHT+SPACING);
		renderDistanceSlider.setMajorTickSpacing(16);
		renderDistanceSlider.setMinorTickSpacing(4);
		renderDistanceSlider.setPaintLabels(true);
		renderDistanceSlider.setPaintTicks(true);
		renderDistanceSlider.addKeyListener(this);
		add(renderDistanceSlider);
		
		fovSlider = new JSlider(SwingConstants.HORIZONTAL,200,1000,Config.zoom);
		fovSlider.setSize(WIDTH, HEIGHT);
		fovSlider.setLocation((Main.Frame.getWidth()/2-renderDistanceSlider.getWidth()/2), (renderDistanceSlider.getY()+HEIGHT+SPACING));
		fovSlider.setMajorTickSpacing(200);
		fovSlider.setMinorTickSpacing(25);
		fovSlider.setPaintLabels(true);
		fovSlider.setPaintTicks(true);
		fovSlider.addKeyListener(this);
		add(fovSlider);
		
		brightnessSlider = new JSlider(SwingConstants.HORIZONTAL,6,9,7);//Config.brightness);
		brightnessSlider.setSize(WIDTH, HEIGHT);
		brightnessSlider.setLocation((Main.Frame.getWidth()/2-renderDistanceSlider.getWidth()/2), (fovSlider.getY()+HEIGHT+SPACING));
		brightnessSlider.setMajorTickSpacing(1);
		brightnessSlider.setMinorTickSpacing(1);
		brightnessSlider.setPaintLabels(true);
		brightnessSlider.setPaintTicks(true);
		brightnessSlider.addKeyListener(this);
		brightnessSlider.setEnabled(false);
		add(brightnessSlider);
		

		
		widthField = new JTextField(20);
		widthField.setSize(WIDTH/2-SPACING, HEIGHT);
		widthField.setLocation(Main.Frame.getWidth()/2-WIDTH/2, brightnessSlider.getY()+HEIGHT+SPACING/3*2);
		widthField.addKeyListener(this);
		add(widthField);
		
		heightField = new JTextField(20);
		heightField.setSize(WIDTH/2, HEIGHT);
		heightField.setLocation(Main.Frame.getWidth()/2, brightnessSlider.getY()+HEIGHT+SPACING/3*2);
		heightField.addKeyListener(this);
		add(heightField);
		
		
		String[] labels = getButtonLabels();
		
		textureButton = new MainMenuButton(labels[0] ,Main.Frame.getWidth()/2-WIDTH/2, heightField.getY()+HEIGHT+SPACING/3*2, WIDTH, HEIGHT);
		textureButton.setActionCommand("switchtexture");
		textureButton.addActionListener(this);
		textureButton.addKeyListener(this);
		add(textureButton);
		
		fogButton = new MainMenuButton(labels[1] ,Main.Frame.getWidth()/2-WIDTH/2, textureButton.getY()+HEIGHT+SPACING/3*2, WIDTH, HEIGHT);
		fogButton.setActionCommand("switchfog");
		fogButton.addActionListener(this);
		fogButton.addKeyListener(this);
		add(fogButton);
		
		creativeButton = new MainMenuButton(labels[2] ,Main.Frame.getWidth()/2-WIDTH/2, fogButton.getY()+HEIGHT+SPACING, WIDTH, HEIGHT);
		creativeButton.setActionCommand("switchcreative");
		creativeButton.addActionListener(this);
		creativeButton.addKeyListener(this);
		add(creativeButton);
		
		
		directRenderingButton = new MainMenuButton(labels[3] ,Main.Frame.getWidth()/2-WIDTH/2, creativeButton.getY()+HEIGHT+SPACING, WIDTH, HEIGHT);
		directRenderingButton.setActionCommand("switchdirectrendering");
		directRenderingButton.addActionListener(this);
		directRenderingButton.addKeyListener(this);
		add(directRenderingButton);
		
		okButton = new MainMenuButton("Save",Main.Frame.getWidth()/2-WIDTH/2, directRenderingButton.getY()+HEIGHT + SPACING*5, WIDTH, HEIGHT);
		okButton.setActionCommand("ok");
		okButton.addActionListener(this);
		okButton.addKeyListener(this);
		add(okButton);
		
		resetButton = new MainMenuButton("Reset",Main.Frame.getWidth()/2, okButton.getY()+HEIGHT + SPACING, WIDTH/2, HEIGHT);
		resetButton.setActionCommand("reset");
		resetButton.addActionListener(this);
		resetButton.addKeyListener(this);
		add(resetButton);
		
		JLabel l1 = new JLabel("Username:");
		l1.setLocation(NameTextField.getX()-180-SPACING, NameTextField.getY());
		l1.setSize(180, HEIGHT);
		l1.setHorizontalAlignment(SwingConstants.RIGHT);
		l1.setForeground(Color.WHITE);
		add(l1);
		
		JLabel l2 = new JLabel("Mouse Sensitivity:");
		l2.setLocation(sensitivitySlider.getX()-180-SPACING, sensitivitySlider.getY());
		l2.setSize(180, HEIGHT);
		l2.setHorizontalAlignment(SwingConstants.RIGHT);
		l2.setForeground(Color.WHITE);
		add(l2);
		
		JLabel l3 = new JLabel("Render Distance:");
		l3.setLocation(sensitivitySlider.getX()-180-SPACING, renderDistanceSlider.getY());
		l3.setSize(180, HEIGHT);
		l3.setHorizontalAlignment(SwingConstants.RIGHT);
		l3.setForeground(Color.WHITE);
		add(l3);
		
		JLabel l4 = new JLabel("Field Of View:");
		l4.setLocation(fovSlider.getX()-180-SPACING, fovSlider.getY());
		l4.setSize(180, HEIGHT);
		l4.setHorizontalAlignment(SwingConstants.RIGHT);
		l4.setForeground(Color.WHITE);
		add(l4);
		
		JLabel l5 = new JLabel("Resolution:");
		l5.setLocation(widthField.getX()-180-SPACING, widthField.getY());
		l5.setSize(180, HEIGHT);
		l5.setHorizontalAlignment(SwingConstants.RIGHT);
		l5.setForeground(Color.WHITE);
		add(l5);

		JLabel l6 = new JLabel("<html><body align='right'>World restart is required for<br>creative setting to take effect</body></html>");
		l6.setLocation(creativeButton.getX()-250-SPACING, creativeButton.getY());
		l6.setSize(250, HEIGHT);
		l6.setHorizontalAlignment(SwingConstants.RIGHT);
		l6.setForeground(Color.WHITE);
		add(l6);
		
		JLabel l7 = new JLabel("Brightness: ");
		l7.setLocation(brightnessSlider.getX()-180-SPACING, brightnessSlider.getY());
		l7.setSize(180, HEIGHT);
		l7.setHorizontalAlignment(SwingConstants.RIGHT);
		l7.setForeground(Color.WHITE);
		add(l7);
	}
	
	@Override
	  protected void paintComponent(Graphics g) {
	    super.paintComponent(g);
	    g.drawImage(Main.SettingsBG, 0, 0, this.getWidth(), this.getHeight(), null);
	}

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
		        Config.directRendering=directRendering;
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
		}else if(e.getActionCommand().equals("switchdirectrendering")){
	        directRendering = !directRendering;
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
		if(directRendering) {
    		widthField.setText(Main.Frame.getWidth()+"");
    		widthField.setEnabled(false);
    		heightField.setText(Main.Frame.getHeight()+"");
    		heightField.setEnabled(false);
    		useTextures=false;
    		textureButton.setEnabled(false);
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
    	directRenderingButton.setText(labels[3]);
    	
	}
	
	private String[] getButtonLabels() {
		return new String[] {
				"Textured Rendering: " + (useTextures?"ON":"OFF"),
				"Show Fog: " + (fogEnabled?"ON":"OFF"),
				"Creative Mode: " + (creative?"ON":"OFF"),
				"Direct Rendering (experimental) : " + (directRendering?"ON":"OFF")
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
