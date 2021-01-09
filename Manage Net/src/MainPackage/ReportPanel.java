package MainPackage;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.time.chrono.JapaneseDate;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;

import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JMenuItem;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultFormatter;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import Asynchronous.DatabaseThread;

public class ReportPanel extends JPanel {
	
	private UtilDateModel datemodel;
	private JDatePanelImpl datePanel;
	private JDatePickerImpl datePicker;
	private static JComboBox<String> reportMode;
	private DefaultTableModel reportTableModel;
	private JTable reportTable;
	private JPopupMenu tableMenu;
	private JMenuItem showMoreDetails;
	private JTextField totalTxt;
	
	
	private static DateFormat df = new SimpleDateFormat();
	
	public ReportPanel(int width,int height) {
		super();
		this.setLayout(null);
		this.setSize(width, height);
		this.setBackground(Color.WHITE);
		
		totalTxt = new JTextField();
		
		tableMenu = new JPopupMenu();
		showMoreDetails = new JMenuItem("Show Details");
		
		tableMenu.add(showMoreDetails);
		
		reportMode = new JComboBox<String>(new String[] {"By Day","By Month","By Year","By Menu Object","By User"});
		
		datemodel = new UtilDateModel();
		Calendar c = Calendar.getInstance();
		
		Date today = new Date();
		datemodel.setDate(c.get(Calendar.YEAR),c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
		datemodel.setSelected(true);
		datePanel = new JDatePanelImpl(datemodel,new Properties());
		AbstractFormatter abs = new AbstractFormatter() {
			
			@Override
			public String valueToString(Object value) throws ParseException {
				String s = value.toString();
				System.out.println(s);
				return s;
			}
			
			@Override
			public Object stringToValue(String text) throws ParseException {
				// TODO Auto-generated method stub
				return null;
			}
		};
		datePicker = new JDatePickerImpl(datePanel, new DefaultFormatter() {
			@Override
			public String valueToString(Object value) throws ParseException {
				if(value instanceof GregorianCalendar) {
					GregorianCalendar cal = (GregorianCalendar) value;
					SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
				    fmt.setCalendar(cal);
					return fmt.format(cal.getTime());
				}
					
				return super.valueToString(value);
			}
			
		});
		
		reportTableModel = new DefaultTableModel();
		reportTable = new JTable(reportTableModel) {
			@Override
			public boolean isCellEditable(int row, int column) {
				// TODO Auto-generated method stub
				return false;
			}
		};
		JScrollPane tablescroll = new JScrollPane(reportTable);
		
		datePicker.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Event invoked "+DatabaseThread.sqlDateFormat.format(datemodel.getValue()));
				try {
					switch(reportMode.getSelectedIndex()) {
					case 0:populateReportTableByDay(DatabaseThread.sqlDateFormat.format(datemodel.getValue()));
						break;
					case 1:populateReportTableByMonth(DatabaseThread.sqlDateFormat.format(datemodel.getValue()));
						break;
					case 2:populateReportTableByYear(DatabaseThread.sqlDateFormat.format(datemodel.getValue()));
						break;
					case 3:populateReportTableByMenuObject(DatabaseThread.sqlDateFormat.format(datemodel.getValue()));
						break;
					case 4:populateReportTableByUser(DatabaseThread.sqlDateFormat.format(datemodel.getValue()));
						break;
					}
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
		
		reportMode.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				try {
					switch(reportMode.getSelectedIndex()) {
					case 0:populateReportTableByDay(DatabaseThread.sqlDateFormat.format(datemodel.getValue()));
						break;
					case 1:populateReportTableByMonth(DatabaseThread.sqlDateFormat.format(datemodel.getValue()));
						break;
					case 2:populateReportTableByYear(DatabaseThread.sqlDateFormat.format(datemodel.getValue()));
						break;
					case 3:populateReportTableByMenuObject(DatabaseThread.sqlDateFormat.format(datemodel.getValue()));
						break;
					case 4:populateReportTableByUser(DatabaseThread.sqlDateFormat.format(datemodel.getValue()));
						break;
					}
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
		
		reportTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				if(reportTableModel.getColumnCount()==3)
					return;
				int i=reportTable.rowAtPoint(e.getPoint());
				if(i<0||i>reportTableModel.getRowCount())
					return;
				
				reportTable.setRowSelectionInterval(i, i);
				if(SwingUtilities.isRightMouseButton(e)) {
					tableMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});
		showMoreDetails.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int id = Integer.parseInt(reportTableModel.getValueAt(reportTable.getSelectedRow(), 0).toString());
				OrderDetailsDialog odd  =new OrderDetailsDialog(id);
				odd.setVisible(true);
			}
		});
		this.add(datePicker).setBounds((this.getWidth()/2)-80,10,160,30);
		this.add(reportMode).setBounds((this.getWidth()/2)-60,50,120,30);
		this.add(tablescroll).setBounds(20,100,this.getWidth()-40,this.getHeight()-140);
		totalTxt.setEditable(false);
		this.add(totalTxt).setBounds(this.getWidth()-150,50,100,20);
		
		
		try {
			populateReportTableByDay(DatabaseThread.sqlDateFormat.format(datemodel.getValue()));
			
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	private void populateReportTableByDay(String date) throws SQLException, ParseException{
		reportTableModel.setColumnIdentifiers(new String[] {"ID","Username","Time","Total"});
		reportTableModel.setRowCount(0);
		String sql = "SELECT * FROM Orders o, User u "
				+ "WHERE o.user=u._id "
				+ "AND o.time_stamp BETWEEN \""+date+" 00:00:00\" AND \""+date+" 23:59:59\"";
		System.out.println(sql);
		ResultSet rs = DatabaseThread.query(sql);
		//_id | user | time_stamp | total | _id | username | password | flag
		System.out.println("attempting to add row");
		while(rs.next()) {
			System.out.println("adding row");
			reportTableModel.addRow(new String[] {rs.getInt(1)+"",rs.getString("username"),rs.getString(3),rs.getString("total")});
		}
		if(reportTableModel.getRowCount()>0)
			reportTable.setRowSelectionInterval(0, 0);
		calculateTotal();
	}
	
	private void populateReportTableByMonth(String date) throws SQLException {
		reportTableModel.setColumnIdentifiers(new String[] {"ID","Username","Time","Total"});
		reportTableModel.setRowCount(0);
		String year = date.substring(0, 7);
		YearMonth yearMonthObject = YearMonth.of(Integer.parseInt(year.substring(0,4)),
				Integer.parseInt(year.substring(5,7)));
		int daysInMonth = yearMonthObject.lengthOfMonth();
		String dateStart = year+"-01";
		String dateEnd = year+"-"+daysInMonth;
		String sql = "SELECT * FROM Orders o, User u "
				+ "WHERE o.user=u._id "
				+ "AND o.time_stamp BETWEEN \""+dateStart+" 00:00:00\" AND \""+dateEnd+" 23:59:59\"";
		System.out.println(sql);
		ResultSet rs = DatabaseThread.query(sql);
		//_id | user | time_stamp | total | _id | username | password | flag
		System.out.println("attempting to add row");
		while(rs.next()) {
			System.out.println("adding row");
			reportTableModel.addRow(new String[] {rs.getInt(1)+"",rs.getString("username"),rs.getString(3),rs.getString("total")});
		}
		if(reportTableModel.getRowCount()>0)
			reportTable.setRowSelectionInterval(0, 0);
		calculateTotal();
	}
	
	private void populateReportTableByYear(String date) throws SQLException {
		reportTableModel.setColumnIdentifiers(new String[] {"ID","Username","Time","Total"});
		reportTableModel.setRowCount(0);
		String year = date.substring(0, 4);
		String dateStart = year+"-01-01";
		String dateEnd = year+"-12-31";
		String sql = "SELECT * FROM Orders o, User u "
				+ "WHERE o.user=u._id "
				+ "AND o.time_stamp BETWEEN \""+dateStart+" 00:00:00\" AND \""+dateEnd+" 23:59:59\"";
		System.out.println(sql);
		ResultSet rs = DatabaseThread.query(sql);
		//_id | user | time_stamp | total | _id | username | password | flag
		System.out.println("attempting to add row");
		while(rs.next()) {
			System.out.println("adding row");
			reportTableModel.addRow(new String[] {rs.getInt(1)+"",rs.getString("username"),rs.getString(3),rs.getString("total")});
		}
		if(reportTableModel.getRowCount()>0)
			reportTable.setRowSelectionInterval(0, 0);
		calculateTotal();
	}
	public void populateReportTableByMenuObject(String date) throws SQLException {
		reportTableModel.setColumnIdentifiers(new String[] {"ID","Menu Object","Quantity"});
		reportTableModel.setRowCount(0);
		String sql = "SELECT mo._id,mo.menu_object_name,sum(quantity) FROM Orders o, OrderDetails od, MenuObject mo "
				+ "WHERE o._id=od.order_id AND od.menu_object=mo._id "
				+ "AND o.time_stamp BETWEEN \""+date+" 00:00:00\" AND \""+date+" 23:59:59\" "
						+ "GROUP BY mo.menu_object_name ORDER BY mo.menu_object_name";
		System.out.println(sql);
		ResultSet rs = DatabaseThread.query(sql);
		//_id | user | time_stamp | total | _id | order_id | menu_object | quantity | note
		System.out.println("attempting to add row");
		while(rs.next()) {
			System.out.println("adding row");
			reportTableModel.addRow(new String[] {rs.getInt(1)+"",rs.getString("menu_object_name"),rs.getString("sum(quantity)")});
		}
		if(reportTableModel.getRowCount()>0)
			reportTable.setRowSelectionInterval(0, 0);
		totalTxt.setText("");
	}
	public void populateReportTableByUser(String date) throws SQLException {
		reportTableModel.setColumnIdentifiers(new String[] {"Username","Total"});
		reportTableModel.setRowCount(0);
		String sql = "SELECT u.username,sum(total) FROM Orders o, User u "
				+ "WHERE o.user=u._id "
				+ "AND o.time_stamp BETWEEN \""+date+" 00:00:00\" AND \""+date+" 23:59:59\" "
						+ "GROUP BY u.username ORDER BY u.username";
		System.out.println(sql);
		ResultSet rs = DatabaseThread.query(sql);
		//_id | user | time_stamp | total | _id | order_id | menu_object | quantity | note
		System.out.println("attempting to add row");
		while(rs.next()) {
			System.out.println("adding row");
			reportTableModel.addRow(new String[] {rs.getString("username"),rs.getString("sum(total)")});
		}
		if(reportTableModel.getRowCount()>0)
			reportTable.setRowSelectionInterval(0, 0);
		totalTxt.setText("");
	}
	private void calculateTotal() {
		double total=0.0;
		for(int i=0;i<reportTableModel.getRowCount();i++) {
			total+=Double.parseDouble((String) reportTableModel.getValueAt(i, 3));
		}
		totalTxt.setText(total+"");
	}
	public static void refresh() {
		int i = reportMode.getSelectedIndex();
		reportMode.setSelectedIndex((i+2)%reportMode.getItemCount());
		reportMode.setSelectedIndex(i);
	}
}
