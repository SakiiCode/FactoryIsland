package ml.sakii.factoryisland.blocks;

public enum BlockFace {
	TOP(new byte[]{0,0,1}, 0, 1),
	BOTTOM(new byte[]{0,0,-1}, 1, 0),
	NORTH(new byte[]{0,1,0}, 2, 3),
	SOUTH(new byte[]{0,-1,0}, 3, 2),
	EAST(new byte[]{1,0,0}, 4, 5),
	WEST(new byte[]{-1,0,0}, 5, 4),
	NONE(new byte[]{0,0,0}, 6, 6);
	
	public byte[] direction;
	public int id;
	public int opposite;
	public static BlockFace[] values = new BlockFace[7];
	public static BlockFace[] ALL = {BlockFace.TOP, BlockFace.BOTTOM, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST};
	
	private BlockFace(byte[] direction, int id, int opposite){
		this.direction = direction;
		this.id = id;
		this.opposite=opposite;
	}
	
	public BlockFace[] getNearby(){
		BlockFace[] ids = new BlockFace[4];
		int idIndex = 0;
		BlockFace opposite = getOpposite();
		for(BlockFace face : BlockFace.values){
			if(face != this && face != opposite && face != BlockFace.NONE){
				ids[idIndex] = face;
				idIndex++;
			}
		}
		
		return ids;
	}
	
	public BlockFace getOpposite(){
		return values[opposite];
	}
	

	static {
		values[0] = TOP;
		values[1] = BOTTOM;
		values[2] = NORTH;
		values[3] = SOUTH;
		values[4] = EAST;
		values[5] = WEST;
		values[6] = NONE;
		
	
	}
}
