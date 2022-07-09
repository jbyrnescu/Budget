package accounts;

import java.io.BufferedWriter;
import finance.Logger;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;

public class BigViewAccount extends Account {

	@Override
	public void loadDatabaseWithTransactions(Connection connection) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void loadTransactionsFromFile(String filename) throws IOException, ParseException {
		// TODO Auto-generated method stub

	}

	@Override
	public void loadTransactionsFromDatabase(Connection c) throws SQLException {
		String queryString = "select * from BigTXView;";
		Statement s = c.createStatement();
		ResultSet rs = s.executeQuery(queryString);
		try {
			while (rs.next()) {
				Transaction t = new BigViewTransaction();
				t.loadTransactionFromDatabase(rs);
				this.addTransaction(t);
			}
		} catch (SQLException e) {
				Logger.out.println("problem reading transactions from Database into memory");
			}
		}

		public void writeTransactionsToCSV(String filename) throws IOException {
			
			Logger.out.println("writing output to csv file: " + filename);
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filename));
			
			// write the headers
//			bufferedWriter.write("TransactionDate|Description|Amount|BudgetCat|XcldFrmCshFlw|Mandatory|Source\n");
			
			for (int i = 0; i < this.getNumberTransactions(); i++) {
				Transaction bvt = getTransaction(i);
				bvt.writeTransaction(bufferedWriter);
			}
			bufferedWriter.flush();
			bufferedWriter.close();
		}

	}
