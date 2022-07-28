package accounts;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import finance.Logger;

public class StarOneTransaction extends Transaction {

	int transactionNumber;
	String memo;
	float debitAmount;
	float creditAmount;
	float balance;
	String checkNumber;
	float fees;
	
	public int getTransactionNumber() {
		return transactionNumber;
	}
	public void setTransactionNumber(int transactionNumber) {
		this.transactionNumber = transactionNumber;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public float getDebitAmount() {
		return debitAmount;
	}
	public void setDebitAmount(float debitAmount) {
		this.debitAmount = debitAmount;
	}
	public float getCreditAmount() {
		return creditAmount;
	}
	public void setCreditAmount(float creditAmount) {
		this.creditAmount = creditAmount;
	}
	public float getBalance() {
		return balance;
	}
	public void setBalance(float balance) {
		this.balance = balance;
	}
	public String getCheckNumber() {
		return checkNumber;
	}
	public void setCheckNumber(String checkNumber) {
		this.checkNumber = checkNumber;
	}
	public float getFees() {
		return fees;
	}
	public void setFees(float fees) {
		this.fees = fees;
	}
	@Override
	public String toString() {
		return "StarOneTransaction [transactionNumber=" + transactionNumber + ", memo=" + memo + ", debitAmount="
				+ debitAmount + ", creditAmount=" + creditAmount + ", balance=" + balance + ", checkNumber="
				+ checkNumber + ", fees=" + fees + ", transactionDate=" + transactionDate + ", description="
				+ description + ", amount=" + amount + ", budgetCat=" + budgetCat + ", xcludeFromCashFlow="
				+ xcludeFromCashFlow + ", mandatory=" + mandatory + ", source=" + source + "]";
	}
	
	public void populateTransactionFromString(String line)
			throws ParseException {
		StarOneTransaction starOneTransaction = this;
		
		String[] columns = line.split(",");

		ArrayList<String> columnList = convertTransactions(columns);
		
		// now just set the appropriate fields
		starOneTransaction.setTransactionNumber(Integer.parseInt(columnList.get(0)));
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
		starOneTransaction.setTransactionDate(simpleDateFormat.parse(columnList.get(1)));
		Logger.out.println("date set: " + starOneTransaction.getTransactionDate());
// Notice these are switched around because they are weird 
		starOneTransaction.setDescription(columnList.get(3));
		starOneTransaction.setMemo(columnList.get(2));
		starOneTransaction.setDebitAmount(Float.parseFloat(columnList.get(4)));
		starOneTransaction.setCreditAmount(Float.parseFloat(columnList.get(5)));
		starOneTransaction.setBalance(Float.parseFloat(columnList.get(6)));
		starOneTransaction.setCheckNumber(columnList.get(7));
		starOneTransaction.setFees(Float.parseFloat(columnList.get(8)));
		
		starOneTransaction.setSource("CheckingStarOneTXs");

		return;
	}

	private static ArrayList<String> convertTransactions(String[] columns) {
		ArrayList<String> list = new ArrayList<String>(9); 
		for(int i = 0; i < columns.length; i++)
		{
			list.add(columns[i]);
		}
		
		if (columns.length <= 7)
		{
			// add the missing columns and fill them
			list.add("");
		}
		if (columns.length <= 8) {
			list.add("0.0");
		}
		
		if (list.get(4).contentEquals("")) list.set(4, "0.0");
		if (list.get(5).equals("")) list.set(5, "0.0");
		return(list);
	}
	
	public void convertToAbstractTransaction() {
		//In this method we convert the more specific StarOne Transaction into
		// one that will work inside our program
		// In this case we deduce the amount from creditAmount & debitAmount (and switch the sign)
		// Then we convert all of the budget categories to what they should be with the category
		// map.
		// We also can get an Object from the transaction  based on the markings inside the db
		// Later on we fill the database with these values.
		
		// all we do is zero out the creditAmount & debitAmount after we calculate credit-debit for
		// amount.
		
		this.amount = creditAmount - debitAmount;
	}
	@Override
	public int loadIntoDatabase(Connection connection) throws SQLException {
		if (super.loadIntoDatabase(connection) == TRANSACTION_EXISTS) return NOTHING_LOADED;
		PreparedStatement statement = connection.prepareStatement("insert into CheckingStarOneTXs ("
				+ "transactionNumber, memo, debitAmount, creditAmount, balance, checkNumber, "
				+ "fees, transactionDate, description, amount, budgetCat, XclFrmCshFlw, mandatory,"
				+ "source) "
				+ "values ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
		statement.setInt(1, this.transactionNumber);
		statement.setString(2, this.memo);
		statement.setFloat(3, debitAmount);
		statement.setFloat(4, this.creditAmount);
		statement.setFloat(5, this.balance);
		statement.setString(6, checkNumber);
		statement.setFloat(7, this.fees);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		statement.setString(8, simpleDateFormat.format(transactionDate));
//		statement.setDate(8, new java.sql.Date(transactionDate.getTime()));
		statement.setString(9, description);
		statement.setDouble(10, amount);
		statement.setString(11, budgetCat);
		statement.setString(12, xcludeFromCashFlow);
		statement.setString(13, mandatory);
		statement.setString(14, source);
				
		int numUpdates = statement.executeUpdate();
		return(TRANSACTION_LOADED);
	}

	@Override
	public Transaction loadTransactionFromDatabase(ResultSet rs) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
}
