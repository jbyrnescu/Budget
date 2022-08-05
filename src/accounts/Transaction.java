package accounts;

import java.io.BufferedWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import finance.Logger;

public abstract class Transaction {
	// This class holds the generalized, most abstract aspects of an account
		// further implementation details are delegated to implementation classes
		// such as StarOne
	
	public static int TRANSACTION_EXISTS = 1;
	public static int NO_SIMILAR_TRANSACTIONS = 3;
	public static int TRANSACTION_LOADED = 2;
	public static int NOTHING_LOADED = 4;
	
		Date transactionDate;
		String description;
		double amount;
		String budgetCat;
		String xcludeFromCashFlow;
		String mandatory;
		String source;
		
		public abstract void convertToAbstractTransaction();
		public int loadIntoDatabase(Connection connection) throws SQLException {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
			String transactionDateString = simpleDateFormat.format(transactionDate);
			
			Logger.out.println("Checking for existing transaction");
			String queryString = "select * from BigTXView where "
					+ " transactionDate = \"" + transactionDateString + "\""
					+ " and description = \"" + description + "\""
					+ " and amount = " + amount;
			
			
			
			Statement s = connection.createStatement();
			ResultSet rs = s.executeQuery(queryString);
//			ResultSetMetaData rsmd = rs.getMetaData();
			if (rs.next()) return TRANSACTION_EXISTS;
			return NO_SIMILAR_TRANSACTIONS;
		}
		public abstract void populateTransactionFromString(String line) throws ParseException;

		public Transaction loadTransactionFromDatabase(ResultSet rs) throws SQLException {
			this.amount = rs.getFloat("amount");
			this.transactionDate = rs.getDate("TransactionDate");
			this.description = rs.getString("Description");
			this.budgetCat = rs.getString("BudgetCat");
			this.xcludeFromCashFlow = rs.getString("XclFrmCshFlw");
			this.mandatory = rs.getString("Mandatory");
			this.source = rs.getString("Source");
			return this;
		}
		
		protected String transactionType;
		
		
		public String getTransactionType() {
			return transactionType;
		}
		public void setTransactionType(String transactionType) {
			this.transactionType = transactionType;
		}
		
		public String getTransactionDateString() {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
			return simpleDateFormat.format(transactionDate);
			
		}
		
		public Date getTransactionDate() {
			return transactionDate;
		}
		public void setTransactionDate(Date transactionDate) {
			this.transactionDate = transactionDate;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public double getAmount() {
			return amount;
		}
		public void setAmount(float amount) {
			this.amount = amount;
		}
		public String getBudgetCat() {
			return budgetCat;
		}
		public void setBudgetCat(String budgetCat) {
			this.budgetCat = budgetCat;
		}
		public String getXcludeFromCashFlow() {
			return xcludeFromCashFlow;
		}
		public void setXcludeFromCashFlow(String xcludeFromCashFlow) {
			this.xcludeFromCashFlow = xcludeFromCashFlow;
		}
		public String getMandatory() {
			return mandatory;
		}
		public void setMandatory(String mandatory) {
			this.mandatory = mandatory;
		}
		public String getSource() {
			return source;
		}
		public void setSource(String source) {
			this.source = source;
		}
		
		public void print() {
			Logger.out.println(this.toString());
		}
		
		@Override
		public String toString() {
			return "Transaction [transactionDate=" + transactionDate + ", description=" + description + ", amount="
					+ amount + ", budgetCat=" + budgetCat + ", xcludeFromCashFlow=" + xcludeFromCashFlow
					+ ", mandatory=" + mandatory + ", source=" + source + "]";
		}
		protected void writeTransaction(BufferedWriter bufferedWriter) throws IOException {

			this.changeNullsToBlanks();
			
			bufferedWriter.write(getTransactionDate() + "|");
			bufferedWriter.write(getDescription() + "|");
			bufferedWriter.write(getAmount() + "|");
			bufferedWriter.write(getBudgetCat() + "|");
			bufferedWriter.write(getXcludeFromCashFlow() + "|");
			bufferedWriter.write(getMandatory() + "|");
			bufferedWriter.write(getSource());
			bufferedWriter.write("\n");
			
		}
		protected void changeNullsToBlanks() {
			if (getDescription()==null)
				setDescription("");
			if (getBudgetCat()==null)
				setBudgetCat("");

			if (getMandatory()==null || getMandatory().contentEquals("null"))
				setMandatory("");
			if (getXcludeFromCashFlow()==null)
				setXcludeFromCashFlow("");


			if (getSource()==null)
				setSource("");
		}

}
