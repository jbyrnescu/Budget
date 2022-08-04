package finance.reports.model;

import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CombinedRangeCategoryPlot;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultKeyedValueDataset;
import org.jfree.data.general.KeyedValueDataset;

import accounts.StarOneTransaction;
import finance.Logger;

public class SuggestedSavingsModel extends PieChartModel {
	
	HashMap<String, Double> suggestedAmounts = new HashMap<String, Double>();
	Date startDate = null;
	String startStr = null;

	public SuggestedSavingsModel(Connection connection, String basePath) {
		super(connection, basePath);
		this.basePath = basePath;
	}
	
	public void writeEntries(String filename) throws FileNotFoundException, SQLException {
		File file = new File(basePath+"/"+filename);
		PrintWriter out = new PrintWriter(file);
		
		if (startDate == null) {
			Logger.out.println("Need to load a budget from a file before using writeEntries");
		}
		
		double incomeAmount = getIncomeFromDatabase();
		loadPieChartEntriesFromDatabase(startStr,null);
		
		out.println("Actual Savings, amount, suggested Savings, amount, difference");
		
		for (int i = 0; i < chartEntries.size(); i++) {
			double suggestedAmount=0.0, savedAmount = 0.0, percentAmount=0.0;
			String category = chartEntries.get(i).getCategory();

			if (suggestedAmounts.get(category) == null)
			{
				suggestedAmount = 0.0;
				percentAmount = 0.0;
			} else
			{
				suggestedAmount = suggestedAmounts.get(category)*incomeAmount;
				percentAmount = suggestedAmounts.get(category);
			}

			savedAmount = chartEntries.get(i).getAmount();

			String printMe = chartEntries.get(i).getCategory() + "," + savedAmount + "," +
					chartEntries.get(i).getCategory() + "," +  suggestedAmount + "," +
					(suggestedAmount - Math.abs(chartEntries.get(i).getAmount()))
					+ "," + percentAmount;

			out.println(printMe);
			
			Logger.out.println(printMe);
		}
		out.flush();
		out.close();
	}
	
	private double getIncomeFromDatabase() throws SQLException {
		String queryString = "select SUM(amount) from BigTXView where budgetCat = 'Income'"
				+ "and transactionDate>'" + startStr + "';";
		ResultSet rs = connection.createStatement().executeQuery(queryString);
		return(rs.getFloat(1));
	}

	// reads from a CSV the wanted budget, not actual
	public void loadBudgetFromFile(String filename) throws ParseException {
//		File file = Paths.get();
		
		try {
		List<String> lines = Files.readAllLines(Paths.get(basePath+"/"+filename));

		String startDateStr[] = lines.get(0).split(",");
		String dateArray[] = startDateStr[0].split("-");
		Calendar calendar = GregorianCalendar.getInstance();

		int year;
		String yearStr;
		// UTF-8 has a character 16 bit character code which istn' interpretted properly here
		// So, I try to fix that.
		char a;
		if ((a = dateArray[0].charAt(0)) != '2') {
			yearStr = dateArray[0].substring(1);
			year = Integer.parseInt(yearStr);
		} else
			year = Integer.parseInt(dateArray[0].trim());
		
		char b = dateArray[0].charAt(1);
		char c = dateArray[0].charAt(2);
		char d = dateArray[0].charAt(3);
		
		int month = Integer.parseInt(dateArray[1]);
		int day = Integer.parseInt(dateArray[2]);
		calendar.set(year, month-1, day, 0,0,0);
		startDate = calendar.getTime();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
//		startDate = simpleDateFormat.parse(startDateStr[0].trim());
		Logger.out.println("start date for budget: " + simpleDateFormat.format(startDate));
		startStr = simpleDateFormat.format(startDate);
		// delete the first line
		lines.remove(0);
	
		// we skip the first line
		for (String line : lines) {
			
			BudgetItem item = new BudgetItem();
			item.populateItemFromString(line);
			suggestedAmounts.put(item.category, item.amount);

		}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return;
	}

	public void drawBudgetStatus() throws SQLException {
		Date beginDate, endDate;
		beginDate = startDate;
		endDate = GregorianCalendar.getInstance().getTime();
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String beginStr = simpleDateFormat.format(beginDate);
		String endStr = simpleDateFormat.format(endDate);
		// Transfer to Wants ; Transfer To Savings Actual Savings amounts
		loadPieChartEntriesFromDatabase(beginStr, endStr);
		
		double incomeAmount = getIncomeFromDatabase();
		
		// This does the same thing as the previous line.  I already did this.
		//		// draw chart Entries first from Pie Chart
		//		String query = "select BudgetCat, sum(amount) from BigTXView where BudgetCat = " + 
		//				budgetCat +
		//				" and TransactionDate >= " + beginStr + 
		//				" group by BudgetCat;"; 
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		CombinedRangeCategoryPlot plot = new CombinedRangeCategoryPlot();
		
		plot.setDataset(0,dataset);
		for (int i = 0; i < chartEntries.size(); i++) {
			double suggestedAmount;
			String entry = chartEntries.get(i).getCategory();
			
			if (suggestedAmounts.get(entry)==null) 
				suggestedAmount=0.0;
			else
				suggestedAmount = suggestedAmounts.get(entry)*incomeAmount;
			dataset.addValue(suggestedAmount, 
					entry +" suggested","");
			dataset.addValue(Math.abs(chartEntries.get(i).getAmount()), 
					entry, "");

			dataset.addValue(0, Integer.toString(i), "");
		}

//		DefaultCategoryDataset suggestedAmountsDataset = new DefaultCategoryDataset();

//		for (int i = 0; i < suggestedAmounts.size(); i++) {
//			suggestedAmountsDataset.addValue(suggestedAmounts.get(i).amount*daysSinceStart, 
//					suggestedAmounts.get(i).category, "");
//		}
//		plot.setDataset(1, suggestedAmountsDataset);
		
//		JFreeChart chart = new JFreeChart("Budget Status", plot);
		JFreeChart chart = ChartFactory.createBarChart("Budget Status", "Category", "Amount", dataset);
		JPanel jPanel = new ChartPanel(chart);
		jPanel.setSize(560,367);
//		RefineryUtilities.centerFrameOnScreen(jPanel);
		jPanel.setVisible(true);
		JFrame frame = new JFrame("Spending Category Amounts");
		frame.setLocationRelativeTo(null);
		frame.setSize(new Dimension(400,400));
		frame.add(jPanel);
		frame.setVisible(true);
	}
	
//	@override
	public int loadPieChartEntriesFromDatabase(String beginDate, String endDate) throws SQLException {
		String and1 ="", and2 = "";
		String endQuote = "\"";
		if (beginDate == null) {
			beginDate = "";
		} else and1 = " and transactionDate >= \"" +beginDate+ endQuote;
		if (endDate == null)
		{ 
			endDate = ""; 
		} else and2 = " and transactionDate <= \"" + endDate + endQuote;
		
		String query = "select BudgetCat,sum(amount) as amount from BigTXView "
				+ " where "
				+ " budgetCat in ('Transfer Savings', 'Transfer Wants') "
				+ " and "
				+ " budgetCat not like \"%ayment%\" "
				+ " and budgetCat not like \"%Income%\" "
				+ and1
				+ and2
				+ " group by BudgetCat "
				+ " order by sum(amount) asc;";
		
		Logger.out.println("query for pie chart: " + query);
		
				Statement s = connection.createStatement();
				ResultSet rs = s.executeQuery(query);
				int numberOfEntries = 0;
				while(rs.next()) {
					PieChartEntry pce = new PieChartEntry();
					pce.loadFromResultSet(rs);
					chartEntries.add(pce);
					numberOfEntries++;
				}
				return numberOfEntries;
				
	}
	
}
