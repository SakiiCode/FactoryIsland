package ml.sakii.factoryisland.items;


public class CreativeInventory extends PlayerInventory
{

	public CreativeInventory()
	{
		super(null);
	}
	
	@Override
	void doMultiplayer(String name, int amount) {
		
	}

	@Override
	public Item add(Item kind, int amount, boolean resend){
		return kind;
	}
}
