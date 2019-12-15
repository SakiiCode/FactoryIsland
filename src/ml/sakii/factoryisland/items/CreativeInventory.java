package ml.sakii.factoryisland.items;

public class CreativeInventory extends PlayerInventory
{

	public CreativeInventory()
	{
		super("",null);
	}
	
	@Override
	void doMultiplayer(ItemType kind, int amount) {
		
	}

	@Override
	public ItemType add(ItemType kind, int amount, boolean resend){
		if(items.containsKey(kind)) {
			return kind;
		}
		return super.add(kind, amount, resend);
	}
}
