package MainPackage;

import javax.swing.table.DefaultTableModel;


public class StockTableModel extends DefaultTableModel {
	
	public StockTableModel(String[] s,int i)
	{
		super(s,i);
	}
	
	@Override
	public boolean isCellEditable(int row, int col) {
		if(col<6)
			return false;
		return true;
	}
	
}