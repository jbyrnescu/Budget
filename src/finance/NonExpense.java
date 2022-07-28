package finance;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;

import accounts.Transaction;

public class NonExpense extends Transaction {
	
	public NonExpense() {
		transactionType = new String("N");
	}

	@Override
	public void convertToAbstractTransaction() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int loadIntoDatabase(Connection connection) {
		return NOTHING_LOADED;		
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
