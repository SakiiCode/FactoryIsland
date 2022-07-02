package ml.sakii.factoryisland;

public abstract class Model {
	public String name;
	
	public GameEngine Engine;
	
	private Model(String name, GameEngine engine) {
		this.name = name;
		this.Engine = engine;
	}
	
	public abstract Vector getPos();
	
	public static class FP extends Model{
		public Vector ViewFrom;
		
		public FP(String name, Vector viewFrom, GameEngine engine) {
			super(name, engine);
			this.ViewFrom = viewFrom;
		}
		
		@Override
		public Vector getPos() {
			return ViewFrom;
		}
	}
	
	public static class Int extends Model{
		public int x, y, z;
		public Point3D pos;
		
		public Int(String name, int x, int y, int z, GameEngine engine){
			super(name, engine);
			this.x = x;
			this.y = y;
			this.z = z;
			pos = new Point3D(x,y,z);
		}
		
		@Override
		public Vector getPos() {
			return new Vector(pos.x,pos.y,pos.z);
		}
	}
	
	
}
