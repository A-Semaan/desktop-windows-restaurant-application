package MainPackage;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

import javax.swing.DefaultCellEditor;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableColumn;

import org.sqlite.SQLiteException;

import Asynchronous.DatabaseThread;

public class StockPanel extends JPanel {
	
	private static JTable stockTable;
	private static StockTableModel stocktableModel;
	
	private JPopupMenu mainMenu;
	private JPopupMenu tableMenu;
	
	private JMenuItem addStockObject;
	private JMenuItem deleteStockObject;
	private JMenuItem showStockObjectHistory;
	
	
	private static String oldValueofCell="";
	
	
	public StockPanel(int width,int height) {
		super();
		this.setLayout(null);
		this.setSize(width, height);
		this.setBackground(Color.WHITE);
		
		stocktableModel = new StockTableModel(new String[] {"ID","Name","Description","Current Quantity","Date Created","Last Date Modified","Insert Quantity"}, 0);
		stockTable = new JTable(stocktableModel);
		stockTable.getColumnModel().getColumn(0).setPreferredWidth(10);
		stockTable.getColumnModel().getColumn(3).setPreferredWidth(80);
		stockTable.getColumnModel().getColumn(4).setPreferredWidth(80);
		stockTable.getColumnModel().getColumn(5).setPreferredWidth(100);
		JScrollPane tableJScrollPane = new JScrollPane(stockTable);
		tableJScrollPane.setBounds(30,30,this.getWidth()-60,this.getHeight()-80);
		add(tableJScrollPane);
		
		mainMenu = new JPopupMenu();
		tableMenu = new JPopupMenu();
		
		addStockObject = new JMenuItem("Add Stock Item");
		deleteStockObject = new JMenuItem("Delete Stock Item");
		showStockObjectHistory = new JMenuItem("Show history");
		
		mainMenu.add(addStockObject);
		
		tableMenu.add(deleteStockObject);
		tableMenu.add(showStockObjectHistory);
		
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				if(SwingUtilities.isRightMouseButton(e)) {
					mainMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});
		
		
		TableColumn tb=stockTable.getColumnModel().getColumn(6);
		
		tb.setCellEditor(new DefaultCellEditor(new JTextField()));
		
		tb.getCellEditor().addCellEditorListener(new CellEditorListener() {
			
			@Override
			public void editingStopped(ChangeEvent e) {
				String newValue = stocktableModel.getValueAt(stockTable.getSelectedRow(), stockTable.getSelectedColumn()).toString();
				
				if(newValue.length()<1||Integer.parseInt(newValue)<0)
					stocktableModel.setValueAt(oldValueofCell,stockTable.getSelectedRow(), stockTable.getSelectedColumn());
				else {
					int id = Integer.parseInt(stocktableModel.getValueAt(stockTable.getSelectedRow(), 0).toString()); 
					try {
						// do major job
						String sql="UPDATE StockObject SET quantity="+newValue+""
								+ ",date_modified=\""+DatabaseThread.sqlDateFormat.format(new Date())+"\" WHERE _id="+id;
						System.out.println(sql);
						DatabaseThread.update(sql);
						sql="INSERT INTO StockRecord VALUES("+id+",\""+newValue+"\",\""+DatabaseThread.sqlDateFormat.format(new Date())+"\")";
						System.out.println(sql);
						DatabaseThread.update(sql);
						populateStockTable();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
			
			@Override
			public void editingCanceled(ChangeEvent e) {
				stocktableModel.setValueAt(oldValueofCell,stockTable.getSelectedRow(), stockTable.getSelectedColumn());
				
			}
		});
		
		stockTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				int i=stockTable.rowAtPoint(e.getPoint());
				if(i<0||i>stocktableModel.getRowCount())
					return;
				
				stockTable.setRowSelectionInterval(i, i);
				if(SwingUtilities.isRightMouseButton(e)) {
					tableMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});
		
		try {
			populateStockTable();
			if(stocktableModel.getRowCount()>0)
				stockTable.setRowSelectionInterval(0, 0);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		addStockObject.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				AddStockObjectDialog asod = new AddStockObjectDialog();
				asod.setVisible(true);
			}
		});
		deleteStockObject.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int id = Integer.parseInt(stocktableModel.getValueAt(stockTable.getSelectedRow(), 0).toString());
				if(JOptionPane.showConfirmDialog(StockPanel.this, "Are You sure you want to delete this Object?\n"
						+ "This will delete this object and all of its records", "Delete Object.", 
						JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE)==JOptionPane.YES_OPTION) {
					String sql = "DELETE FROM StockObject WHERE _id="+id;
					System.out.println(sql);
					try {
						DatabaseThread.update(sql);
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					sql = "DELETE FROM StockRecord WHERE object_id="+id;
					System.out.println(sql);
					try {
						DatabaseThread.update(sql);
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					try {
						populateStockTable();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		showStockObjectHistory.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int id = Integer.parseInt(stocktableModel.getValueAt(stockTable.getSelectedRow(), 0).toString());
				StockObjectRecord sor = new StockObjectRecord(id);
				sor.setVisible(true);
				
			}
		});
	}
	
	public static void populateStockTable() throws SQLException{
		stocktableModel.setRowCount(0);
		String sql = "SELECT * FROM StockObject";
		ResultSet rs = DatabaseThread.query(sql);
		while(rs.next()) {
			stocktableModel.addRow(new String[] {rs.getInt(1)+"",rs.getString(2),rs.getString(3),
					rs.getString(4),rs.getString(5),rs.getString(6),""});
		}
	}
}
