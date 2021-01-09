package shared;

import java.io.Serializable;

public abstract class User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8983564764727556859L;
	public static final int SERVER=-1;
    public static final int MANAGER=0;
    public static final int ASSISTANT_MANAGER=1;
    public static final int CASHIER=2;
    public static final int WAITER=3;




    protected String username;
    protected String password;
    protected int flag;

    public User(String u,String p,int f){
        username=u;
        password=p;
        flag=f;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    @Override
    public String toString() {
        return username;
    }

}
