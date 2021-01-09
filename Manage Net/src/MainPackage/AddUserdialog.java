package MainPackage;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import Asynchronous.DatabaseThread;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.awt.GridBagConstraints;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JPasswordField;
import javax.swing.JComboBox;
import javax.swing.SwingConstants;

public class AddUserdialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JComboBox comboBox;
	private JTextField usernameTxt;
	private JPasswordField passwordField;
	private JLabel errorlbl;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			AddUserdialog dialog = new AddUserdialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public AddUserdialog() {
		setSize( 300, 260);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{0, 0};
		gbl_contentPanel.rowHeights = new int[]{0, 0};
		gbl_contentPanel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		{
			JPanel panel = new JPanel();
			panel.setLayout(null);
			GridBagConstraints gbc_panel = new GridBagConstraints();
			gbc_panel.fill = GridBagConstraints.BOTH;
			gbc_panel.gridx = 0;
			gbc_panel.gridy = 0;
			contentPanel.add(panel, gbc_panel);
			
			JLabel lblNewLabel = new JLabel("Username");
			lblNewLabel.setFont(new Font("Arial", Font.PLAIN, 18));
			lblNewLabel.setBackground(Color.WHITE);
			lblNewLabel.setBounds(21, 11, 99, 22);
			panel.add(lblNewLabel);
			
			usernameTxt = new JTextField();
			usernameTxt.setBounds(153, 11, 113, 22);
			panel.add(usernameTxt);
			usernameTxt.setColumns(10);
			
			JLabel lblNewLabel_1 = new JLabel("Password");
			lblNewLabel_1.setFont(new Font("Arial", Font.PLAIN, 18));
			lblNewLabel_1.setBackground(Color.WHITE);
			lblNewLabel_1.setBounds(21, 58, 99, 22);
			panel.add(lblNewLabel_1);
			
			passwordField = new JPasswordField();
			passwordField.setBounds(153, 58, 113, 22);
			panel.add(passwordField);
			
			JLabel lblNewLabel_2 = new JLabel("User Type");
			lblNewLabel_2.setBackground(Color.WHITE);
			lblNewLabel_2.setFont(new Font("Arial", Font.PLAIN, 18));
			lblNewLabel_2.setBounds(21, 101, 99, 22);
			panel.add(lblNewLabel_2);
			
			comboBox = new JComboBox(new String[] {"Waiter","Cashier","Assistant Manager","Manager"});
			comboBox.setBounds(153, 103, 113, 22);
			panel.add(comboBox);
			
			errorlbl = new JLabel("");
			errorlbl.setHorizontalAlignment(SwingConstants.CENTER);
			errorlbl.setFont(new Font("Arial", Font.BOLD, 16));
			errorlbl.setForeground(Color.RED);
			errorlbl.setBackground(Color.WHITE);
			errorlbl.setBounds(51, 134, 182, 22);
			panel.add(errorlbl);
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
							String username = usernameTxt.getText().toString();
							String password = new String(passwordField.getPassword());
							int flag = (comboBox.getSelectedIndex()*-1)+3;
							try {
								DatabaseThread.update("INSERT INTO user (username,password,flag) VALUES(\""+username+"\",\""+password+"\","+flag+")");
								UserPanel.populateTable();
							} catch (SQLException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							
							AddUserdialog.this.dispose();
						}
						else {
							errorlbl.setText("Some fields are empty.");
						}
						
					}
					private boolean checkfields() {
						return usernameTxt.getText().toString().length()>2&&new String(passwordField.getPassword()).length()>2;
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
						AddUserdialog.this.dispose();
					}
				});
			}
		}
	}
}
