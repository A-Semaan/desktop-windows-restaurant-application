package shared;

import java.io.Serializable;

public class Cashier extends User implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 479070154921702266L;
	public static final int flag=User.CASHIER;

    public Cashier(String username,String password){
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
