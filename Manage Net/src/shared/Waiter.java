package shared;

import java.io.Serializable;

public class Waiter extends User implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = -4291389854423906276L;
	public static final int flag=User.WAITER;

    public Waiter(String username,String password){
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
