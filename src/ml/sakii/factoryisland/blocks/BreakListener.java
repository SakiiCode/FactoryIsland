package ml.sakii.factoryisland.blocks;

public interface BreakListener {
	

	/**
	 * @param felhaszn�l�n�v csak multiplayerben sz�m�t
    * @return <code>boolean</code> return to inventory
	*/
	boolean breaked(String username);
}
