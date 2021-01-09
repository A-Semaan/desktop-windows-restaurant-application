package Packets;

import java.net.InetAddress;
import java.util.ArrayList;

import DataClasses.OrderItem;
import shared.User;

public class PayPacket extends Packet {

    private ArrayList<OrderItem> orderitems;
    private  double total;
    private User user;

    public PayPacket(String source, InetAddress destination, int sourceT, int destinationT,ArrayList<OrderItem> orderitems,double total,User user){
        this.sourceIP = source;
        this.destinationIP=destination;
        this.sourceUserType = sourceT;
        this.destinationUserType=destinationT;
        this.orderitems=orderitems;
        this.total=total;
        this.user=user;
    }

    public ArrayList<OrderItem> getOrderItems(){
        return orderitems;
    }
    public double getTotal(){
        return total;
    }
    public User getUser(){
        return user;
    }
}
