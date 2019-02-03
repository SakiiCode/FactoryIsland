package ml.sakii.factoryisland.blocks;

public interface MetadataListener
{
	//true ha sajat hashmap.put-ot akarsz
	public void onMetadataUpdate(String key, String value);
}
