package ml.sakii.factoryisland.net;

@Deprecated
public class Packet {
	Connection connection;
	String message;
	
	public Packet(Connection connection, String message) {
		this.connection = connection;
		this.message = message;
	}
}
