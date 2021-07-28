package ml.sakii.factoryisland.blocks;

import java.util.HashMap;

import ml.sakii.factoryisland.GameEngine;
import ml.sakii.factoryisland.World;

public interface BlockInterface {
	GameEngine getEngine();
	World getWorld();
	HashMap<String, String> getBlockMeta();
	void setBlockMeta(String key, String value, boolean resend); // más név kellett a setMetadata-ra, ezt a nevet csak a BlockInterface használja
}
