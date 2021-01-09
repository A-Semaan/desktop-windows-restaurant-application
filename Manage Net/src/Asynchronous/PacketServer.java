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
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;

import javax.swing.JOptionPane;

import com.sun.nio.sctp.SendFailedNotification;

import DataClasses.Category;
import DataClasses.MenuObject;
import DataClasses.OrderItem;
import MainPackage.ManagerFrame;
import MainPackage.ReportPanel;
import MainPackage.ServerFrame;
import MainPackage.StockPanel;
import Packets.ConnectivityOperation;
import Packets.ConnectivityReply;
import Packets.DataPacket;
import Packets.Flags;
import Packets.Packet;
import Packets.PayPacket;
import Packets.Ports;
import Packets.ReplyPacket;
import Packets.RequestPacket;
import Packets.SQLPacket;
import shared.AssistantManager;
import shared.User;
import shared.Waiter;

public class PacketServer extends Thread implements Flags,Ports{
	
	public static ArrayList<MenuObject> trackables = new ArrayList<MenuObject>();
	
	private DatagramSocket ss;
	
	public PacketServer() {
		System.setProperty("java.net.preferIPv4Stack" , "true");
		try {
			ss = new DatagramSocket(REQUEST_PORT);
			getTrackables();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		synchronized (ss) {
			do {
				
				try {
					
					System.out.println("PACKET SERVER DEPLOYED");
					byte[] buf = new byte[2048];
					DatagramPacket packet = new DatagramPacket(buf, buf.length);
		            ss.receive(packet);
		            System.out.println("PACKET OBJECT RECIEVED");
		            InetAddress address = packet.getAddress();
		            int port = packet.getPort();
		            System.out.println("here1");
					
					byte[] actuals = packet.getData();
					System.out.println("here2");
					ByteArrayInputStream bis = new ByteArrayInputStream(actuals);
					System.out.println("here3");
					Object o = new ObjectInputStream(bis).readObject();
					
					System.out.println("here4");
					if(o instanceof Packet) {
						Packet p = (Packet)o;
						System.out.println("INSTANCE OF PACKET RECEIVED");
						if(p instanceof SQLPacket) {
							DatabaseThread.update(((SQLPacket) p).getSql());
						}
						else if(o instanceof ReplyPacket) {
							
							ReplyPacket rep1 = (ReplyPacket) p;
							System.out.println("REPLY PACKET RECEIVED, FORWARDING TO "+rep1.getDestinationIP());
							ReplyPacket rep2 = new ReplyPacket(rep1.getSourceIP(), rep1.getDestinationIP(), 
									rep1.getSourceUserType(), rep1.getDestinationUserType(), rep1.getRequest());
							//sendReplyPacket(rep2.getDestinationIP(),USER_REQUEST_PORT,rep2);
							DatagramSocket socket = new DatagramSocket();
					        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
					        final ObjectOutputStream oos = new ObjectOutputStream(baos);
					        oos.writeObject(new String(""));
					        oos.flush();
					        byte[] buffer = baos.toByteArray();

					        DatagramPacket packet2
					                = new DatagramPacket(buffer, buffer.length,rep1.getDestinationIP(), USER_REQUEST_PORT);
					        socket.send(packet2);
					        System.out.println(packet2.toString()+" "+packet2.getAddress()+" "+packet2.getPort());
					        socket.close();
							break;
						}
						else if(p instanceof PayPacket) {
							PayPacket pp = (PayPacket)p;
							ArrayList<OrderItem> orderItems = pp.getOrderItems();
							double total = pp.getTotal();
							User user = pp.getUser();
							String sql="INSERT INTO Orders (user,time_stamp,total) "
									+ "VALUES((SELECT _id FROM User WHERE username=\""+user.getUsername()+"\"),"
									+ "\""+DatabaseThread.sqlDateTimeFormat.format(new Date())+"\","
									+ " "+total+")";
							System.out.println(sql);
							DatabaseThread.update(sql);
							
							ResultSet rs = DatabaseThread.query("SELECT last_insert_rowid()");
							rs.next();
							int addedOrderId = rs.getInt(1);
							
							for (OrderItem oi:orderItems) {
								sql = "INSERT INTO OrderDetails (order_id,menu_object,quantity,note)"
										+ "VALUES("+addedOrderId+",(SELECT _id FROM MenuObject WHERE menu_object_name"
										+ "= \""+oi.getMenuObject().getName()+"\"), "+oi.getQtyOfMenuObject()+", "
										+ " \""+oi.getNote()+"\")";
								System.out.println(sql);
								DatabaseThread.update(sql);
								if(contains(trackables,oi.getMenuObject())) {
									int quantity = oi.getQtyOfMenuObject();
									String dateModified = DatabaseThread.sqlDateFormat.format(new Date());
									
									sql= "UPDATE StockObject SET quantity=quantity-"+quantity+", "
											+ "date_modified=\""+dateModified+"\" WHERE name=\""+oi.getMenuObject().getName()+"\"";
									System.out.println("ELEMENT IS TRACKABLE updating with\n"+sql);
									DatabaseThread.update(sql);
									sql = "INSERT INTO StockRecord (object_id,quantity,date)"
											+ "VALUES((SELECT _id FROM StockObject WHERE name=\""+oi.getMenuObject().getName()+"\"), "
											+ ""+quantity+", \""+dateModified+"\")";
									System.out.println(sql);
									DatabaseThread.update(sql);
								}
								
							}
							StockPanel.populateStockTable();
							ReportPanel.refresh();
							
							
						}
						else if(p instanceof RequestPacket) {
							System.out.println("INSTANCE OF REQUEST RECEIVED");
							RequestPacket rp = (RequestPacket) p;
							System.out.println(p.toString());
							switch(rp.getRequest()) {
							case REQUEST_REPORT_EXPORT_RIGHTS:
								ManagerFrame.showRequestDialog((RequestPacket) p);
								break;
							case REQUEST_TABLE_NUMBER:
								
								ResultSet rs = DatabaseThread.query("SELECT * FROM \"RestaurantTables\"");
								rs.next();
								int num=rs.getInt(1);
								String[] returnable = new String[num];
								for(int j=0;j<returnable.length;j++) {
									returnable[j]=(j+1)+"";
								}
								ReplyPacket repPacket = new ReplyPacket(rp.getDestinationIP().toString(), address, rp.getDestinationUserType(), rp.getSourceUserType(), REPLY_TABLE_NUMBER);
								repPacket.setData(returnable);
								sendReplyPacket(repPacket.getDestinationIP(),USER_REQUEST_PORT,repPacket);
								break;
							case REQUEST_MENU:
								HashMap<String, ArrayList<MenuObject>> returnableMenu = new HashMap<String, ArrayList<MenuObject>>();
								ResultSet rs2 = DatabaseThread.query("SELECT * FROM MenuObject m,Category c WHERE m.categ=c._id ORDER BY c.category_name,m.menu_object_name ");
								//returned as _id | categ | menu_object_name | description | price | _id | category_name
								String currentCategory="";
								ArrayList<MenuObject> list = new ArrayList<MenuObject>();
								if(rs2.next()) {
									do{
										
										System.out.println(rs2.getString("category_name"));
										String readCategory = rs2.getString("category_name");
										if(currentCategory.equals(""))
											currentCategory=readCategory;
										if(!currentCategory.equals(readCategory)) {
											returnableMenu.put(currentCategory, list);
											currentCategory=readCategory;
											list=new ArrayList<MenuObject>();
										}
										int id = rs2.getInt(1);
										String name = rs2.getString(3);
										String description = rs2.getString(4);
										double price = rs2.getDouble(5);
										Category category = new Category(rs2.getInt(7), rs2.getString("category_name"));
										MenuObject mo = new MenuObject(id, name, description, price, category);
										list.add(mo);
									}while(rs2.next());
									returnableMenu.put(currentCategory, list);
									System.out.println("SIZE OF MENU CATEGS IS " + returnableMenu.keySet().size());
									
								}
								else {
									System.out.println("RESULT SET IS EMPTY");
								}
								ReplyPacket repPacket2 = new ReplyPacket(rp.getDestinationIP().toString(), address, rp.getDestinationUserType(), rp.getSourceUserType(), REPLY_MENU);
								System.out.println(returnableMenu.toString()+"  "+returnableMenu.keySet().size());
								repPacket2.setMenu(returnableMenu);
								sendReplyPacket(repPacket2.getDestinationIP(),USER_REQUEST_PORT,repPacket2);
								break;
							case REQUEST_CONNECTED_WAITERS:
								System.out.println("CONNECTED WAITERS REQUEST RECEIVED");
								ReplyPacket repPacketwaiters = new ReplyPacket(rp.getDestinationIP().toString(), InetAddress.getByName(rp.getSourceIP()),User.SERVER, User.CASHIER, REPLY_CONNECTED_WAITERS);
								ArrayList<String> waiters = new ArrayList<String>();
								System.out.println("CONNECTED WAITERS REPLY PACKET CREATED");
								Iterator<Waiter> it = ConnectivityServer.getWaiterHashMap().values().iterator();
								while(it.hasNext()) {
									Waiter w = it.next();
									waiters.add(w.getUsername());
								}
								repPacketwaiters.setData(waiters);
								System.out.println("CONNECTED WAITERS DATA ADDED");
								sendReplyPacket(repPacketwaiters.getDestinationIP(), USER_REQUEST_PORT, repPacketwaiters);
								System.out.println("CONNECTED WAITERS PACKET SENT");
								break;
							case ORDER_EDITING_REQUEST:
								RequestPacket initial = (RequestPacket) p;
								System.out.println("USER ASKING FOR PERMISSION");
								if(ServerFrame.isLoggedIn()) {
									System.out.println("Manager online");
									ManagerFrame.showRequestDialog(initial);
								}
								else {
									
									User u = ConnectivityServer.getWaiterHashMap().get(InetAddress.getByName(((RequestPacket) p).getSourceIP()));
									System.out.println("Manager offline sending  from "+u);
									RequestPacket replyP = new RequestPacket(initial.getSourceIP(), initial.getDestinationIP(), initial.getSourceUserType(), initial.getDestinationUserType(), Flags.ORDER_EDITING_REQUEST);
									replyP.setData(u);
									for(InetAddress ip : ConnectivityServer.getCashierHashMap().keySet()) {
										sendPacket(replyP, ip);
									}
									
								}
								System.out.println("editing request taken care of");
								break;
								
							}
							
							
						}
						
					}
					else {
						System.out.println(o.toString());
					}
					//TODO
					
				} catch (IOException e) {	
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}while(true);
		}
	}
	
	public static void approveReportAccess(RequestPacket p) throws IOException {
		
		ReplyPacket rp = new ReplyPacket(InetAddress.getLocalHost().toString(), InetAddress.getByName(((RequestPacket) p).getSourceIP()), User.MANAGER, User.ASSISTANT_MANAGER, REPORT_EXPORT_RIGHTS_GRANTED);
		sendReplyPacket(InetAddress.getByName(p.getSourceIP()),USER_REQUEST_PORT,rp);
		
	}
	
	public static void denyReportAccess(RequestPacket p) throws IOException {
		ReplyPacket rp = new ReplyPacket(InetAddress.getLocalHost().toString(), InetAddress.getByName(((RequestPacket) p).getSourceIP()), User.MANAGER, User.ASSISTANT_MANAGER, REPORT_EXPORT_RIGHTS_DENIED);
		sendReplyPacket(InetAddress.getByName(p.getSourceIP()),USER_REQUEST_PORT,rp);
	}
	
	public static void approveOrderEditing(RequestPacket p) throws IOException {
		
		ReplyPacket rp = new ReplyPacket(InetAddress.getLocalHost().toString(), InetAddress.getByName(((RequestPacket) p).getSourceIP()), User.MANAGER, User.WAITER, ORDER_EDITING_APPROVED);
		sendReplyPacket(InetAddress.getByName(p.getSourceIP()),USER_REQUEST_PORT,rp);
		
	}
	
	public static void denyOrderEditing(RequestPacket p) throws IOException {
		ReplyPacket rp = new ReplyPacket(InetAddress.getLocalHost().toString(), InetAddress.getByName(((RequestPacket) p).getSourceIP()), User.MANAGER, User.WAITER, ORDER_EDITING_DENIED);
		sendReplyPacket(InetAddress.getByName(p.getSourceIP()),USER_REQUEST_PORT,rp);
	}
	
	private static void sendReplyPacket(InetAddress address, int port,ReplyPacket o) throws IOException {
		DatagramSocket socket = new DatagramSocket();
		System.out.println("1");
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.out.println("12");
        final ObjectOutputStream oos = new ObjectOutputStream(baos);
        System.out.println("123");
        oos.writeObject(o);
        System.out.println("1234");
        oos.flush();
        System.out.println("12345");
        byte[] buffer = baos.toByteArray();
        System.out.println("123456");
        DatagramPacket packet2
                = new DatagramPacket(buffer, buffer.length,address, port);
        System.out.println("1234567");
        socket.send(packet2);
        System.out.println(packet2.toString()+" "+packet2.getAddress()+" "+packet2.getPort());
        socket.close();
	}
	private static void sendPacket(RequestPacket p,InetAddress ip) throws IOException {
		DatagramSocket socket = new DatagramSocket();
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(p);
        oos.flush();
        byte[] buffer = baos.toByteArray();

        DatagramPacket packet2
                = new DatagramPacket(buffer, buffer.length,ip, USER_REQUEST_PORT);
        socket.send(packet2);
        System.out.println(packet2.toString()+" "+packet2.getAddress()+" "+packet2.getPort());
        socket.close();
	}
	private void getTrackables() throws SQLException {
		ResultSet rs = DatabaseThread.query("SELECT * FROM MenuObject WHERE trackable=true");
		while(rs.next()) {
			trackables.add(new MenuObject(rs.getInt("_id"),
					rs.getString("menu_object_name"), rs.getString("description"),
					rs.getDouble("price"), new Category(rs.getInt("categ"),"")));
		}
	}
	public static boolean contains(ArrayList<MenuObject> track,MenuObject mo) {
		for(MenuObject temp:track) {
			System.out.println("Comparing:\n\t"+temp.getName()+" and "+mo.getName());
			if(temp.compareTo(mo)==0)
				return true;
		}
		return false;
	}
}
