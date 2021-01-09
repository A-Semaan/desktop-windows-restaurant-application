package shared;

import java.io.Serializable;

public class AssistantManager extends User implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = -5156479603307110046L;
	public static final int flag=User.ASSISTANT_MANAGER;

    public AssistantManager(String username,String password){
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
