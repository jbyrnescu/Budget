package finance.reports.model;

public class BudgetItem {
	
	double amount = 0.0;
	String category = "";
	
	public BudgetItem() {
	
	}
	
	public void populateItemFromString(String line) {
		String[] columns = line.split(",");
		category = columns[0];
		amount = Double.parseDouble(columns[1]);
	}

}
