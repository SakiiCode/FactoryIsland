package ml.sakii.factoryisland.blocks;

public interface BreakListener {
	
	
	//true ha sajat inventory visszarakast akarsz
	// fhnev csak multiplayerben szamit
	boolean breaked(String username);
	//void breakedOnServer();
}
