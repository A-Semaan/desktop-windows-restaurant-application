package shared;

import java.io.Serializable;

public class Manager extends User implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 3553557414416238816L;
	public static final int flag=User.MANAGER;

    public Manager(String username,String password){
        super(username,password,flag);
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

}
