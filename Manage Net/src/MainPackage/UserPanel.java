package MainPackage;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.EventObject;

import javax.swing.DefaultCellEditor;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import Asynchronous.DatabaseThread;

public class UserPanel extends JPanel {
	private static String oldValueofCell="";
	private static MyTableModel mtableModel;
	private JTable table;
	
	private JPopupMenu addMenu;
	private JPopupMenu deleteMenu;
	
	public UserPanel(int width,int height) {
		super();
		this.setLayout(null);
		this.setSize(width, height);
		this.setBackground(Color.WHITE);
		
		addMenu = new JPopupMenu();
		deleteMenu = new JPopupMenu();
		JMenuItem add = new JMenuItem("Add User");
		JMenuItem delete = new JMenuItem("Delete");
		addMenu.add(add);
		deleteMenu.add(delete);
		
		add.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				AddUserdialog aud = new AddUserdialog();
				aud.setVisible(true);
				
			}
		});
		delete.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String username = mtableModel.getValueAt(table.getSelectedRow(), 1).toString();
				if(JOptionPane.showConfirmDialog(UserPanel.this, "Are you sure you want to remove "+username+"?", "Remove User?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE)==JOptionPane.YES_OPTION) {
					int id = Integer.parseInt(mtableModel.getValueAt(table.getSelectedRow(), 0).toString());
					String sql = "DELETE FROM user WHERE _id="+id;
					
					try {
						DatabaseThread.update(sql);
						populateTable();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				
				
			}
		});
		
		mtableModel = new MyTableModel(new String[] {"ID","Username","Password","User Type"}, 0);
		table = new JTable();
		JScrollPane tableScrollpane = new JScrollPane(table);
		table.setBackground(Color.WHITE);
		tableScrollpane.setBounds(30, 60, this.getWidth()-60, this.getHeight()-100);
		table.setModel(mtableModel);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		mtableModel.setRowCount(5);
		Enumeration<TableColumn> columns = table.getColumnModel().getColumns();
		while(columns.hasMoreElements()) {
			TableColumn tb=columns.nextElement();
			tb.setCellEditor(new DefaultCellEditor(new JTextField()));
		}
			
		//table.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(new JTextField()));
		Enumeration<TableColumn> columns2 = table.getColumnModel().getColumns();
		while(columns2.hasMoreElements()) {
			TableColumn tb=columns2.nextElement();
			tb.getCellEditor().addCellEditorListener(new CellEditorListener() {
				
				@Override
				public void editingStopped(ChangeEvent e) {
					String newValue = mtableModel.getValueAt(table.getSelectedRow(), table.getSelectedColumn()).toString();
					if(newValue.length()<2)
						mtableModel.setValueAt(oldValueofCell,table.getSelectedRow(), table.getSelectedColumn());
					else {
						int id = Integer.parseInt(mtableModel.getValueAt(table.getSelectedRow(), 0).toString()); 
						try {
							String sql="UPDATE user SET "+getColumnNamebyIndex(table.getSelectedColumn())+"=\""+newValue+"\""
									+ " WHERE _id="+id;
							System.out.println(sql);
							DatabaseThread.update(sql);
							populateTable();
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
				
				@Override
				public void editingCanceled(ChangeEvent e) {
					mtableModel.setValueAt(oldValueofCell,table.getSelectedRow(), table.getSelectedColumn());
					
				}
			});
		}
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				int i=table.rowAtPoint(e.getPoint());
				if(i<0||i>mtableModel.getRowCount())
					return;
				table.setRowSelectionInterval(i, i);
				oldValueofCell = mtableModel.getValueAt(table.getSelectedRow(),table.getSelectedColumn()).toString();
				
				if(SwingUtilities.isRightMouseButton(e)) {
					table.setRowSelectionInterval(i, i);
					deleteMenu.show(e.getComponent(), e.getX(), e.getY());
				}
				System.out.println(e.getClickCount());
			}
		});
		table.getColumnModel().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				
				oldValueofCell = mtableModel.getValueAt(table.getSelectedRow(),table.getSelectedColumn()).toString();
			}
		});
		
		
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				if(SwingUtilities.isRightMouseButton(e)) {
					addMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});
		
		this.add(tableScrollpane);
		try {
			populateTable();
			if(mtableModel.getRowCount()>0)
				table.setRowSelectionInterval(0, 0);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private  String getColumnNamebyIndex(int index) {
		switch(index) {
			case 0:return "_id";
			case 1:return "username";
			case 2:return "password";
			case 3:return "flag";
			default: return "";
		}
	}
	public static void populateTable() throws SQLException {
		ResultSet rs = DatabaseThread.query("SELECT * FROM user");
		mtableModel.setRowCount(0);
		while(rs.next()) {
			mtableModel.addRow(new String[] {rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4)});
			
		}
	}
	
}
