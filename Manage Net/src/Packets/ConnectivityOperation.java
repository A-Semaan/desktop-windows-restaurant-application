package Packets;

import java.io.Serializable;

import shared.User;
import shared.UserInfo;

public class ConnectivityOperation implements Flags,Serializable{



    /**
	 * 
	 */
	private static final long serialVersionUID = 7318261584191873301L;
	private int operation;
    private UserInfo user;

    public ConnectivityOperation(int op, UserInfo us){
        operation = op;
        user=us;
    }

    public int getOperation() {
        return operation;
    }

    public UserInfo getUserInfo() {
        return user;
    }
}
