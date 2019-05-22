package ml.sakii.factoryisland.items;

import java.awt.image.BufferedImage;

public class Item {


	public String name;
	public String className;
	public BufferedImage ItemTexture;
	public BufferedImage ViewmodelTexture;

	
	public Item(String name, String className, BufferedImage ItemTexture, BufferedImage ViewmodelTexture){
		this.name = name;
		this.className = className;
		this.ItemTexture = ItemTexture;
		this.ViewmodelTexture = ViewmodelTexture;
		
	}
	
}
