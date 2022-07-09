package finance;

public class Refund extends Expense {
	
	// This class will find it's matching expense automatically
	// and register it here so that this information can be used later
	
	Expense matchingExpense;
	
	public Refund() {
		transactionType = new String("R"); 
	}

}
