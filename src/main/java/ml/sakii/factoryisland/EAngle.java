package ml.sakii.factoryisland;

public class EAngle {

	public float pitch, yaw;
	private Vector toVector=new Vector();
	
	public EAngle(){
		this.pitch=0f;
		this.yaw=0f;
	}
	
	public EAngle(float yaw, float pitch){
		this.pitch = pitch;
		this.yaw = yaw;
	}
	
	public Vector toVector(){
		double yaw2 = Math.toRadians(yaw);
		double pitch2 = Math.toRadians(pitch);
		toVector.set(
				(float) (Math.cos(yaw2)*Math.cos(pitch2)),
				(float) (Math.sin(yaw2)*Math.cos(pitch2)),
				(float) Math.sin(pitch2)).normalize();
		return toVector;
		
		
	}
	
	public void normalize(){
		if(pitch>89.999f)
			pitch = 89.999f;
		if(pitch<-89.999)
			pitch = -89.999f;
		
		if(yaw <-180)
			yaw += 360;
		if(yaw >180)
			yaw -= 360;
		if(yaw == Float.NaN || yaw == Float.NEGATIVE_INFINITY || yaw == Float.POSITIVE_INFINITY) {
			Main.err("yaw float error");
			yaw = 0;
			
		}
			
	}

	public void set(Vector angles) {
		yaw =angles.x;
		pitch = angles.y;
	}
	
	public void set(EAngle angle) {
		yaw=angle.yaw;
		pitch=angle.pitch;
	}
	
	public void set(float yaw, float pitch) {
		this.yaw=yaw;
		this.pitch=pitch;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(pitch);
		result = prime * result + Float.floatToIntBits(yaw);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof EAngle))
			return false;
		EAngle other = (EAngle) obj;
		if (Float.floatToIntBits(pitch) != Float.floatToIntBits(other.pitch))
			return false;
		if (Float.floatToIntBits(yaw) != Float.floatToIntBits(other.yaw))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		
		return yaw+","+pitch;
	}
	
	public static EAngle parseEAngle(String str) {
		String[] arr = str.split(",");
		
		return new EAngle(Float.parseFloat(arr[0]), Float.parseFloat(arr[1]));
		
	}
	

}
