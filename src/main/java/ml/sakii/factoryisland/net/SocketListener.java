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
	private TCPListener Listener;
	public int port;
	public String success="OK";

	public SocketListener(TCPListener Listener) {
		try {
			try {
	            this.socket = new ServerSocket(GameServer.DEFAULT_PORT);
	        } catch (@SuppressWarnings("unused") IOException ex) {
	        	this.socket = new ServerSocket(0);
	        }
			this.port = socket.getLocalPort();
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(Main.Frame.getContentPane(), "Could not start server: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			success = e.getMessage();
		}
		try {
			this.socket.setReceiveBufferSize(8196);
		} catch (SocketException e) {
			success=e.getMessage();
			e.printStackTrace();
		}
		this.Listener = Listener;
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
	    	} catch (IOException e) {
					Main.log(e.getMessage());
	    	}
    	}

    }
	
	public void kill() {
		running=false;
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
