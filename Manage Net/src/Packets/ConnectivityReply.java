package Packets;

import java.io.Serializable;

import shared.User;
import shared.UserInfo;

public class ConnectivityReply  implements Flags,Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = -268685799246337821L;
	private int operation;
    private User user;

    public ConnectivityReply(int op, User us){
        operation = op;
        user=us;
    }

    public int getOperation() {
        return operation;
    }

    public User getUserInfo() {
        return user;
    }
}
