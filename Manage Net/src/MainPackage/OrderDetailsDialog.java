package MainPackage;

import java.awt.EventQueue;
import java.awt.Font;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import Asynchronous.DatabaseThread;

public class OrderDetailsDialog extends JDialog {

	private JTable orderDetailTable;
	private DefaultTableModel orderDetailTableModel;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					OrderDetailsDialog dialog = new OrderDetailsDialog(0);
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the dialog.
	 */
	public OrderDetailsDialog(int id) {
		getContentPane().setFont(new Font("Arial", Font.PLAIN, 18));
		setSize(530, 500);
		getContentPane().setLayout(null);
		setLocationRelativeTo(null);
		
		orderDetailTableModel = new DefaultTableModel(new String[] {"ID","Menu Object","Quantity","Price"},0);
		orderDetailTable = new JTable(orderDetailTableModel) {
			public boolean isCellEditable(int row, int column) {                
                return false;               
			};
		};
		orderDetailTable.setFont(new Font("Arial", Font.PLAIN, 17));
		orderDetailTable.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 17));
		orderDetailTable.getColumnModel().getColumn(0).setPreferredWidth(10);
		JScrollPane tableSroll = new JScrollPane(orderDetailTable);
		tableSroll.setBounds(30,30,this.getWidth()-60,this.getHeight()-85);
		getContentPane().add(tableSroll);

		try {
			populateTable(id);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	private void populateTable(int id) throws SQLException {
		orderDetailTableModel.setRowCount(0);
		String sql = "SELECT * FROM OrderDetails o, MenuObject mo WHERE o.menu_object=mo._id AND o.order_id="+id;
		//_id | order_id | menu_object | qunatity | note | _id |categ | menu_object_name | description | price
		
		ResultSet rs = DatabaseThread.query(sql);
		while(rs.next()) {
			orderDetailTableModel.addRow(new String[] {rs.getInt(6)+"",rs.getString("menu_object_name"),rs.getString(4),rs.getString("price")});
		}
	}

}
