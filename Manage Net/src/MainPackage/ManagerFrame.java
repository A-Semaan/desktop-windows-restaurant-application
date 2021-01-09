package MainPackage;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTabbedPane;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.border.LineBorder;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.InsetsUIResource;

import Asynchronous.ConnectivityServer;
import Asynchronous.PacketServer;
import Packets.Flags;
import Packets.RequestPacket;

import java.awt.SystemColor;
import javax.swing.JTable;

public class ManagerFrame extends JFrame {

	private JPanel contentPane;
	private static ManagerFrame thiss;

	/**
	 * Create the frame.
	 */
	public ManagerFrame() {
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		getContentPane().setLayout(null);
		this.setSize(800,600);
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setLayout(null);
		setContentPane(contentPane);
		contentPane.setSize(this.getWidth(), this.getHeight());
		this.setLocationRelativeTo(null);
		UIManager.put("TabbedPane.borderHightlightColor", SystemColor.controlHighlight);
		UIManager.put("TabbedPane.selected", SystemColor.controlHighlight);
		UIManager.put("TabbedPane.contentBorderInsets", new InsetsUIResource(1, 1, 1, 1));
		UIManager.put("TabbedPane.contentAreaColor", new ColorUIResource(SystemColor.controlHighlight));
		
		thiss=this;
		JButton logout = new JButton("Logout");
		logout.setBackground(SystemColor.controlHighlight);
		logout.setFont(new Font("Arial", Font.PLAIN, 15));
		logout.setBounds(this.getWidth()-200,30,150, 30);
		contentPane.add(logout);
		logout.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ServerFrame.logout();
				//ManagerFrame.this.dispatchEvent(new WindowEvent(ManagerFrame.this, WindowEvent.WINDOW_CLOSING));
				ManagerFrame.this.dispose();
			}
		});
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setForeground(Color.BLACK);
		tabbedPane.setBackground(Color.WHITE);
		tabbedPane.setFont(new Font("Arial", Font.PLAIN, 21));
		
		
		tabbedPane.setBounds(50,70,this.getWidth()-100, this.getHeight()-130);
		
		UserPanel userpanel=new UserPanel(tabbedPane.getWidth(),tabbedPane.getHeight());
		MenuPanel menuPanel=new MenuPanel(tabbedPane.getWidth(),tabbedPane.getHeight());
		StockPanel stockPanel=new StockPanel(tabbedPane.getWidth(),tabbedPane.getHeight());
		ReportPanel reportPanel=new ReportPanel(tabbedPane.getWidth(),tabbedPane.getHeight());
		
		tabbedPane.addTab("User",userpanel);
		
		
		tabbedPane.addTab("Menu", menuPanel);
		tabbedPane.addTab("Stock Management", stockPanel);
		tabbedPane.addTab("Report", reportPanel);
		contentPane.add(tabbedPane);
		
		
	}
	
	@Override
	public void validate() {
		updateUI(getContentPane());
		
		super.validate();
	}
	private void updateUI(Container comp) {
		Component[] comps = comp.getComponents();
		//System.out.println("RESIZED size="+comps.length);
		for(Component component:comps) {
			if(component instanceof Container) {
				//System.out.println("RESIZED FORKING");
				updateUI((Container)component);
				
			}
			else if(component instanceof JButton){
				component.setBounds(this.getWidth()-200,30,150, 30);
				//System.out.println("RESIZED JBUTTON");
			}
			else if(component instanceof JTabbedPane) {
				//System.out.println("RESIZED JTABBEDPANE");
				component.setBounds(50,70,this.getWidth()-100, this.getHeight()-130);
			}
			else {
				//System.out.println("NOT RESIZED " +component.toString());
			}
		}
		if(comp instanceof JButton){
			comp.setBounds(this.getWidth()-200,30,150, 30);
			//System.out.println("RESIZED JBUTTON");
		}
		else if(comp instanceof JTabbedPane) {
			//System.out.println("RESIZED JTABBEDPANE");
			comp.setBounds(50,70,this.getWidth()-100, this.getHeight()-130);
		}
		else {
			//System.out.println("NOT RESIZED " +comp.toString());
		}
		
	}
	
	public static void showRequestDialog(RequestPacket p) {
		if(p.getRequest()==Flags.REQUEST_REPORT_EXPORT_RIGHTS) {
			if(JOptionPane.showConfirmDialog(thiss, ConnectivityServer.getWaiterHashMap().get(((RequestPacket) p).getSourceIP()).getUsername()+"  is trying to edit an order.", "Report Access Request", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE)==JOptionPane.YES_OPTION) {
				try {
					PacketServer.approveReportAccess(p);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else {
				try {
					PacketServer.denyReportAccess(p);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		else if(p.getRequest()==Flags.ORDER_EDITING_REQUEST) {
			//System.out.println(ConnectivityServer.getWaiterHashMap().keySet().toString());
			try {
				if(JOptionPane.showConfirmDialog(thiss, ConnectivityServer.getWaiterHashMap().get(InetAddress.getByName(((RequestPacket) p).getSourceIP())).getUsername()+"  is trying to edit an order.", "Report Access Request", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE)==JOptionPane.YES_OPTION) {
					try {
						PacketServer.approveOrderEditing(p);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else {
					try {
						PacketServer.denyOrderEditing(p);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} catch (HeadlessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
