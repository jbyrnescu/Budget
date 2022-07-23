package accounts;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;

public class Incomes extends BigViewAccount {
	
	private static double[][] array;
	
	@Override
	public void loadTransactionsFromDatabase(Connection c, String beginDate, String endDate) throws SQLException {

		String and1 ="", and2 = "";
		String endQuote = "\"";
		if (beginDate == null) {
			beginDate = "";
		} else and1 = " and transactionDate >= \"" +beginDate+ endQuote;
		if (endDate == null)
		{ 
			endDate = ""; 
		} else and2 = " and transactionDate <= \"" + endDate + endQuote;
		
		String query = "select * from BigTXView where BudgetCat like \"%ncome%\" "
				+ and1
				+ and2
				+ "and amount > 0;";

		ResultSet rs = c.createStatement().executeQuery(query);
		while(rs.next()) {
			BigViewTransaction bvt = new BigViewTransaction();
			bvt.loadTransactionFromDatabase(rs);
			addTransaction(bvt);
		}
	}

	public XYDataset getXYDataset() {

		getArrayOfPoints();
		
		DefaultXYDataset dataset = new DefaultXYDataset();
		dataset.addSeries("Cumulative Income Amount", array);
		return(dataset);
//
	}
	
	public double[][] getArrayOfPoints() {
		int numTransactions = getNumberTransactions();
		
		double cumulativeAmount = 0.0;
		array = new double[2][numTransactions];
		
		for (int i = 0; i < getNumberTransactions(); i++) {
			array[0][i] = getTransaction(i).getTransactionDate().getTime();
			cumulativeAmount += getTransaction(i).getAmount();
			array[1][i] = cumulativeAmount;

		}
		return array;
	}
}
