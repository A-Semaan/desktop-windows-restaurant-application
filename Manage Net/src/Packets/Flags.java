package Packets;

public interface Flags {
    public static final int REQUEST_ORDERS=1;
    public static final int REQUEST_REPORT_EXPORT_RIGHTS=2;
    public static final int REPORT_EXPORT_RIGHTS_GRANTED=6;
    public static final int REPORT_EXPORT_RIGHTS_DENIED=9;

    public static final int REQUEST_TABLE_NUMBER=3;
    public static final int REPLY_TABLE_NUMBER=4;

    public static final int REQUEST_MENU=5;
    public static final int REPLY_MENU=7;

    public static final int REQUEST_CONNECTED_WAITERS=10;
    public static final int REPLY_CONNECTED_WAITERS=11;

    public static final int CONNECTION_REQUEST=12;
    public static final int CONNECTION_APPROVED=13;
    public static final int CONNECTION_DENIED=16;
    public static final int DISCONNECTION_REQUEST=14;
    public static final int DISCONNECTION_APPROVED=15;
    public static final int DISCONNECTION_DENIED=17;
    

    public static final int ORDER_EDITING_REQUEST=22;
    public static final int ORDER_EDITING_APPROVED=23;
    public static final int ORDER_EDITING_DENIED=24;
}
