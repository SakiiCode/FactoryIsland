package ml.sakii.factoryisland.items;

public class ItemStack {
	public Item kind;
	public int amount;
	
	
	public ItemStack(Item item, int amount){
		this.kind = item;
		this.amount = amount;
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
