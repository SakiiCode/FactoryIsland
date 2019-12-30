package ml.sakii.factoryisland.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class Connection {
	BufferedWriter outputStream;
	BufferedReader inputStream;
	static final int PROTOCOL_VERSION = 1;

	//private Socket socket;
	//public LinkedList<String> lines = new LinkedList<>();
	//public String message="";
	
	public Connection(BufferedReader inputStream ,  BufferedWriter outputStream) {
		this.inputStream = inputStream;
		this.outputStream = outputStream;
		//this.socket = socket;
	}
	
	public void close() {
		try {
			inputStream.close();
			outputStream.close();
			//socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
