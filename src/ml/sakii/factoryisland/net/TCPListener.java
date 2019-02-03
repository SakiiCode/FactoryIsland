package ml.sakii.factoryisland.net;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

public class TCPListener extends Thread {
	
	private ConcurrentLinkedQueue<Packet> packets = new ConcurrentLinkedQueue<>();
	CopyOnWriteArrayList<Connection> Connections = new CopyOnWriteArrayList<>();
	private boolean running= true;
	public SocketListener acceptThread;
	
	public TCPListener(){
		this.setName("TCPListener");
		acceptThread = new SocketListener(this);
		acceptThread.start();
	}

	
	@Override
	public void run(){
		while(running){
			for(Connection conn : Connections){
				try {
					if(conn.inputStream.ready()) {
						String message = conn.inputStream.readLine();
						if(message != null && !message.isEmpty()){
							packets.add(new Packet(conn, message));
						}
					}
				}catch(Exception e) {
					break;
				}
			
			}
			try
			{
				Thread.sleep(GameClient.CMDTIME);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		
		}
		
		acceptThread.kill();

		for(Connection s : Connections){
			s.close();
		}
		Connections.clear();
		
		

		
	}
	
	
	public Packet read(){
		if(packets.size()>0){
			return packets.remove();
		}
		return null;
	}
	
	
	public void kill(){
		running=false;
		

	}

}
