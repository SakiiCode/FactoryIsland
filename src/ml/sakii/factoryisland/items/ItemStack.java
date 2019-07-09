package ml.sakii.factoryisland.items;

import ml.sakii.factoryisland.Main;

public class ItemStack {
	
	public static final ItemStack EMPTY = new ItemStack(null, 0);
	
	@Override
	public String toString()
	{
		return "ItemStack(" + kind + "=" + amount + ")";
	}



	public ItemType kind;
	public int amount;
	
	
	public ItemStack(ItemType item, int amount){
		this.kind = item;
		this.amount = amount;
	}
	
	public ItemStack() {
		kind=Main.Items.get("Stone");
		amount=1;
	}
	
	public ItemStack set(ItemType kind, int amount) {
		this.kind=kind;
		this.amount=amount;
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((kind == null) ? 0 : kind.hashCode());
		return result;
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ItemStack))
			return false;
		ItemStack other = (ItemStack) obj;
		if (kind != other.kind)
			return false;
		return true;
	}


}
