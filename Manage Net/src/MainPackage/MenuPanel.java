package MainPackage;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.*;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;

import Asynchronous.DatabaseThread;
import Asynchronous.PacketServer;
import DataClasses.Category;
import DataClasses.MenuObject;

public class MenuPanel extends JPanel {
	
	private JPopupMenu mainMenu;
	
	private JPopupMenu categoryMain;
	private JPopupMenu menuMain;
	
	private JMenuItem addCategory;
	private JMenuItem addMenuObject;
	
	private JMenuItem deleteCategory;
	private JMenuItem deleteMenuObject;
	private JMenuItem addObjectToStock;
	
	private static JComboBox<String> categories;
	
	static MyTableModel categoryTableModel;
	JTable categoryTable;
	
	static MyTableModel menuTableModel;
	static JTable menuTable;
	
	JLabel nboftableslbl;
	JTextField nbrofTablesTxt;
	
	private static String oldValueofCellCategory;
	private static String oldValueofCellMenu;
	
	public MenuPanel(int width,int height) {
		super();
		this.setLayout(null);
		this.setSize(width, height);
		this.setBackground(Color.WHITE);
		
		mainMenu = new JPopupMenu();
		
		categoryMain = new JPopupMenu();
		menuMain = new JPopupMenu();
		
		addCategory = new JMenuItem("Add Category");
		addMenuObject = new JMenuItem("Add Menu Object");
		
		deleteCategory = new JMenuItem("Delete");
		deleteMenuObject = new JMenuItem("Delete");
		addObjectToStock = new JMenuItem("Add as Stock Object");
		
		nboftableslbl = new JLabel("Number of tables");
		nbrofTablesTxt = new JTextField();
		
		mainMenu.add(addCategory);
		mainMenu.add(addMenuObject);
		categoryMain.add(deleteCategory);
		menuMain.add(deleteMenuObject);
		menuMain.add(addObjectToStock);
		
		categoryTableModel = new MyTableModel(new String[] {"ID","Name"}, 0);
		categoryTable = new JTable(categoryTableModel);
		
		menuTableModel = new MyTableModel(new String[] {"ID","Name","Description","Price","Category",}, 0);
		menuTable = new JTable(menuTableModel);
		
		
		
		JScrollPane categoryTableScroll  = new JScrollPane(categoryTable);
		JScrollPane menuTableScroll = new JScrollPane(menuTable);
		
		categoryTableScroll.setBounds((this.getWidth()/2)-100,20,200,100);
		menuTableScroll.setBounds((this.getWidth()/2)-250,150,500,270);
		
		this.add(categoryTableScroll);
		this.add(menuTableScroll);
		this.add(nboftableslbl).setBounds((this.getWidth()/2)-230,40,100,30);
		this.add(nbrofTablesTxt).setBounds((this.getWidth()/2)-240,80,120,20);
		
		Enumeration<TableColumn> columnsCategory = categoryTable.getColumnModel().getColumns();
		while(columnsCategory.hasMoreElements()) {
			TableColumn tb=columnsCategory.nextElement();
			
			tb.setCellEditor(new DefaultCellEditor(new JTextField()));
		}
		Enumeration<TableColumn> columns2Category = categoryTable.getColumnModel().getColumns();
		while(columns2Category.hasMoreElements()) {
			TableColumn tb=columns2Category.nextElement();
			tb.getCellEditor().addCellEditorListener(new CellEditorListener() {
				
				@Override
				public void editingStopped(ChangeEvent e) {
					String newValue = categoryTableModel.getValueAt(categoryTable.getSelectedRow(), categoryTable.getSelectedColumn()).toString();
					if(newValue.length()<2)
						categoryTableModel.setValueAt(oldValueofCellCategory,categoryTable.getSelectedRow(), categoryTable.getSelectedColumn());
					else {
						int id = Integer.parseInt(categoryTableModel.getValueAt(categoryTable.getSelectedRow(), 0).toString()); 
						try {
							String sql="UPDATE category SET category_name=\""+newValue+"\""
									+ " WHERE _id="+id;
							System.out.println(sql);
							DatabaseThread.update(sql);
							populateCategoryTable();
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
				
				@Override
				public void editingCanceled(ChangeEvent e) {
					categoryTableModel.setValueAt(oldValueofCellCategory,categoryTable.getSelectedRow(), categoryTable.getSelectedColumn());
					
				}
			});
		}
		categoryTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				int i=categoryTable.rowAtPoint(e.getPoint());
				if(i<0||i>categoryTableModel.getRowCount())
					return;
				
				categoryTable.setRowSelectionInterval(i, i);
				if(SwingUtilities.isRightMouseButton(e)) {
					categoryTable.setRowSelectionInterval(i, i);
					categoryMain.show(e.getComponent(), e.getX(), e.getY());
					return;
				}
				oldValueofCellCategory = categoryTableModel.getValueAt(categoryTable.getSelectedRow(),categoryTable.getSelectedColumn()).toString();
				
				
				System.out.println(e.getClickCount());
			}
		});
		categoryTable.getColumnModel().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				oldValueofCellCategory = categoryTableModel.getValueAt(categoryTable.getSelectedRow(),categoryTable.getSelectedColumn()).toString();
			}
		});
		
		

		int i=0;
		while(i<5) {
			TableColumn tb=menuTable.getColumnModel().getColumn(i);
			
				tb.setCellEditor(new DefaultCellEditor(new JTextField()));
			i++;
		}
		setUpComboColumn(menuTable, menuTable.getColumnModel().getColumn(4));
		
		 i=0;
		Enumeration<TableColumn> columns2 = menuTable.getColumnModel().getColumns();
		while(columns2.hasMoreElements()) {
			if(i==4) {
				i++;
				continue;
			}
			TableColumn tb=columns2.nextElement();
			
			tb.getCellEditor().addCellEditorListener(new CellEditorListener() {
				
				@Override
				public void editingStopped(ChangeEvent e) {
					if(menuTable.getSelectedColumn()==4)
						return;
					String newValue = menuTableModel.getValueAt(menuTable.getSelectedRow(), menuTable.getSelectedColumn()).toString();
					if(newValue.length()<2) 
						menuTableModel.setValueAt(oldValueofCellMenu,menuTable.getSelectedRow(), menuTable.getSelectedColumn());
					else {
						int id = Integer.parseInt(menuTableModel.getValueAt(menuTable.getSelectedRow(), 0).toString()); 
						try {
							String sql="UPDATE MenuObject SET "+getColumnNamebyIndex(menuTable.getSelectedColumn())+"=\""+newValue+"\""
									+ " WHERE _id="+id;
							System.out.println(sql);
							DatabaseThread.update(sql);
							populateMenuTable();
						} catch (SQLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
				
				@Override
				public void editingCanceled(ChangeEvent e) {
					menuTableModel.setValueAt(oldValueofCellMenu,menuTable.getSelectedRow(), menuTable.getSelectedColumn());
					
				}
			});
			i++;
		}
		menuTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				int i=menuTable.rowAtPoint(e.getPoint());
				if(i<0||i>menuTableModel.getRowCount())
					return;
				
				menuTable.setRowSelectionInterval(i, i);
				if(SwingUtilities.isRightMouseButton(e)) {
					menuTable.setRowSelectionInterval(i, i);
					menuMain.show(e.getComponent(), e.getX(), e.getY());
					return;
				}
				if(menuTable.getSelectedColumn()==4)
					return;
				oldValueofCellMenu = menuTableModel.getValueAt(menuTable.getSelectedRow(),menuTable.getSelectedColumn()).toString();
				
				
				System.out.println(e.getClickCount());
			}
		});
		menuTable.getColumnModel().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(menuTable.getSelectedColumn()==4)
					return;
				oldValueofCellMenu = menuTableModel.getValueAt(menuTable.getSelectedRow(),menuTable.getSelectedColumn()).toString();
			}
		});
		
		
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				if(SwingUtilities.isRightMouseButton(e)) {
					mainMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});
		
		addCategory.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				AddCategoryDialog acd = new AddCategoryDialog();
				acd.setVisible(true);
				
			}
		});
		
		addMenuObject.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				AddMenuObjectDialog amod = new AddMenuObjectDialog();
				amod.setVisible(true);
				
			}
		});
		deleteCategory.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String category = categoryTableModel.getValueAt(categoryTable.getSelectedRow(), 1).toString();
				if(JOptionPane.showConfirmDialog(MenuPanel.this, "Are you sure you want to remove "+category+"?", "Remove Category?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE)==JOptionPane.YES_OPTION) {
					int id = Integer.parseInt(categoryTableModel.getValueAt(categoryTable.getSelectedRow(), 0).toString());
					String sql = "DELETE FROM category WHERE _id="+id;
					
					try {
						DatabaseThread.update(sql);
						populateCategoryTable();
						if(categoryTableModel.getRowCount()>0)
							categoryTable.setRowSelectionInterval(0, 0);
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				
			}
		});
		deleteMenuObject.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String menuObject = menuTableModel.getValueAt(menuTable.getSelectedRow(), 1).toString();
				if(JOptionPane.showConfirmDialog(MenuPanel.this, "Are you sure you want to remove "+menuObject+"?", "Remove Menu Object?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE)==JOptionPane.YES_OPTION) {
					int id = Integer.parseInt(menuTableModel.getValueAt(menuTable.getSelectedRow(), 0).toString());
					String sql = "DELETE FROM MenuObject WHERE _id="+id;
					
					try {
						DatabaseThread.update(sql);
						populateMenuTable();
						if(menuTableModel.getRowCount()>0)
							menuTable.setRowSelectionInterval(0, 0);
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		
		addObjectToStock.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int row = menuTable.getSelectedRow();
				MenuObject mo = new MenuObject(Integer.parseInt(menuTableModel.getValueAt(row, 0).toString()),
						menuTableModel.getValueAt(row, 1).toString(),
						menuTableModel.getValueAt(row, 2).toString(),
						Double.parseDouble(menuTableModel.getValueAt(row, 3).toString()),
						new Category(Integer.parseInt(menuTableModel.getValueAt(row, 4).toString()),""));
				if(PacketServer.contains(PacketServer.trackables,mo)) {
					JOptionPane.showMessageDialog(MenuPanel.this, "Object already being tracked");
					return;
				}
				else {
					try {
						String inputquantity = JOptionPane.showInputDialog(MenuPanel.this, "Please inser the initial quantity of this object.", "Initiation", JOptionPane.QUESTION_MESSAGE);
						if(inputquantity==null||inputquantity.equalsIgnoreCase(""))
							return;
						int quantity = Integer.parseInt(inputquantity);
						String dateModified = DatabaseThread.sqlDateFormat.format(new Date());
						PacketServer.trackables.add(mo);
						String sql = "INSERT INTO StockObject(name,description,quantity,"
								+ "date_created,date_modified) VALUES(\""+mo.getName()+"\","
								+ "\""+mo.getDescritpion()+"\","+quantity+",\""+dateModified+"\","
								+ "\""+dateModified+"\")";
						System.out.println(sql);
						DatabaseThread.update(sql);
						sql="INSERT INTO StockRecord(object_id,quantity,date) VALUES("
							+ "(SELECT last_insert_rowid()),"+quantity+", \""+dateModified+"\")";
						System.out.println(sql);
						DatabaseThread.update(sql);
						sql="UPDATE MenuObject SET trackable=true "
								+ "WHERE menu_object_name=\""+mo.getName()+"\"";
						System.out.println(sql);
						DatabaseThread.update(sql);
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
		
		nbrofTablesTxt.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try{
					int newNum = Integer.parseInt(nbrofTablesTxt.getText().toString());
					DatabaseThread.update("UPDATE RestaurantTables SET _id="+newNum);
				}
				catch(NumberFormatException ex) {
					
					try {
						ResultSet rs=DatabaseThread.query("SELECT * FROM RestaurantTables");
						rs.next();
						nbrofTablesTxt.setText(rs.getString(1));
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}
		});
		
		
		
		try {
			ResultSet rs=DatabaseThread.query("SELECT * FROM RestaurantTables");
			rs.next();
			nbrofTablesTxt.setText(rs.getString(1));
			populateCategoryTable();
			populateMenuTable();
			if(categoryTableModel.getRowCount()>0)
				categoryTable.setRowSelectionInterval(0, 0);
			if(menuTableModel.getRowCount()>0)
				menuTable.setRowSelectionInterval(0, 0);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
	}
	private static void populateComboBox() {
		try {
			categories.removeAllItems();
			ResultSet categoryResult= DatabaseThread.query("SELECT * FROM Category");
			while(categoryResult.next()) {
				System.out.println(categoryResult.getString(2));
				categories.addItem(categoryResult.getString(2));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private  String getColumnNamebyIndex(int index) {
		switch(index) {
			case 0:return "_id";
			case 1:return "menu_object_name";
			case 2:return "description";
			case 3:return "price";
			case 4:return "categ";
			default: return "";
		}
	}
	
	public static void populateCategoryTable() throws SQLException {
		
		ResultSet rs = DatabaseThread.query("SELECT * FROM Category");
		categoryTableModel.setRowCount(0);
		while(rs.next()) {
			categoryTableModel.addRow(new String[] {rs.getInt(1)+"",rs.getString(2)});
		}
		setUpComboColumn(menuTable, menuTable.getColumnModel().getColumn(4));
		populateMenuTable();
		
		
	}
	public static void populateMenuTable() throws SQLException {
		ResultSet rs2 = DatabaseThread.query("SELECT * FROM MenuObject m,Category c WHERE m.categ=c._id ORDER BY m.menu_object_name ");
		//returned as _id | categ | menu_object_name | description | price | _id | category_name
		menuTableModel.setRowCount(0);
		while(rs2.next()) {
			menuTableModel.addRow(new String[] {rs2.getInt(1)+"",rs2.getString(3),rs2.getString(4),rs2.getString(5),rs2.getString("category_name")});
		}
	}
	
	public static void setUpComboColumn(JTable table, TableColumn comboColumn) {
		//Set up the editor for the sport cells.
		categories = new JComboBox<String>();
		populateComboBox();
		comboColumn.setCellEditor(new DefaultCellEditor(categories));
		
		categories.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange()==ItemEvent.SELECTED) {
					Object o = e.getItem();
					System.out.println("Event Fired"+o.toString());
					String id = menuTableModel.getValueAt(menuTable.getSelectedRow(), 0).toString();
					String sql="UPDATE MenuObject SET categ=(SELECT _id FROM Category WHERE category_name=\""+o.toString()+"\") WHERE _id="+id+"";
					try {
						System.out.println(sql);
						DatabaseThread.update(sql);
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				
			}
		});
		
		//Set up tool tips for the sport cells.
		DefaultTableCellRenderer renderer =
		new DefaultTableCellRenderer();
		renderer.setToolTipText("Click for combo box");
		comboColumn.setCellRenderer(renderer);
	}
	
}
