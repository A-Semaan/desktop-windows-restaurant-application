package DataClasses;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

public class License implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7113453148199188032L;
	
	public static final long PERMANENT=-365;
	
	private String license;
	private Date date;
	private long validity;
	
	public License(String input,Date d,long val) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		
		byte[] bytes = input.getBytes("UTF-8");
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] digest = md.digest(bytes);
		license = digest.toString();
		date=d;
		validity=val;
	}
	
	public String getLicense() {
		return license;
	}

	public Date getDate() {
		return date;
	}

	public long getValidity() {
		return validity;
	}
	
	

}
