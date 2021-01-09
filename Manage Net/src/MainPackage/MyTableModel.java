package MainPackage;

import javax.swing.table.DefaultTableModel;


public class MyTableModel extends DefaultTableModel {
	
	public MyTableModel(String[] s,int i)
	{
		super(s,i);
	}
	
	@Override
	public boolean isCellEditable(int row, int col) {
		if(col==0)
			return false;
		return true;
	}
	
}