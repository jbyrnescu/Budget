package finance;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import SqliteDBUtils.Column;
import SqliteDBUtils.ColumnMap;

public class Finance {
	
	Finance() {
		// we're going to assume you want to connect
		connect();
	}
	
	private Connection connection;
	ArrayList<ArrayList<Object>> table = new ArrayList<ArrayList<Object>>();

	ArrayList<Object> row = new ArrayList<Object>();

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
            String url = "jdbc:sqlite:/home/jbyrne/Dropbox/eclipse-workspace/SqliteTest/TXs.db";
            // create a connection to the database
            connection = DriverManager.getConnection(url);
            
            System.out.println("Connection to SQLite has been established.");
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
/*            try {
                if (connection != null) {
                	System.out.println("Connection not null... congrats!");
/*                    connection.close(); 
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            } */
        }
    }

	public static void main(String[] args) throws SQLException, IOException {
		Finance finance = new Finance();
		
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        Driver driver = drivers.nextElement();
        System.out.println("driver.toString()... " + driver.toString());
        
//        testConnection(finance);
        
    	Column column = new Column(finance.getConnection(),4); 
    	ColumnMap<String, String> columnMap = new ColumnMap<String, String>("Categorized.csv");
    	
    	columnMap.printMap();
        
	}

	private static void testConnection(Finance finance) throws SQLException {
		Statement statement = finance.connection.createStatement();
        
        statement.execute("Select * from BigTXView;");
        
        ResultSet resultSet = statement.getResultSet();
        while (resultSet.next())
        	for (int i = 1; i < resultSet.getMetaData().getColumnCount(); i++) {
        		System.out.println("i: " + i);
        		System.out.println(resultSet.getString(i));
        	}
	}

	

}
