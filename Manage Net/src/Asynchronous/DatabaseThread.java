package Asynchronous;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;

import org.apache.commons.io.FileUtils;

public final class DatabaseThread extends Thread {
	
	public static final SimpleDateFormat sqlDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat sqlDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	
	private static Connection connection;
	
	public DatabaseThread() {
		System.setProperty("java.net.preferIPv4Stack" , "true");
		try {
			connection=getConnection();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void run() {
		
	}
	
	public static ResultSet query(String sql) throws SQLException {
		return connection.createStatement().executeQuery(sql);
	}
	public static void update(String sql) throws SQLException {
		connection.createStatement().executeUpdate(sql);
	}
	
	private static Connection getConnection() throws Exception {
	    String driver = "org.sqlite.JDBC";
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
	    	File ff = new File(".\\GreenTown.db");
	    	File dest = new File(f.getAbsolutePath());
	    	FileUtils.copyFileToDirectory(ff, dest);
	    }
	    else if(!new File(myDocuments+"\\GreenTown Database\\GreenTown.db").exists()) {
	    	File ff = new File(".\\GreenTown.db");
	    	File dest = new File(f.getAbsolutePath());
	    	FileUtils.copyFileToDirectory(ff, dest);
	    }
	    System.out.println(myDocuments);
	    String url = "jdbc:sqlite:"+f.getAbsolutePath()+"\\GreenTown.db";
	    String username = "";
	    String password = "";
	    DriverManager.registerDriver(
	    		   (Driver) Class.forName( driver ).newInstance() );
	    return DriverManager.getConnection(url, username, password);
	 }
}
