package Packets;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import DataClasses.MenuObject;

public class ReplyPacket extends Packet implements Serializable{



    /**
     *
     */
    private static final long serialVersionUID = -1442133580097107924L;
    private int request;
    private Object data;
    private String[] dataArray;
    private HashMap<String, ArrayList<MenuObject>> menu;

    public ReplyPacket(String source, InetAddress destination, int sourceT, int destinationT, int request){
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
        this.data=data;
    }

    public String[] getDataArray() {
        return dataArray;
    }

    public void setDataArray(String[] data) {
        this.dataArray=data;
    }

    public HashMap<String, ArrayList<MenuObject>> getMenu(){
        return menu;
    }

    public void setMenu(HashMap<String, ArrayList<MenuObject>> menu) {
        this.menu=menu;
    }

}
