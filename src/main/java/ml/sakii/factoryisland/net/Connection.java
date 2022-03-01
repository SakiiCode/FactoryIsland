package ml.sakii.factoryisland.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

@Deprecated
public class Connection {
	BufferedWriter outputStream;
	BufferedReader inputStream;
	static final int PROTOCOL_VERSION = 1;

	public Connection(BufferedReader inputStream ,  BufferedWriter outputStream) {
		this.inputStream = inputStream;
		this.outputStream = outputStream;
	}
	
	void close() {
		try {
			inputStream.close();
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
