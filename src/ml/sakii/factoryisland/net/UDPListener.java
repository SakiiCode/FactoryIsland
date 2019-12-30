package ml.sakii.factoryisland.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;
import ml.sakii.factoryisland.net.PacketEntry;

public class UDPListener extends Thread {
	
	protected DatagramSocket socket;
	public ConcurrentLinkedQueue<PacketEntry<DatagramPacket,String>> packets = new ConcurrentLinkedQueue<>();
	private boolean running= true;
	
	
	public UDPListener(int port){
		this.setName("UDPListener");
		try {
			this.socket = new DatagramSocket(port);
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
	}
	
	public UDPListener(){

		try {
			this.socket = new DatagramSocket();
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
	}
	
	@Override
	public void run(){
		while(running){

			byte[] data = new byte[8196];
			DatagramPacket packet = new DatagramPacket(data, data.length);
			boolean succeeded = true;
			try {
				socket.receive(packet);
			} catch (IOException e) {
				e.printStackTrace();
				succeeded = false;
			}
			if(succeeded)
				packets.add(new PacketEntry<>(packet,new String(packet.getData()).trim()));
		
		}
		this.socket.close();

		
	}
	
	
	public Entry<DatagramPacket,String> read(){
		return packets.poll();
	}
	
	
	public void kill(){
		running=false;
	}

}
