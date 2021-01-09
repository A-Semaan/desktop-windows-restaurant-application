package MainPackage;

import java.awt.EventQueue;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import Asynchronous.DatabaseThread;

import java.awt.Font;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StockObjectRecord extends JDialog {
	
	DefaultTableModel stockRecordTableModel;
	JTable stockRecordTable;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					StockObjectRecord dialog = new StockObjectRecord(0);
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
	public StockObjectRecord(int id) {
		getContentPane().setFont(new Font("Arial", Font.PLAIN, 18));
		setSize(530, 500);
		getContentPane().setLayout(null);
		setLocationRelativeTo(null);
		
		stockRecordTableModel = new DefaultTableModel(new String[] {"ID","Quantity","Date"},0);
		stockRecordTable = new JTable(stockRecordTableModel) {
			public boolean isCellEditable(int row, int column) {                
                return false;               
			};
		};
		stockRecordTable.setFont(new Font("Arial", Font.PLAIN, 17));
		stockRecordTable.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 17));
				
		JScrollPane tableSroll = new JScrollPane(stockRecordTable);
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
		stockRecordTableModel.setRowCount(0);
		String sql = "SELECT * FROM StockRecord WHERE object_id="+id;
		ResultSet rs = DatabaseThread.query(sql);
		while(rs.next()) {
			stockRecordTableModel.addRow(new String[] {rs.getInt(1)+"",rs.getString(2),rs.getString(3)});
		}
	}

}
