package ml.sakii.factoryisland.blocks;

import ml.sakii.factoryisland.items.ItemStack;

public interface BreakListener {
	

	/**
	 * @param felhasználónév csak multiplayerben számít
    * @return <code>ItemStack</code> ItemStack amit visszaad (null ha 1 eredetit, ures tomb ha semmit)
	*/
	ItemStack[] breaked(String username);
}
