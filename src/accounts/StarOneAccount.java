package accounts;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StarOneAccount extends Account {
	// This class extends accounts and adds account implementation specific
	// fields to this particular bank.
	
	public StarOneAccount() {
		sourceName = "CheckingStarOneTXs";
		filenamePrefix = "statement_starone_2_.*";
	}
	
		
	public void loadTransactionsFromFile(String filename) throws IOException, ParseException {
		StarOneAccount starOneAccount = this;
		
		// open the file and get just the data we want from it
		try {
		List<String> lines = Files.readAllLines(Paths.get(filename));

		
		// delete the first line
		lines.remove(0);

	
		// we skip the first line
		for (String line : lines) {
			
			StarOneTransaction t = new StarOneTransaction();
			t.populateTransactionFromString(line);
			starOneAccount.addTransaction(t);
			t.convertToAbstractTransaction();
		}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		return;
	}

	@Override
	public void loadDatabaseWithTransactions(Connection connection) throws SQLException {
		for (Transaction t : getTransactions()) {
			t.loadIntoDatabase(connection);
		}
	}


	@Override
	public void loadTransactionsFromDatabase(Connection c) throws SQLException {
		// TODO Auto-generated method stub
		
	}


}
