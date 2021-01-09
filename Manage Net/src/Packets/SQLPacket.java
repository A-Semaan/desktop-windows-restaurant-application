package Packets;

import java.io.Serializable;
import java.net.InetAddress;

public class SQLPacket extends Packet implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 4004684756881542189L;
	private String sql;


    public SQLPacket(String source, InetAddress destination, int sourceT, int destinationT, String sql) {
        sourceIP=source;
        destinationIP=destination;
        sourceUserType=sourceT;
        destinationUserType=destinationT;
        this.sql=sql;
    }

    public String getSql() {
        return sql;
    }

}
