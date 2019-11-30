package ml.sakii.factoryisland.blocks;

public interface BreakListener {
	

	/**
	 * @param felhasználónév csak multiplayerben számít
    * @return <code>boolean</code> return to inventory
	*/
	boolean breaked(String username);
}
