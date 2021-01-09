package Packets;

import java.io.Serializable;
import java.net.InetAddress;

public class DataPacket extends Packet implements Serializable{



    /**
	 * 
	 */
	private static final long serialVersionUID = 7861457553047965906L;

	private int flag;

    private Object data;

    public DataPacket(String source, InetAddress destination, int sourceT, int destinationT, int flag,Object data) {
        sourceIP=source;
        destinationIP=destination;
        sourceUserType=sourceT;
        destinationUserType=destinationT;
        this.flag=flag;
        this.data=data;
    }

    public String getSourceIP() {
        return sourceIP;
    }

    public InetAddress getDestinationIP() {
        return destinationIP;
    }

    public int getSourceUserType() {
        return sourceUserType;
    }

    public int getDestinationUserType() {
        return destinationUserType;
    }

    public int getFlag() {
        return flag;
    }

    public Object getData() {
        return data;
    }
}
