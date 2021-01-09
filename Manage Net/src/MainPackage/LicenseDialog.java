package MainPackage;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import DataClasses.License;

import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.Font;
import javax.swing.JPasswordField;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class LicenseDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JPasswordField passwordField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			
			LicenseDialog dialog = new LicenseDialog();
			if(!checkForActivation()) {
				dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				dialog.setVisible(true);
			}
			else {
				
				ServerFrame sf = new ServerFrame();
				sf.setVisible(true);
				dialog.dispose();
			}
		
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public LicenseDialog() {
		setSize(450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		setLocationRelativeTo(null);
		setUndecorated(true);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWeights = new double[]{1.0};
		contentPanel.setLayout(gbl_contentPanel);
		{
			JLabel lblNewLabel = new JLabel("Please Enter License Key");
			lblNewLabel.setFont(new Font("Arial", Font.BOLD, 18));
			GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
			gbc_lblNewLabel.insets = new Insets(0, 0, 5, 0);
			gbc_lblNewLabel.gridx = 0;
			gbc_lblNewLabel.gridy = 0;
			contentPanel.add(lblNewLabel, gbc_lblNewLabel);
		}
		{
			passwordField = new JPasswordField();
			passwordField.setFont(new Font("Arial", Font.PLAIN, 18));
			GridBagConstraints gbc_passwordField = new GridBagConstraints();
			gbc_passwordField.fill = GridBagConstraints.HORIZONTAL;
			gbc_passwordField.insets = new Insets(0, 10, 0, 10);
			gbc_passwordField.gridx = 0;
			gbc_passwordField.gridy = 1;
			passwordField.setSize(120,30);
			contentPanel.add(passwordField, gbc_passwordField);
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
						if(passwordField.getPassword().length<10) {
							passwordField.grabFocus();
							passwordField.selectAll();
							return;
						}
						String input = new String(passwordField.getPassword());
						if(input.contains("chronos")&&
								input.length()<20 &&
								input.startsWith("*#") &&
								input.endsWith("__")) {
							try {
								activate(input);
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							ServerFrame sf = new ServerFrame();
							sf.setVisible(true);
							LicenseDialog.this.dispose();
						}
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
						System.exit(0);
						
					}
				});
			}
		}
	}
	private static final void activate(String input) throws IOException {
		String myDocuments = null;

	    try {
	        Process p =  Runtime.getRuntime().exec("reg query \"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Shell Folders\" /v personal");
	        p.waitFor();

	        InputStream in = p.getInputStream();
	        byte[] b = new byte[in.available()];
	        in.read(b);
	        in.close();

	        myDocuments = new String(b);
	        myDocuments = myDocuments.split("\\s\\s+")[4];

	    } catch(Throwable t) {
	        t.printStackTrace();
	    }
	    File f= new File(myDocuments+"\\GreenTown Database");
	    if(!f.exists()) {
	    	f.mkdir();
	    }
	    File db = new File(f.getAbsolutePath()+File.separator+"GreenTown.db");
		if(!db.exists()) {
			FileInputStream inputDb = new FileInputStream("."+File.separator+"GreenTown.db");
			FileOutputStream outputDb = new FileOutputStream(db);
			IOUtils.copy(inputDb, outputDb);
		}
    	File cache = new File(f.getAbsolutePath()+"\\cache");
    	cache.mkdir();
    	
    	File ff = new File(cache.getAbsolutePath()+"\\license");
    	ff.createNewFile();
    	License l=null;
		try {
			l = new License(input,new Date(),60);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	FileOutputStream fos = new FileOutputStream(ff);
    	ObjectOutputStream oos = new ObjectOutputStream(fos);
    	oos.writeObject(l);
    	oos.flush();
    	oos.close();
    	fos.flush();
    	fos.close();
	    
	}
	
	private static final boolean checkForActivation() throws IOException {
		String myDocuments = null;

	    try {
	        Process p =  Runtime.getRuntime().exec("reg query \"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Shell Folders\" /v personal");
	        p.waitFor();

	        InputStream in = p.getInputStream();
	        byte[] b = new byte[in.available()];
	        in.read(b);
	        in.close();

	        myDocuments = new String(b);
	        myDocuments = myDocuments.split("\\s\\s+")[4];

	    } catch(Throwable t) {
	        t.printStackTrace();
	    }
	    File f= new File(myDocuments+"\\GreenTown Database");
	    if(!f.exists()) {
	    	return false;
	    }
	    File cache = new File(f.getAbsolutePath()+"\\cache");
	    File ff = new File(cache.getAbsolutePath()+"\\license");
	    if(!cache.exists()||!ff.exists())
	    	return false;
	    try {
			FileInputStream fis = new FileInputStream(ff);
			ObjectInputStream ois = new ObjectInputStream(fis);
			
			try{
				License l = (License)ois.readObject();
				long validity=l.getValidity();
				if(validity==License.PERMANENT) {
					File db = new File(f.getAbsolutePath()+File.separator+"GreenTown.db");
					System.out.println(db.getAbsolutePath());
					if(!db.exists()) {
						db.createNewFile();
						FileInputStream input = new FileInputStream("."+File.separator+"GreenTown.db");
						FileOutputStream output = new FileOutputStream(db);
						IOUtils.copy(input, output);
					}
					return true;
				}
					
				Date d = new Date();
				Date licDate = l.getDate();
				long diff = d.getTime() - licDate.getTime();
				long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
				if(validity<days||days<0) {
					return false;
				}
				return true;
				
			}
			catch(ClassNotFoundException e) {
				e.printStackTrace();
				return false;
			}
	    }
	    catch(EOFException e) {
	    	return false;
	    }
		
	}

}
