package finance.reports.model;

import java.awt.Dimension;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
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

public class BudgetModel extends PieChartModel {
	
	HashMap<String, Double> allowedAmounts = new HashMap<String, Double>();
	Date startDate = null;

	public BudgetModel(Connection connection, String basePath) {
		super(connection, basePath);
	}
	
	public long getDaysSinceStart() {
		if (startDate == null)
			return 0;
		else
		{
			Calendar now = GregorianCalendar.getInstance();
			long nowTimeInMillis = now.getTimeInMillis();
			return((nowTimeInMillis-startDate.getTime())/1000/60/60/24);
		}
	}
	
	public void writeEntries(String filename) throws FileNotFoundException {
		File file = new File(basePath+"/"+filename);
		PrintWriter out = new PrintWriter(file);
		
		long daysSinceStart = getDaysSinceStart();
		out.println("ActualSpendingCategory1, amount, AllowedSpending Category2, amount, difference, dailyAllowedAmount, days to go");
		
		for (int i = 0; i < chartEntries.size(); i++) {
			double allowedAmount=0.0, spentAmount = 0.0, dailyAllowedAmount;
			String category = chartEntries.get(i).getCategory();

			if (allowedAmounts.get(category) == null)
			{
				allowedAmount = 0.0;
				dailyAllowedAmount = 0.0;
			} else
			{
				allowedAmount = allowedAmounts.get(category)*daysSinceStart;
				dailyAllowedAmount = allowedAmounts.get(category);
			}

			spentAmount = chartEntries.get(i).getAmount();
			double daysToGoUntil0 = (allowedAmount - Math.abs(chartEntries.get(i).getAmount()))/dailyAllowedAmount;

			String printMe = chartEntries.get(i).getCategory() + "," + spentAmount + "," +
					chartEntries.get(i).getCategory() + "," +  allowedAmount + "," +
					(allowedAmount - Math.abs(chartEntries.get(i).getAmount()))
					+ "," + dailyAllowedAmount + "," + daysToGoUntil0;

			out.println(printMe);
			
			Logger.out.println(printMe);
		}
		out.flush();
		out.close();
	}
	
	// reads from a CSV the wanted budget, not actual
	public void loadBudgetFromFile(String filename) throws ParseException {
		BudgetModel budgetModel = this;
		
//		File file = Paths.get();
		
		try {
		List<String> lines = Files.readAllLines(Paths.get(basePath+"/"+filename));


		
		String startDateStr[] = lines.get(0).split(",");
		String dateArray[] = startDateStr[0].split("-");
		Calendar calendar = GregorianCalendar.getInstance();
		// This looks like designed obsolescense, but the only way I could
		// get this to work is to hard-code 2022.  Go figure.  The other
		// line below did not work.  I don't know!
		int year;
//		int year = Integer.parseInt("2022");
//		int year = Integer.parseInt(dateArray[0].trim());
		char a;
		if ((a = dateArray[0].charAt(0)) != 0) {
			String yearStr = dateArray[0].substring(1);
			year = Integer.parseInt(yearStr);
		} else
			year = Integer.parseInt(dateArray[0].trim());
		
		char b = dateArray[0].charAt(1);
		char c = dateArray[0].charAt(2);
		char d = dateArray[0].charAt(3);
		char e = dateArray[0].charAt(4);
		
		int month = Integer.parseInt(dateArray[1]);
		int day = Integer.parseInt(dateArray[2]);
		calendar.set(year, month-1, day, 0,0,0);
		startDate = calendar.getTime();
//		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
//		startDate = simpleDateFormat.parse(startDateStr[0].trim());
		// delete the first line
		lines.remove(0);

	
		// we skip the first line
		for (String line : lines) {
			
			BudgetItem item = new BudgetItem();
			item.populateItemFromString(line);
			allowedAmounts.put(item.category, item.amount);

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
		
		loadPieChartEntriesFromDatabase(beginStr, endStr);
		
		// This does the same thing as the previous line.  I already did this.
		//		// draw chart Entries first from Pie Chart
		//		String query = "select BudgetCat, sum(amount) from BigTXView where BudgetCat = " + 
		//				budgetCat +
		//				" and TransactionDate >= " + beginStr + 
		//				" group by BudgetCat;"; 
		long daysSinceStart = getDaysSinceStart();
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		CombinedRangeCategoryPlot plot = new CombinedRangeCategoryPlot();
		
		plot.setDataset(0,dataset);
		for (int i = 0; i < chartEntries.size(); i++) {
			double allowedAmount;
			String entry = chartEntries.get(i).getCategory();
			
			if (allowedAmounts.get(entry)==null) 
				allowedAmount=0.0;
			else
				allowedAmount = allowedAmounts.get(entry)*daysSinceStart;
			dataset.addValue(allowedAmount, 
					entry +" allowed","");
			dataset.addValue(Math.abs(chartEntries.get(i).getAmount()), 
					entry, "");

			dataset.addValue(0, Integer.toString(i), "");
		}

//		DefaultCategoryDataset allowedAmountsDataset = new DefaultCategoryDataset();

//		for (int i = 0; i < allowedAmounts.size(); i++) {
//			allowedAmountsDataset.addValue(allowedAmounts.get(i).amount*daysSinceStart, 
//					allowedAmounts.get(i).category, "");
//		}
//		plot.setDataset(1, allowedAmountsDataset);
		
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
}
