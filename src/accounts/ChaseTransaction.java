package src.accounts;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChaseTransaction extends Transaction {

	Date postDate;
	String Category;
	String transactionType;
	String memo;

	public Date getPostDate() {
		return postDate;
	}

	public void setPostDate(Date postDate) {
		this.postDate = postDate;
	}
	
	public String getPostDateAsString() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		return simpleDateFormat.format(getPostDate());
	}

	public String getCategory() {
		return Category;
	}

	public void setCategory(String category) {
		Category = category;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public float getBalance() {
		return balance;
	}

	public void setBalance(float balance) {
		this.balance = balance;
	}

	float balance;
	
	@Override
	public void convertToAbstractTransaction() {
		
	}

	@Override
	public int loadIntoDatabase(Connection connection) throws SQLException {
		int returnCode = super.loadIntoDatabase(connection);
		if (returnCode == TRANSACTION_EXISTS) return NOTHING_LOADED;
		PreparedStatement statement = connection.prepareStatement("insert into VisaChaseTXs ("
				+ "TransactionDate, PostDate, Description, Category, TransactionType, Amount, "
				+ "Memo, "
				+ "budgetCat, XclFrmCshFlw, mandatory,"
				+ "source) "
				+ "values ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		statement.setString(8, simpleDateFormat.format(transactionDate));
		
		
		statement.setString(1, getTransactionDateString());
		statement.setString(2, simpleDateFormat.format(this.postDate));
		statement.setString(3, this.description);
		statement.setString(4, Category);
		statement.setString(5, transactionType);
		statement.setDouble(6, getAmount());
		statement.setString(7, getMemo());
		statement.setString(8, getBudgetCat());
		statement.setString(9, getXcludeFromCashFlow());
		statement.setString(10, getMandatory());
		statement.setString(11, "VisaChaseTXs");
		
		statement.executeUpdate();
		return(TRANSACTION_LOADED);
	}

	public void populateTransactionFromString(String line) throws ParseException {
		String[] columns = line.split(",");

		ArrayList<String> columnList = convertTransactions(columns);
		
		// now just set the appropriate fields
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
		setTransactionDate(simpleDateFormat.parse(columnList.get(0)));
		setPostDate(simpleDateFormat.parse(columnList.get(1)));
		setDescription(columnList.get(2));
		setCategory(columnList.get(3));
		setTransactionType(columnList.get(4));
		setAmount(Float.parseFloat(columnList.get(5)));
		setMemo(columnList.get(6));
		
	}

	private ArrayList<String> convertTransactions(String[] columns) {
		ArrayList<String> columnList = new ArrayList<String>();
		for (int i = 0; i < columns.length; i++) {
			columnList.add(columns[i]);
		}
		// if there was not memo... just add one memory is cheap
		if (columnList.size() <= 6) {
			columnList.add("");
		}
		return columnList;
	}

	@Override
	public Transaction loadTransactionFromDatabase(ResultSet rs) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

}
