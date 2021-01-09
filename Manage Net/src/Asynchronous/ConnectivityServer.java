package Asynchronous;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import Packets.ConnectivityOperation;
import Packets.ConnectivityReply;
import Packets.Flags;
import Packets.Ports;
import shared.AssistantManager;
import shared.Cashier;
import shared.ServerAutomatedUser;
import shared.User;
import shared.Waiter;

public class ConnectivityServer extends Thread implements Ports,Flags{
	
	private DatagramSocket ss;
	
	private static HashMap<InetAddress, AssistantManager> assistantManagers = new HashMap<InetAddress, AssistantManager>();
	private static HashMap<InetAddress, Cashier> cashiers = new HashMap<InetAddress, Cashier>();
	private static HashMap<InetAddress, Waiter> waiters = new HashMap<InetAddress, Waiter>();
	
	private static HashMap<InetAddress, Double> cashierBalances = new HashMap<InetAddress, Double>();
	private static HashMap<InetAddress, Double> waiterBalances = new HashMap<InetAddress, Double>();
	
	
	public ConnectivityServer() {
		System.setProperty("java.net.preferIPv4Stack" , "true");
		try {
			ss = new DatagramSocket(CONNECTIVITY_PORT);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		synchronized (ss) {
			do {
				
				try {
					
					System.out.println("CONNECTIVITY SERVER DEPLOYED");
					byte[] buf = new byte[2048];
					DatagramPacket packet = new DatagramPacket(buf, buf.length);
		            ss.receive(packet);
		            System.out.println("CONNECTIVITY OBJECT RECIEVED");
		            InetAddress address = packet.getAddress();
		            int port = packet.getPort();
		            System.out.println("here1");
					
					byte[] actuals = packet.getData();
					System.out.println("here2");
					ByteArrayInputStream bis = new ByteArrayInputStream(actuals);
					System.out.println("here3");
					Object o = new ObjectInputStream(bis).readObject();
					
					System.out.println("here4 connectivity");
					if(o instanceof ConnectivityOperation) {
						ConnectivityOperation co = (ConnectivityOperation)o;
						
						switch(co.getOperation()) {
						case CONNECTION_REQUEST:
							analyseConnection(packet,co);
							break;
						case DISCONNECTION_REQUEST:
							analyseDisconnection(packet);
							break;
						}
					}
					//TODO
					
				} catch (IOException e) {	
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				
			}while(true);
		}
	}
	
	public void analyseConnection(DatagramPacket p,ConnectivityOperation co) throws IOException {
		
		String username = co.getUserInfo().getUsername();
		String password = co.getUserInfo().getPassword();
		try {
			ResultSet rs = DatabaseThread.query("SELECT * FROM user WHERE username=\""+username+"\" AND password=\""+password+"\"");
			if(!rs.next()) {
				respondNegativelyConnection(p);
			}else {
				User u = null;
				System.out.println(rs.getInt(4));
				switch(rs.getInt(4)) {
				
				case User.MANAGER: case User.ASSISTANT_MANAGER:
					u=new AssistantManager(username, password);
					assistantManagers.put(p.getAddress(),new AssistantManager(username, password));
					
					break;
				case User.CASHIER:
					u= new Cashier(username, password);
					cashiers.put(p.getAddress(), new Cashier(username, password));
					cashierBalances.put(p.getAddress(), 0.0);
					break;
				case User.WAITER:
					u= new Waiter(username, password);
					waiters.put(p.getAddress(), new Waiter(username, password));
					waiterBalances.put(p.getAddress(), 0.0);
					break;
				}
				if(u==null)
					respondNegativelyConnection(p);
				else
					respondPositivelyConnection(p,u);
				
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void analyseDisconnection(DatagramPacket p) throws IOException {
		
		if(assistantManagers.containsKey(p.getAddress())) {
			assistantManagers.remove(p.getAddress());
		}
		else if(cashiers.containsKey(p.getAddress())) {
			cashiers.remove(p.getAddress());
			cashierBalances.remove(p.getAddress());
		}
		else if(waiters.containsKey(p.getAddress())) {
			waiters.remove(p.getAddress());
			waiterBalances.remove(p.getAddress());
		}
	
		
		respondPositivelyDisconnection(p);
	}
	
	public static HashMap<InetAddress, AssistantManager> getAssistantManagerHashMap(){
		return assistantManagers;
	}
	
	public static HashMap<InetAddress, Cashier> getCashierHashMap(){
		return cashiers;
	}
	
	public static HashMap<InetAddress, Waiter> getWaiterHashMap(){
		return waiters;
	}
	
	private void respondPositivelyConnection(DatagramPacket p,User u) throws IOException {
		System.out.println(p.getAddress());
		ConnectivityReply coReply = new ConnectivityReply(CONNECTION_APPROVED, u);
		DatagramSocket socket = new DatagramSocket();
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(coReply);
        oos.flush();
        byte[] buffer = baos.toByteArray();

        DatagramPacket packet2
                = new DatagramPacket(buffer, buffer.length,p.getAddress(), USER_CONNECTIVITY_PORT);
        socket.send(packet2);
        System.out.println(packet2.toString()+" "+packet2.getAddress()+" "+packet2.getPort());
        socket.close();
	}
	
	private void respondNegativelyConnection(DatagramPacket p) throws IOException {
		ConnectivityReply coReply = new ConnectivityReply(CONNECTION_DENIED, new ServerAutomatedUser());
		sendPacket(p.getAddress(), USER_CONNECTIVITY_PORT, coReply);
	}
	
	private void respondPositivelyDisconnection(DatagramPacket p) throws IOException { 
		ConnectivityReply coReply = new ConnectivityReply(DISCONNECTION_APPROVED, new ServerAutomatedUser());
		System.out.println("RESPONDING POSITIVELY");
		sendPacket(p.getAddress(), USER_CONNECTIVITY_PORT, coReply);
	}
	
	private void respondNegativelyDisconnection(DatagramPacket p) throws IOException {
		ConnectivityReply coReply = new ConnectivityReply(DISCONNECTION_DENIED, new ServerAutomatedUser());
		sendPacket(p.getAddress(), USER_CONNECTIVITY_PORT, coReply);
	}
	
	private void sendPacket(InetAddress address, int port,ConnectivityReply o) throws IOException {
		DatagramSocket socket = new DatagramSocket();
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(o);
        oos.flush();
        byte[] buffer = baos.toByteArray();

        DatagramPacket packet2
                = new DatagramPacket(buffer, buffer.length,address, port);
        System.out.println("ATTEMPTING TO SEND "+ packet2);
        socket.send(packet2);
        System.out.println(packet2.toString()+" "+packet2.getAddress()+" "+packet2.getPort());
        socket.close();
	}
}
