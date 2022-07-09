package finance;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;

import accounts.Transaction;

public class Expense extends Transaction {
	
	public Expense() {
		transactionType = new String("E");
	}

	@Override
	public void convertToAbstractTransaction() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loadIntoDatabase(Connection connection) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void populateTransactionFromString(String line) throws ParseException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Transaction loadTransactionFromDatabase(ResultSet rs) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

}
