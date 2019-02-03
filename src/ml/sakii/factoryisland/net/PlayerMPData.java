package ml.sakii.factoryisland.net;

import ml.sakii.factoryisland.EAngle;
import ml.sakii.factoryisland.Vector;
import ml.sakii.factoryisland.items.PlayerInventory;

public class PlayerMPData {
	public Connection socket;
	public 	Vector position;
	//public float yaw;
	//public float pitch;
	public EAngle aim;
	public String username;
	public PlayerInventory inventory;
	public boolean local;
	
	public PlayerMPData(String username, Vector pos, float yaw, float pitch, PlayerInventory inventory, Connection socket, boolean local){
		this.socket = socket;
		//this.ID = ID;
		this.position = pos;
		//this.yaw = yaw;
		this.username = username;
		this.inventory = inventory;
		//this.pitch = pitch;
		this.aim=new EAngle(yaw, pitch);
		this.local=local;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof PlayerMPData))
			return false;
		PlayerMPData other = (PlayerMPData) obj;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}
}
