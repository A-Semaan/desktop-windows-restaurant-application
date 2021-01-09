package shared;

import java.io.Serializable;

public final class ServerAutomatedUser extends User implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7429266673382660391L;
	private static final String username="server";
	private static final String password="";
	private static final int flag = SERVER;
	
	private ServerAutomatedUser(String u, String p, int f) {
		super(u,p,f);
	}
	public ServerAutomatedUser() {
		this(username,password,flag);
	}
	public String getUsername() {
		return username;
	}
	public String getPassword() {
		return password;
	}
	public int getFlag() {
		return flag;
	}
	
	

}
