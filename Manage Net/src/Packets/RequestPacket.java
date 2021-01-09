package Packets;

import java.io.Serializable;
import java.net.InetAddress;

public class RequestPacket extends Packet implements Serializable{


    /**
	 * 
	 */
	private static final long serialVersionUID = -5258795643739191629L;
	private int request;
	private Object data;
	
    public RequestPacket(String source, InetAddress destination, int sourceT, int destinationT, int request){
        sourceIP=source;
        destinationIP=destination;
        sourceUserType=sourceT;
        destinationUserType=destinationT;
        this.request=request;
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

    public int getRequest() {
        return request;
    }

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
    
}
