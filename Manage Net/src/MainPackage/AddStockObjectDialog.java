package MainPackage;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import Asynchronous.DatabaseThread;

import javax.swing.JLabel;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.swing.SwingConstants;
import java.awt.Color;
import javax.swing.JTextField;
import javax.swing.JComboBox;

public class AddStockObjectDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField quantityTxt;
	private JTextField descTxt;
	private JTextField nameTxt;
	private JLabel errorlbl;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			AddStockObjectDialog dialog = new AddStockObjectDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public AddStockObjectDialog() {
		setSize(239, 222);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		setLocationRelativeTo(null);
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			JLabel lblNewLabel = new JLabel("Name");
			lblNewLabel.setFont(new Font("Arial", Font.PLAIN, 18));
			lblNewLabel.setBounds(10, 11, 95, 24);
			contentPanel.add(lblNewLabel);
		}
		{
			JLabel lblNewLabel_1 = new JLabel("Description");
			lblNewLabel_1.setFont(new Font("Arial", Font.PLAIN, 18));
			lblNewLabel_1.setBounds(10, 46, 95, 24);
			contentPanel.add(lblNewLabel_1);
		}
		{
			JLabel lblNewLabel_2 = new JLabel("Quantity");
			lblNewLabel_2.setFont(new Font("Arial", Font.PLAIN, 18));
			lblNewLabel_2.setBounds(10, 81, 95, 24);
			contentPanel.add(lblNewLabel_2);
		}
		{
			errorlbl = new JLabel("");
			errorlbl.setForeground(Color.RED);
			errorlbl.setHorizontalAlignment(SwingConstants.CENTER);
			errorlbl.setFont(new Font("Arial", Font.PLAIN, 16));
			errorlbl.setBounds(10, 115, 205, 24);
			contentPanel.add(errorlbl);
		}
		{
			nameTxt = new JTextField();
			nameTxt.setFont(new Font("Arial", Font.PLAIN, 18));
			nameTxt.setBounds(115, 11, 95, 24);
			contentPanel.add(nameTxt);
			nameTxt.setColumns(10);
		}
		{
			descTxt = new JTextField();
			descTxt.setFont(new Font("Arial", Font.PLAIN, 18));
			descTxt.setBounds(115, 46, 95, 24);
			contentPanel.add(descTxt);
			descTxt.setColumns(10);
		}
		{
			quantityTxt = new JTextField();
			quantityTxt.setFont(new Font("Arial", Font.PLAIN, 18));
			quantityTxt.setBounds(115, 81, 95, 24);
			contentPanel.add(quantityTxt);
			quantityTxt.setColumns(10);
		}
		
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
				okButton.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						if(checkfields()) {
							errorlbl.setText("");
							String name = nameTxt.getText().toString();
							String desc = descTxt.getText().toString();
							int qty = Integer.parseInt(quantityTxt.getText().toString());
							try {
								String sql = "INSERT INTO StockObject (name,description"
										+ ",quantity,date_created,date_modified) VALUES(\""+name+"\",\""+desc+"\","+qty+","
												+"\"" +DatabaseThread.sqlDateFormat.format(new Date())+"\",\""+DatabaseThread.sqlDateFormat.format(new Date())+"\")";
								System.out.println(sql);
								DatabaseThread.update(sql);
								sql = "INSERT INTO StockRecord "
										+ "VALUES((SELECT last_insert_rowid() FROM StockObject Limit 1),"+qty+","
												+"\"" +DatabaseThread.sqlDateFormat.format(new Date())+"\")";
								System.out.println(sql);
								DatabaseThread.update(sql);
								StockPanel.populateStockTable();
							} catch (SQLException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							
							AddStockObjectDialog.this.dispose();
						}
						else {
							errorlbl.setText("Some fields are empty.");
						}
						
					}
					private boolean checkfields() {
						boolean b=true;
						b = nameTxt.getText().toString().length()>2&&
								descTxt.getText().toString().length()>2;
						try {
							if(Integer.parseInt(quantityTxt.getText().toString())<0)
								b=false;
							
						}
						catch(NumberFormatException e) {
							b=false;
						}
						return b;
					}
				});
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
				cancelButton.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						AddStockObjectDialog.this.dispose();
					}
				});
			}
		}
	}
}
