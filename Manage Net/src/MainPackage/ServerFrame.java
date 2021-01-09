package MainPackage;
import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import Asynchronous.ConnectivityServer;
import Asynchronous.DatabaseThread;
import Asynchronous.PacketServer;
import Packets.RequestPacket;
import shared.User;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Font;
import java.awt.Insets;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.JPasswordField;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JTabbedPane;

public class ServerFrame extends JFrame {

	private JPanel contentPane;
	private JTextField usernameTextField;
	private JPasswordField passwordField;
	private static JPanel timeMainPanel;
	private static JPanel loginPanel;
	
	private static ServerFrame thiss;
	
	private static boolean isLoggedIn=false;
	

	/**
	 * Launch the application.
	 */
	

	/**
	 * Create the frame.
	 */
	public ServerFrame() {
		thiss=this;
		System.setProperty("java.net.preferIPv4Stack" , "true");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 700, 500);
		this.setResizable(false);
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new GridBagLayout());
		setContentPane(contentPane);
		
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				if(JOptionPane.showConfirmDialog(ServerFrame.this, "Are you sure you want to quit?", "Shut down server?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE)==JOptionPane.YES_OPTION) {
					System.exit(0);
				}
				
			}
		});
		
		timeMainPanel = new JPanel();
		timeMainPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				timeMainPanel.setVisible(false);
				loginPanel.setVisible(true);
				usernameTextField.grabFocus();
			}
		});
		timeMainPanel.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				timeMainPanel.setVisible(false);
				loginPanel.setVisible(true);
			}
		});
		timeMainPanel.addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if(timeMainPanel.isVisible())
					ServerFrame.this.setVisible(true);
				
			}
		});
		timeMainPanel.setBackground(Color.WHITE);
		timeMainPanel.setLayout(new GridBagLayout());
		contentPane.add(timeMainPanel);
		
		
		JLabel timeMainLabel = new JLabel("");
		timeMainLabel.setFont(new Font("Arial", Font.BOLD, 72));
		timeMainLabel.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				timeMainPanel.setVisible(false);
				loginPanel.setVisible(true);
			}
		});
		timeMainPanel.add(timeMainLabel);
		
		
		loginPanel = new JPanel();
		loginPanel.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_ESCAPE) {
					passwordField.setText("");
					usernameTextField.setText("");
					timeMainPanel.setVisible(true);
					loginPanel.setVisible(false);
					timeMainPanel.grabFocus();
				}
			}
		});
		loginPanel.setBackground(Color.WHITE);
		contentPane.add(loginPanel);
		GridBagLayout gbl_loginPanel = new GridBagLayout();
		gbl_loginPanel.columnWidths = new int[]{0, 0};
		gbl_loginPanel.rowHeights = new int[]{0, 0};
		gbl_loginPanel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_loginPanel.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		loginPanel.setLayout(gbl_loginPanel);
		loginPanel.setVisible(false);
		
		JPanel loginForm = new JPanel();
		loginForm.setBackground(Color.WHITE);
		GridBagConstraints gbc_loginForm = new GridBagConstraints();
		gbc_loginForm.gridx = 0;
		gbc_loginForm.gridy = 0;
		loginPanel.add(loginForm, gbc_loginForm);
		GridBagLayout gbl_loginForm = new GridBagLayout();
		gbl_loginForm.columnWidths = new int[]{0, 0};
		gbl_loginForm.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0};
		gbl_loginForm.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_loginForm.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		loginForm.setLayout(gbl_loginForm);
		
		JLabel lblPleaseLogin = new JLabel("Please Login");
		lblPleaseLogin.setFont(new Font("Arial", Font.BOLD, 35));
		GridBagConstraints gbc_lblPleaseLogin = new GridBagConstraints();
		gbc_lblPleaseLogin.insets = new Insets(0, 0, 5, 0);
		gbc_lblPleaseLogin.gridx = 0;
		gbc_lblPleaseLogin.gridy = 0;
		loginForm.add(lblPleaseLogin, gbc_lblPleaseLogin);
		
		JLabel lblUsername = new JLabel("Username");
		lblUsername.setFont(new Font("Arial", Font.PLAIN, 25));
		GridBagConstraints gbc_lblUsername = new GridBagConstraints();
		gbc_lblUsername.insets = new Insets(0, 0, 5, 0);
		gbc_lblUsername.gridx = 0;
		gbc_lblUsername.gridy = 1;
		loginForm.add(lblUsername, gbc_lblUsername);
		
		usernameTextField = new JTextField();
		
		usernameTextField.setFont(new Font("Arial", Font.PLAIN, 25));
		GridBagConstraints gbc_usernameTextField = new GridBagConstraints();
		gbc_usernameTextField.insets = new Insets(0, 0, 5, 0);
		gbc_usernameTextField.fill = GridBagConstraints.HORIZONTAL;
		gbc_usernameTextField.gridx = 0;
		gbc_usernameTextField.gridy = 2;
		loginForm.add(usernameTextField, gbc_usernameTextField);
		usernameTextField.setColumns(10);
		
		JLabel lblPassword = new JLabel("Password");
		lblPassword.setFont(new Font("Arial", Font.PLAIN, 25));
		GridBagConstraints gbc_lblPassword = new GridBagConstraints();
		gbc_lblPassword.insets = new Insets(0, 0, 5, 0);
		gbc_lblPassword.gridx = 0;
		gbc_lblPassword.gridy = 3;
		loginForm.add(lblPassword, gbc_lblPassword);
		
		passwordField = new JPasswordField();
		
		passwordField.setFont(new Font("Arial", Font.PLAIN, 25));
		GridBagConstraints gbc_passwordField = new GridBagConstraints();
		gbc_passwordField.insets = new Insets(0, 0, 5, 0);
		gbc_passwordField.fill = GridBagConstraints.HORIZONTAL;
		gbc_passwordField.gridx = 0;
		gbc_passwordField.gridy = 4;
		loginForm.add(passwordField, gbc_passwordField);
		
		JLabel errorLoginLabel = new JLabel("");
		errorLoginLabel.setForeground(Color.RED);
		errorLoginLabel.setFont(new Font("Arial", Font.PLAIN, 20));
		GridBagConstraints gbc_errorLoginLabel = new GridBagConstraints();
		gbc_errorLoginLabel.gridx = 0;
		gbc_errorLoginLabel.gridy = 5;
		loginForm.add(errorLoginLabel, gbc_errorLoginLabel);
		setLocationRelativeTo(null);

	
		
		usernameTextField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if(((int)e.getKeyChar())==KeyEvent.VK_ESCAPE) {
					passwordField.setText("");
					usernameTextField.setText("");
					timeMainPanel.setVisible(true);
					loginPanel.setVisible(false);
				}
			}
			@Override
			public void keyPressed(KeyEvent e) {
				if(((int)e.getKeyChar())==KeyEvent.VK_ENTER) {
					if(checkFields())
						attemptLogin(usernameTextField.getText().toString(),new String(passwordField.getPassword()));
					else {
						errorLoginLabel.setText("Some of the fields may be empty.");
					}
				}
				
			}
			private boolean checkFields() {
				return !usernameTextField.getText().toString().equalsIgnoreCase("")&&passwordField.getPassword().length>1;
			}
			private void attemptLogin(String username,String password) {
				try {
					ResultSet rs = DatabaseThread.query("SELECT * FROM user WHERE username=\""+username+"\" AND password=\""+password+"\"");
					if(!rs.next()) {
						errorLoginLabel.setText("Username or password incorrect");
					}else {
						if(!rs.next()) {
							passwordField.setText("");
							usernameTextField.setText("");
							errorLoginLabel.setText("");
							
							login();
						}
						else {
							errorLoginLabel.setText("Username or password incorrect");
						}
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
		passwordField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if(e.getKeyCode()==KeyEvent.VK_ESCAPE) {
					passwordField.setText("");
					usernameTextField.setText("");
					timeMainPanel.setVisible(true);
					loginPanel.setVisible(false);
				}
			}
			public void keyPressed(KeyEvent e) {
				if(((int)e.getKeyChar())==KeyEvent.VK_ENTER) {
					if(checkFields())
						attemptLogin(usernameTextField.getText().toString(),new String(passwordField.getPassword()));
					else {
						errorLoginLabel.setText("Some of the fields may be empty.");
					}
				}
				
			}
			private boolean checkFields() {
				return !usernameTextField.getText().toString().equalsIgnoreCase("")&&passwordField.getPassword().length>1;
			}
			private void attemptLogin(String username,String password) {
				try {
					ResultSet rs = DatabaseThread.query("SELECT * FROM user WHERE username=\""+username+"\" AND password=\""+password+"\"");
					if(!rs.next()) {
						errorLoginLabel.setText("Username or password incorrect");
					}
					else if(rs.getInt(4)!=0) {
						errorLoginLabel.setText("Unauthorized user");
					}
			
					else if(!rs.next()) {
						errorLoginLabel.setText("");
						login();
					}
					else {
						errorLoginLabel.setText("Username or password incorrect");
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
		setupClock(timeMainLabel);
		new DatabaseThread().start();
		new ConnectivityServer().start();
		new PacketServer().start();
		
	}
	
	public static boolean isLoggedIn() {
		return isLoggedIn;
	}
	
	
	private void setupClock(JLabel label) {
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
		SimpleDateFormat sdf2 = new SimpleDateFormat("hh mm");
		Timer t = new Timer(1000, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Date d = new Date();
				if(label.getText().toString().contains(":"))
					label.setText(sdf2.format(d.getTime()));
				else {
					label.setText(sdf.format(d.getTime()));
				}
			}
		});
		t.setRepeats(true);
		t.setDelay(1000);
		t.setCoalesce(true);
		t.start();
		
	}
	private void login() {
		isLoggedIn=true;
		timeMainPanel.setVisible(false);
		loginPanel.setVisible(false);
		ManagerFrame mf = new ManagerFrame();
		passwordField.setText("");
		usernameTextField.setText("");
		
		
		mf.setVisible(true);
		this.setVisible(false);
		
	}
	public static void logout() {
		isLoggedIn=false;
		loginPanel.setVisible(false);
		
		timeMainPanel.setVisible(true);
		thiss.setVisible(true);
	}
}
