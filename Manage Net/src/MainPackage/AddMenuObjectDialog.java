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

import javax.swing.SwingConstants;
import java.awt.Color;
import javax.swing.JTextField;
import javax.swing.JComboBox;

public class AddMenuObjectDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JComboBox categoryCmb;
	private JTextField priceTxt;
	private JTextField descTxt;
	private JTextField nameTxt;
	private JLabel errorlbl;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			AddMenuObjectDialog dialog = new AddMenuObjectDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public AddMenuObjectDialog() {
		setSize(239, 258);
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
			JLabel lblNewLabel_2 = new JLabel("Price");
			lblNewLabel_2.setFont(new Font("Arial", Font.PLAIN, 18));
			lblNewLabel_2.setBounds(10, 81, 95, 24);
			contentPanel.add(lblNewLabel_2);
		}
		{
			JLabel lblNewLabel_3 = new JLabel("Category");
			lblNewLabel_3.setFont(new Font("Arial", Font.PLAIN, 18));
			lblNewLabel_3.setBounds(10, 116, 95, 24);
			contentPanel.add(lblNewLabel_3);
		}
		{
			errorlbl = new JLabel("");
			errorlbl.setForeground(Color.RED);
			errorlbl.setHorizontalAlignment(SwingConstants.CENTER);
			errorlbl.setFont(new Font("Arial", Font.PLAIN, 16));
			errorlbl.setBounds(10, 151, 205, 24);
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
			priceTxt = new JTextField();
			priceTxt.setFont(new Font("Arial", Font.PLAIN, 18));
			priceTxt.setBounds(115, 81, 95, 24);
			contentPanel.add(priceTxt);
			priceTxt.setColumns(10);
		}
		
		categoryCmb = new JComboBox();
		categoryCmb.setFont(new Font("Arial", Font.PLAIN, 18));
		categoryCmb.setBounds(115, 116, 95, 24);
		populateComboBox();
		contentPanel.add(categoryCmb);
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
							Double price = Double.parseDouble(priceTxt.getText().toString());
							String categ = categoryCmb.getSelectedItem().toString();
							try {
								String sql = "INSERT INTO MenuObject (categ,menu_object_name"
										+ ",description,price) VALUES((SELECT _id FROM category WHERE"
										+ " category_name=\""+categ+"\"),\""+name+"\",\""+desc+"\","+price+")";
								System.out.println(sql);
								DatabaseThread.update(sql);
								
								MenuPanel.populateMenuTable();
							} catch (SQLException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							
							AddMenuObjectDialog.this.dispose();
						}
						else {
							errorlbl.setText("Some fields are empty.");
						}
						
					}
					private boolean checkfields() {
						boolean b=false;
						b = nameTxt.getText().toString().length()>2&&
								descTxt.getText().toString().length()>2&&
								categoryCmb.getSelectedIndex()>0;
						try {
							Double.parseDouble(priceTxt.getText().toString());
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
						AddMenuObjectDialog.this.dispose();
					}
				});
			}
		}
	}
	private void populateComboBox() {
		try {
			categoryCmb.addItem("Select");
			ResultSet categoryResult= DatabaseThread.query("SELECT * FROM Category");
			while(categoryResult.next()) {
				System.out.println(categoryResult.getString(2));
				categoryCmb.addItem(categoryResult.getString(2));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
