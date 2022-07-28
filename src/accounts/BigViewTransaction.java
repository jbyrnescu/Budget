package accounts;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;

public class BigViewTransaction extends Transaction {

	@Override
	public void convertToAbstractTransaction() {
	//  We won't need to do this right now
	}

	@Override
	public int loadIntoDatabase(Connection connection) throws SQLException {
	//  We won't need to do this right now
		return(NOTHING_LOADED);
	}

	@Override
	public void populateTransactionFromString(String line) throws ParseException {
	//	We won't need to do this right now
	}


}
