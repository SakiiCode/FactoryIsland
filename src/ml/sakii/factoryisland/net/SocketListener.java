package ml.sakii.factoryisland.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import javax.swing.JOptionPane;

import ml.sakii.factoryisland.Main;

public class SocketListener extends Thread {

	private boolean running=true;
	private ServerSocket socket;
	//private CopyOnWriteArrayList<Connection> Connections;
	private TCPListener Listener;
	public int port;

	public SocketListener(TCPListener Listener) {
		try {
			try {
	            this.socket = new ServerSocket(1420);
	        } catch (@SuppressWarnings("unused") IOException ex) {
	        	this.socket = new ServerSocket(0);
	        }
			this.port = socket.getLocalPort();
			JOptionPane.showMessageDialog(Main.Frame.getContentPane(), "Server opened to LAN at port " + socket.getLocalPort(), "Info", JOptionPane.INFORMATION_MESSAGE);
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(Main.Frame.getContentPane(), "Could not start server: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
		try {
			this.socket.setReceiveBufferSize(8196);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		this.Listener = Listener;
		//this.Connections = connections;
	}
	
	@Override
    public void run() {
    	while(running){
    		try {
				@SuppressWarnings("resource")
				Socket s = socket.accept();
				s.setSendBufferSize(8196);
				s.setReceiveBufferSize(8196);
				Connection conn = new Connection(new BufferedReader(new InputStreamReader(s.getInputStream())), new BufferedWriter(new OutputStreamWriter(s.getOutputStream())));
				Listener.Connections.add(conn);
				//Connections.add(ss);
	    	} catch (IOException e) {
	    		//if(e.getMessage().equalsIgnoreCase("Socket closed")) {
	    			//Main.err(e.getMessage());
	    		//	e.printStackTrace();
	    		//}else {
					e.printStackTrace();
	    		//}
	    	}
    	}
    	
    	try {
			this.socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	
	public void kill() {
		running=false;
	}
}
