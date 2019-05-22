package ml.sakii.factoryisland.items;

import java.awt.image.BufferedImage;

public class ItemType {


	public String name;
	public String className;
	public BufferedImage ItemTexture;
	public BufferedImage ViewmodelTexture;

	
	public ItemType(String name, String className, BufferedImage ItemTexture, BufferedImage ViewmodelTexture){
		this.name = name;
		this.className = className;
		this.ItemTexture = ItemTexture;
		this.ViewmodelTexture = ViewmodelTexture;
		
	}


	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((className == null) ? 0 : className.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ItemType other = (ItemType) obj;
		if (className == null)
		{
			if (other.className != null)
				return false;
		} else if (!className.equals(other.className))
			return false;
		return true;
	}


	@Override
	public String toString()
	{
		return "ItemType(" + className + ")";
	}
	
	
}
