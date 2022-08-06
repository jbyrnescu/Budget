package src.SqliteDBUtils;

import java.sql.Connection;
import java.util.ArrayList;

// 
public class Column {
	
	Connection connection;

	public Column(Connection connection, int columnIndex) {
		this.columnIndex = columnIndex;
		this.connection = connection;
	}
	
	public Column(Connection connection, String columnName) {
		this.columnName = columnName;
		this.connection = connection;
	}
	
	ArrayList<Object> column = new ArrayList<Object>();
	
	int columnIndex;
	String columnName;
	
	public <K, V> void mapColumn(ColumnMap<K, V> map, int keyColumn) {
		
		// This method takes 2 columns, the map column and the mapped column
		// and maps the mapped column into what an algorithm and the map says it should be.
		
		
		
	}
	
}
