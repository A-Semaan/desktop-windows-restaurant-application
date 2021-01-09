package DataClasses;

import java.io.Serializable;

public class MenuObject implements Serializable,Comparable<MenuObject>{

    /**
	 * 
	 */
	private static final long serialVersionUID = -8019628736067710352L;

	private static int next=0;

    private int id;
    private String name;
    private String description;
    private double price;
    private Category category;
    private boolean isTrackable=false;

    public MenuObject(int id,String name, String description, double price, Category category) {
        this.id=id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
    }

    public MenuObject(int id,String name, double price, Category category) {
        this(id,name,null,price,category);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescritpion() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public Category getCategory() {
        return category;
    }

    public static void setNext(int next) {
        MenuObject.next = next;
    }

    public int compareTo(MenuObject mo){
        return this.name.compareTo(mo.getName());
    }

	public boolean isTrackable() {
		return isTrackable;
	}

	public void setTrackable(boolean isTrackable) {
		this.isTrackable = isTrackable;
	}
    
    
}
