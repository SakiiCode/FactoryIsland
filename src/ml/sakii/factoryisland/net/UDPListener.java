package ml.sakii.factoryisland.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;
import ml.sakii.factoryisland.net.PacketEntry;

public class UDPListener extends Thread {
	
	protected DatagramSocket socket;
	public CopyOnWriteArrayList<PacketEntry<DatagramPacket,String>> packets = new CopyOnWriteArrayList<>();
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
		for(Entry<DatagramPacket, String> entry : packets){
			if(entry == null)
				continue;
			packets.remove(entry);
			return entry;
		}
		return null;
	}
	
	
	public void kill(){
		running=false;
	}

}
