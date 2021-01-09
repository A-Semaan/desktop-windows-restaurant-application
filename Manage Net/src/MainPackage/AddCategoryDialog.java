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
import java.sql.SQLException;

import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.Color;

public class AddCategoryDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField categoryNameTxt;
	private JLabel errorlbl;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			AddCategoryDialog dialog = new AddCategoryDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public AddCategoryDialog() {
		setSize(290, 130);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		setLocationRelativeTo(null);
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		JLabel lblCategoryName = new JLabel("Category Name");
		lblCategoryName.setFont(new Font("Arial", Font.PLAIN, 18));
		lblCategoryName.setBounds(10, 10, 127, 18);
		contentPanel.add(lblCategoryName);
		
		categoryNameTxt = new JTextField();
		categoryNameTxt.setFont(new Font("Arial", Font.PLAIN, 15));
		categoryNameTxt.setBounds(140, 10, 126, 19);
		contentPanel.add(categoryNameTxt);
		categoryNameTxt.setColumns(10);
		{
			errorlbl = new JLabel("");
			errorlbl.setForeground(Color.RED);
			errorlbl.setHorizontalAlignment(SwingConstants.CENTER);
			errorlbl.setFont(new Font("Arial", Font.PLAIN, 16));
			errorlbl.setBounds(10, 36, 256, 20);
			contentPanel.add(errorlbl);
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
							String categ = categoryNameTxt.getText().toString();
							try {
								DatabaseThread.update("INSERT INTO category (category_name) VALUES(\""+categ+"\")");
								MenuPanel.populateCategoryTable();
							} catch (SQLException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							
							AddCategoryDialog.this.dispose();
						}
						else {
							errorlbl.setText("Category field is empty.");
						}
						
					}
					private boolean checkfields() {
						return categoryNameTxt.getText().toString().length()>2;
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
						AddCategoryDialog.this.dispose();
					}
				});
			}
		}
	}
}
