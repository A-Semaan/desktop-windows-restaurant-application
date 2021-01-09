package DataClasses;

import java.io.Serializable;

public class OrderItem implements Serializable,Comparable<OrderItem>{

    /**
	 * 
	 */
	private static final long serialVersionUID = 3571123553755011871L;
	private MenuObject menuObject;
    private int qtyOfMenuObject;
    private String note;

    public OrderItem(MenuObject menuObject, int qtyOfMenuObject, String note) {
        this.menuObject = menuObject;
        this.qtyOfMenuObject = qtyOfMenuObject;
        this.note = note;
    }

    public OrderItem(MenuObject menuObject, int qtyOfMenuObject) {
        this(menuObject,qtyOfMenuObject,"");
    }

    public MenuObject getMenuObject() {
        return menuObject;
    }

    public int getQtyOfMenuObject() {
        return qtyOfMenuObject;
    }

    public String getNote() {
        return note;
    }

    public void setQtyOfMenuObject(int qtyOfMenuObject) {
        this.qtyOfMenuObject = qtyOfMenuObject;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int compareTo(OrderItem oi){
        return this.menuObject.compareTo(oi.getMenuObject());
    }
}
