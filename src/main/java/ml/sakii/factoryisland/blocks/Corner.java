package ml.sakii.factoryisland.blocks;

import ml.sakii.factoryisland.Point3D;

public enum Corner {
	UP_LEFT(0), UP_RIGHT(1), DOWN_RIGHT(2), DOWN_LEFT(3);
	
	
	public int id;
	
	private Corner(int id) {
		this.id=id;
	}
	
	public static Corner fromDelta(BlockFace face, Point3D delta) {
		int x = (delta.x+1)/2;
		int y = (delta.y+1)/2;
		int z = (delta.z+1)/2;
		int magicNumber;
		
		switch(face) {
		
		case TOP: magicNumber = Math.floorMod((x << 1 | y) + 2, 4); break;
		case BOTTOM: magicNumber = x << 1 | y; break;
		case SOUTH: magicNumber = Math.floorMod((x << 1 | z) + 2, 4); break;
		case NORTH: magicNumber = x << 1 | z; break;
		case EAST: magicNumber = Math.floorMod((y << 1 | z) + 2, 4); break;
		case WEST: magicNumber = y << 1 | z; break;
		default: magicNumber=0;
		
		}
		
		switch(magicNumber) {
		case 3: return UP_LEFT;
		case 1: return UP_RIGHT;
		case 0: return DOWN_RIGHT;
		case 2: return DOWN_LEFT;
		default: return UP_LEFT;
		}
		
	}

	
	public int toInt() {
		return id;
	}
	
}
