package ml.sakii.factoryisland.blocks;

public interface MetadataListener
{
	/**
	 * 
	 * 
	 * @param key
	 * @param value
	 * @return true ha volt hashmap.put
	 */
	public boolean onMetadataUpdate(String key, String value);
}
