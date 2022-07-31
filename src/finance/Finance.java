package finance;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;

import SqliteDBUtils.Column;
import SqliteDBUtils.ColumnMap;
import accounts.Account;
import accounts.BigViewAccount;
import accounts.ChaseAccount;
import accounts.Expenses;
import accounts.StarOneAccount;
import db.Tables;

import finance.Logger;
import finance.reports.model.BudgetModel;
import finance.reports.model.PieChartModel;

public class Finance {
	
	// push/pull key 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

	String baseProjectPath;
	String downloadsDirectory;
	ArrayList<Account> accounts = new ArrayList<Account>();

	ColumnMap<String, String> categoriesMap ;

	Finance() {
	}

	private Connection connection;
	ArrayList<ArrayList<Object>> table = new ArrayList<ArrayList<Object>>();

	ArrayList<Object> row = new ArrayList<Object>();
	private ColumnMap<String, String> mandatoryMap;
	private ColumnMap<String, String> excludedTransactionsMap;


	public static void main(String[] args) throws SQLException, IOException, ParseException {
		// we have to instantiate a Logger because it throws an exception with file creation
		// In other words we can't make it static
		Logger logger = new Logger();
		logger.toggleStdout();

		Logger.out.println("Arguments passed: ");
		for (int i = 0; i < args.length; i++) {
			Logger.out.println(i + ":" + args[i]);
		}

		
		String downloadPath= System.getenv("FINANCE_DOWNLOADS_PATH");
		String basePath = System.getenv("FINANCE_BASE_PATH");
		Finance finance = new Finance();
		
		if (downloadPath == null) {
			finance.setDownloadDirectory(args[1]);
			downloadPath = args[1];
//			finance.setDownloadDirectory("/Users/jbyrne/Downloads/");
		}
		else
		{
			finance.setDownloadDirectory(downloadPath);
		}
			
		if (basePath == null) { 
			finance.setBasePath(args[0]);
			basePath = args[0];
		}
		
//			finance.setBasePath("/Users/jbyrne/Dropbox/finance/");
		else
			finance.setBasePath(basePath);
		

			
		finance.connect();


		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		Calendar calendar1 = GregorianCalendar.getInstance();
		Date date1 = calendar1.getTime();
		String dateStr2 = simpleDateFormat.format(date1);

		calendar1.add(Calendar.DATE, -40);
		Date date2 = calendar1.getTime();
		String dateStr1 = simpleDateFormat.format(date2);
		
		
		Enumeration<Driver> drivers = DriverManager.getDrivers();
		Driver driver = drivers.nextElement();
		Logger.out.println("driver.toString()... " + driver.toString());

		finance.createAccountDatabase();
		finance.writeTransactionsToCSV("output.csv", dateStr1, dateStr2);
		finance.writeDatabaseToCSV("output2.csv");
		
		PieChartModel pcm = new PieChartModel(finance.connection, basePath);
		pcm.loadPieChartEntriesFromDatabase(dateStr1, dateStr2);
		pcm.writePieChartEntries();
		pcm.drawPieChart();
		
		PieChartModel pcm2 = new PieChartModel(finance.connection, basePath);
		pcm2.loadPieChartEntriesFromDatabase(null,  null);
		pcm2.drawPieChart();
		
		Expenses e = new Expenses(finance.connection);
		e.loadTransactionsFromDatabase(finance.connection, dateStr1, dateStr2);
		e.drawCashFlowGraph();
		
		Expenses e2 = new Expenses(finance.connection);
		e2.loadTransactionsFromDatabase(null, null, null);
		e2.drawCashFlowGraph();
		
		BudgetModel b = new BudgetModel(finance.connection, basePath);
		b.loadBudgetFromFile("DollarsPerDayExpenditures.csv");
		b.drawBudgetStatus();
		b.writeEntries("Budget.csv");
		
		finance.closeAll();
	}

	private void writeDatabaseToCSV(String filename) throws SQLException, IOException {
		BigViewAccount bva = new BigViewAccount();		
		bva.loadTransactionsFromDatabase(connection, null, null);
		Logger.out.println("writing transactions to: " + baseProjectPath + filename);
		bva.writeTransactionsToCSV(baseProjectPath+"/"+filename);		
	}

	private void writeTransactionsToCSV(String filename, String dateStr1, String dateStr2) throws IOException, SQLException {

		// just write all Transactions from BigTXView
		// the other option is to load memory/Transactions and print them... 
		// but, we're not going to do that
		BigViewAccount bva = new BigViewAccount();
		

//		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
//		
//		Calendar calendar1 = GregorianCalendar.getInstance();
//		Date date1 = calendar1.getTime();
//		String dateStr2 = simpleDateFormat.format(date1);
//
//		calendar1.add(Calendar.DATE, -40);
//		Date date2 = calendar1.getTime();
//		String dateStr1 = simpleDateFormat.format(date2);
		
		bva.loadTransactionsFromDatabase(connection, dateStr1, dateStr2);
		Logger.out.println("writing transactions to: " + baseProjectPath + filename);
		bva.writeTransactionsToCSV(baseProjectPath+"/"+filename);
	}

	private void closeAll() throws SQLException {
		connection.close();
	}

	private void setBasePath(String directory) {
		baseProjectPath=directory;
	}

	private void setDownloadDirectory(String directory) {
		downloadsDirectory = directory;
	}

	public void createAccountDatabase() throws IOException, ParseException, SQLException {
		// read in Star One Checking Account file
		StarOneAccount soa = new StarOneAccount();
		accounts.add(soa);
		downloadsDirectory = "/"+downloadsDirectory+"/";
		Logger.out.println("loading transactions files from directory: " + downloadsDirectory);
		soa.loadDirectory(downloadsDirectory);
//		soa.loadTransactionsFromFile(downloadsDirectory+"statement_starone_2_06_10_2022_to_06_26_2022.csv");
//		soa.printTransactions();

		ChaseAccount chaseAccount = new ChaseAccount();
		accounts.add(chaseAccount);
		chaseAccount.loadDirectory(downloadsDirectory);
//		chaseAccount.loadTransactionsFromFile(downloadsDirectory+"Chase3929_Activity20220610_20220627_20220627.CSV");
//		chaseAccount.printTransactions();

		Tables tables = new Tables(this.getConnection());

		String schema = this.getConnection().getSchema();
		String[] types = {"TABLE"};
		ResultSet r = this.getConnection().getMetaData().getTables(null, null, "%", types);
		while(r.next()) {
			Logger.out.println(r.getString("TABLE_NAME"));
		}
		// database should be loaded with transactions after the next 2 lines.   Check the .db file.
		soa.loadDatabaseWithTransactions(this.getConnection());
		chaseAccount.loadDatabaseWithTransactions(this.getConnection());

		Logger.out.println("IN MEMORY:");
		soa.printTransactions();

		chaseAccount.printTransactions();


		// use columnMap to rename categories
		this.readCategoriesMap("Categorized.csv");
		this.remapCategories();

		// use columnMap to change mandatory
		this.readMandatoryMap("MandatoryMap.csv");
		this.markMandatory();
		
		// use columnMap to change XcludeFromCashFlow
		this.readExcludeFromCashFlowMap("XcldFrmCshFlw.csv");
		this.markExcludedTransactions();

	}

	private void markExcludedTransactions() throws SQLException {
		for (int accountNum = 0; accountNum < accounts.size(); accountNum++) {
			String source = accounts.get(accountNum).getSourceName();
			for (String key : excludedTransactionsMap.keySet()) {
				String queryString = "update " + source + " set XclFrmCshFlw=\"" 
						+ excludedTransactionsMap.get(key) +
						"\" where Description like \"%" + key + "%\";";
				Logger.out.println("updating: " + queryString);
				Statement statement = connection.createStatement();
				int numUpdated = statement.executeUpdate(queryString);
				Logger.out.print(numUpdated);
			}
		}
	}

	private void readExcludeFromCashFlowMap(String file) throws IOException {

		excludedTransactionsMap = new ColumnMap<String, String>(baseProjectPath + "/" + file);

//			mandatoryMap.printMap();
	}

	private void markMandatory() throws SQLException {
		for (int accountNum = 0; accountNum < accounts.size(); accountNum++) {
			String source = accounts.get(accountNum).getSourceName();
			for (String key : categoriesMap.keySet()) {
				String queryString = "update " + source + " set Mandatory=\"" + mandatoryMap.get(key) +
						"\" where Description like \"%" + key + "%\";";
				Logger.out.println("updating: " + queryString);
				Statement statement = connection.createStatement();
				int numUpdated = statement.executeUpdate(queryString);
				Logger.out.print(numUpdated);
			}
		}
	}

	private void remapCategories() throws SQLException {
		for (int accountNum = 0; accountNum < accounts.size(); accountNum++) {
			String source = accounts.get(accountNum).getSourceName();
			for (String key : categoriesMap.keySet()) {
				String queryString = "update " + source + " set BudgetCat=\"" + categoriesMap.get(key) +
						"\" where Description like \"%" + key + "%\";";
				Logger.out.println("updating: " + queryString);
				Statement statement = connection.createStatement();
				int numUpdated = statement.executeUpdate(queryString);
				Logger.out.print(numUpdated);
			}
		}
	}
	
	private void readMandatoryMap(String file) throws IOException {

//		Column column = new Column(this.getConnection(),999); 
		mandatoryMap = new ColumnMap<String, String>(baseProjectPath + "/MarkMandatory/" + file);

//		mandatoryMap.printMap();
	}

	private void readCategoriesMap(String file) throws IOException {

//		Column column = new Column(this.getConnection(),4); 
		categoriesMap = new ColumnMap<String, String>(baseProjectPath + "/Categorize/" + file);

//		categoriesMap.printMap();
	}

	public Connection getConnection() {
		return connection;
	}

	/**
	 * Connect to a sample database
	 */
	public void connect() {
		connection = null;
		try {
			// db parameters
//			String url = "jdbc:sqlite:" + baseProjectPath + "TXs2.db";
			String url = "jdbc:sqlite:" + baseProjectPath + "/TXs2.db";
			// create a connection to the database
			connection = DriverManager.getConnection(url);

			Logger.out.println("Connection to SQLite has been established.");

		} catch (SQLException e) {
			Logger.out.println(e.getMessage());
		} finally {
			/*            try {
                if (connection != null) {
                	Logger.out.println("Connection not null... congrats!");
/*                    connection.close(); 
                }
            } catch (SQLException ex) {
                Logger.out.println(ex.getMessage());
            } */
		}
	}


	private static void testConnection(Finance finance) throws SQLException {
		Statement statement = finance.connection.createStatement();

		statement.execute("Select * from BigTXView;");

		ResultSet resultSet = statement.getResultSet();
		while (resultSet.next())
			for (int i = 1; i < resultSet.getMetaData().getColumnCount(); i++) {
				Logger.out.println("i: " + i);
				Logger.out.println(resultSet.getString(i));
			}
	}



}
