package DataClasses;

import java.io.Serializable;

public class Category implements Serializable,Comparable<Category>{

    /**
	 * 
	 */
	private static final long serialVersionUID = -7809568009800160196L;

	private static int next=0;

    private int id;
    private String name;

    public Category(int id,String name){
        this.id=id;
        this.name=name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static void setNext(int next) {
        Category.next = next;
    }

    public int compareTo(Category categ){
        return this.name.compareTo(categ.getName());
    }
}
