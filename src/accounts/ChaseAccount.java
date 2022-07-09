package accounts;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

public class ChaseAccount extends Account {
	
	public ChaseAccount() {
		sourceName = "VisaChaseTXs";
		filenamePrefix="Chase3929_Activity.*";
	}

	@Override
	public void loadDatabaseWithTransactions(Connection connection) throws SQLException {
		for (Transaction t : getTransactions()) {
			t.loadIntoDatabase(connection);
		}
	}

	@Override
	public void loadTransactionsFromFile(String filename) throws IOException, ParseException {
		
		// open the file and get just the data we want from it
		List<String> lines = Files.readAllLines(Paths.get(filename));
		
		// delete the first line
		lines.remove(0);

	
		// we skip the first line
		for (String line : lines) {
			
			
			ChaseTransaction t = new ChaseTransaction();
			t.populateTransactionFromString(line);
			this.addTransaction(t);
			t.convertToAbstractTransaction();
		}
		
		return;
	}

	@Override
	public void loadTransactionsFromDatabase(Connection c) throws SQLException {
		// TODO Auto-generated method stub
		
	}

}
