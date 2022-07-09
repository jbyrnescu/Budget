package finance.reports.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class PieChartModel {
	
	ArrayList<PieChartEntry> chartEntries = new ArrayList<PieChartEntry>();
	Connection connection;
	String basePath;
	
	PieChartModel(Connection connection, String basePath) {
		this.connection = connection;
		this.basePath = basePath;
	}
	
	public void writePieChartEntries() throws IOException {
		PrintWriter file = new PrintWriter(new File(basePath+"pieChart.csv"));
		for(int i = 0; i < chartEntries.size(); i++) {
			file.println(chartEntries.get(i).getCategory() + "," + chartEntries.get(i).getAmount());
		}
		file.close();
	}
	
	public int getPieChartEntries() throws SQLException {
		String query = "select BudgetCat,sum(amount) from BigTXView "
				+ "where "
				+ "XclFrmCshFlw not like 'y'"
				+ "and budgetCat not like \"%ayment%\" "
				+ "and budgetCat not like \"%Income%\" "
				+ "group by BudgetCat "
				+ "order by sum(amount) asc;";
		
				Statement s = connection.createStatement();
				ResultSet rs = s.executeQuery(query);
				int numberOfEntries = 0;
				while(!rs.isAfterLast()) {
					PieChartEntry pce = new PieChartEntry();
					pce.loadFromResultSet(rs);
					chartEntries.add(pce);
					numberOfEntries++;
				}
				return numberOfEntries;
				
	}

}

