package ml.sakii.factoryisland.blocks;

public interface TickListener {
	
	/**
	 * 
	 * @param tickCount aktualis tick szam
	 * @return true ha maradjon a tickelendo blockok kozott 
	 */
	boolean tick(long tickCount);
	
}
