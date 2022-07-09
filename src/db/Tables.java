package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

// This should really be done with Persistent classes and Spring, jpa what have you
// However, I'm not very good with these frameworks and I don't want to spend the time
// to learn them.  Plus, those frameworks need dependent packages/libraries that I don't want
// to deal with.  And, you never know... those libraries may go away one day.

public class Tables {

	Connection connection;
	
	public Tables(Connection connection) throws SQLException
	{
		this.connection = connection;
		createTables();
	}
	
	public void createTables() throws SQLException {
		String statement = "CREATE TABLE IF NOT EXISTS \"CheckingStarOneTXs\" (\n" + 
				"	\"TransactionNumber\"	INTEGER,\n" + 
				"	\"TransactionDate\"	TEXT,\n" + 
				"	\"Memo\"			TEXT,\n" + 
				"	\"Description\"	TEXT,\n" + 
				"	\"DebitAmount\"	REAL,\n" + 
				"	\"CreditAmount\"	REAL,\n" + 
				"	\"Balance\"	REAL,\n" + 
				"	\"CheckNumber\"	TEXT,\n" + 
				"	\"Fees\"		REAL,\n" + 
				"	\"BudgetCat\"	TEXT,\n" + 
				"	\"Amount\"	REAL,\n" + 
				"	\"XclFrmCshFlw\"	TEXT,\n" + 
				"	\"Mandatory\"	TEXT,\n" + 
				"	\"Source\"	TEXT\n" + 
				"\n" + 
				");\n" ;
		PreparedStatement s = connection.prepareStatement(statement);
		Integer iReturnValue = s.executeUpdate();
	
		String SavingsStarOneString = "CREATE TABLE IF NOT EXISTS \"SavingsStarOneTXs\" (\n" + 
				"	\"TransactionNumber\"	INTEGER,\n" + 
				"	\"TransactionDate\"	TEXT,\n" + 
				"	\"Memo\"		TEXT,\n" + 
				"	\"Description\"	TEXT,\n" + 
				"	\"DebitAmount\"	REAL,\n" + 
				"	\"CreditAmount\"	REAL,\n" + 
				"	\"Balance\"	REAL,\n" + 
				"	\"CheckNumber\"	TEXT,\n" + 
				"	\"Fees\"		TEXT,\n" + 
				"	\"BudgetCat\"	TEXT,\n" + 
				"	\"Amount\"	REAL,\n" + 
				"	\"XclFrmCshFlw\"	TEXT,\n" + 
				"	\"Mandatory\"	TEXT,\n" + 
				"	\"Source\"	TEXT\n" + 
				");";
		s = connection.prepareStatement(SavingsStarOneString);
		iReturnValue = s.executeUpdate();
		
		String visaChaseString = "CREATE TABLE IF NOT EXISTS \"VisaChaseTXs\" (\n" + 
				"	\"TransactionDate\"	TEXT,\n" + 
				"	\"PostDate\"	TEXT,\n" + 
				"	\"Description\"	TEXT,\n" + 
				"	\"Category\"	TEXT,\n" + 
				"	\"TransactionType\"		TEXT,\n" + 
				"	\"Amount\"	REAL,\n" + 
				"	\"BudgetCat\"	TEXT,\n" + 
				"	\"Memo\"		TEXT,\n" + 
				"	\"XclFrmCshFlw\"	TEXT,\n" + 
				"	\"Mandatory\"	TEXT,\n" + 
				"	\"balance\"	REAL,\n" + 
				"	\"Source\"	TEXT\n" + 
				");";
		s = connection.prepareStatement(visaChaseString);
		iReturnValue = s.executeUpdate();
		
		String BigTXViewString = "CREATE VIEW IF NOT EXISTS BigTXView as\n" + 
				"\n" + 
				"select TransactionDate, Description, amount, BudgetCat, XclFrmCshFlw, Mandatory, source from VisaChaseTXs\n" + 
				"union \n" + 
				"select TransactionDate, Description, amount, BudgetCat, XclFrmCshFlw, Mandatory, source from CheckingStarOneTXs\n" + 
				"union\n" + 
				"select TransactionDate, Description, amount, BudgetCat, XclFrmCshFlw, Mandatory, source from SavingsStarOneTXs\n"; 
		s = connection.prepareStatement(BigTXViewString);
		iReturnValue = s.executeUpdate();
		
	}
	
}
