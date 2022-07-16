package accounts;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;

public class Incomes extends BigViewAccount {
	@Override
	public void loadTransactionsFromDatabase(Connection c) throws SQLException {
		
		String query = "select * from BigTXView where BudgetCat like \"%ncome%\" "
				+ "and amount > 0;";

		ResultSet rs = c.createStatement().executeQuery(query);
		while(rs.next()) {
			BigViewTransaction bvt = new BigViewTransaction();
			bvt.loadTransactionFromDatabase(rs);
			addTransaction(bvt);
		}
	}

	public XYDataset getXYDataset() {
		int numTransactions = getNumberTransactions();
		double[][] array = new double[2][numTransactions];
		
		float cumulativeAmount = 0.0f;
		
		for (int i = 0; i < getNumberTransactions(); i++) {
			array[0][i] = getTransaction(i).getTransactionDate().getTime();
			cumulativeAmount += getTransaction(i).getAmount();
			array[1][i] = cumulativeAmount;

		}
		
		DefaultXYDataset dataset = new DefaultXYDataset();
		dataset.addSeries("Cumulative Income Amount", array);
		return(dataset);
//
	}
}
