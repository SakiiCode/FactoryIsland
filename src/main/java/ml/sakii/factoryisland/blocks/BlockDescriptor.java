package ml.sakii.factoryisland.blocks;

import java.util.ArrayList;
import java.util.List;

public interface BlockDescriptor {

	final static ArrayList<String> ANY = new ArrayList<>();
	
	default List<String> getCanBePlacedOn(){
		return ANY;
	}
	default int getLightLevel() {
		return 0;
	}
	default boolean isFullBlock() {
		return true;
	}
	default boolean isTransparent() {
		return false;
	}
	default boolean isSolid() {
		return true;
	}
	default int getRefreshRate() {
		return 1;
	}
}
