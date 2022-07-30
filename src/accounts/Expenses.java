package accounts;

import java.awt.Dimension;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;

import finance.Logger;

public class Expenses extends BigViewAccount {
	
	String beginDate, endDate;
	
	public Expenses(Connection connection) {
		this.connection = connection;
	}

	@Override
	public void loadTransactionsFromDatabase(Connection c, String beginDate, String endDate) throws SQLException {
		this.beginDate = beginDate;
		this.endDate = endDate;
		String and1 ="", and2 = "";
		String endQuote = "\"";
		if (beginDate == null) {
			beginDate = "";
		} else and1 = " and transactionDate >= \"" +beginDate+ endQuote;
		if (endDate == null)
		{ 
			endDate = ""; 
		} else and2 = " and transactionDate <= \"" + endDate + endQuote;
		
		if (c == null)
			c = connection;
		
		String query = "select * from BigTXView where XclFrmCshFlw is null "
				+ and1
				+ and2
				+ "and amount < 0;"
				+ "and BudgetCat not like %ncome%";
		ResultSet rs = c.createStatement().executeQuery(query);
		while(rs.next()) {
			BigViewTransaction bvt = new BigViewTransaction();
			bvt.loadTransactionFromDatabase(rs);
			addTransaction(bvt);
		}
	}
	
	public void drawCashFlowGraph() throws SQLException {

		Incomes incomes = new Incomes();
		incomes.loadTransactionsFromDatabase(connection, beginDate, endDate);
		XYDataset incomeDataset = incomes.getXYDataset();
		
		MandatoryTransactions mt = new MandatoryTransactions();
		mt.loadTransactionsFromDatabase(connection, beginDate, endDate);
		XYDataset mtDataset = mt.getXYDataset();
		
		// put numbers in double[][] array
		int numTransactions = getNumberTransactions();
		double[][] array = new double[2][numTransactions];
		
		float cumulativeAmount = 0.0f;
		double sumXY = 0.0, sumXSquared = 0.0, sumY = 0.0, sumX = 0.0;
		
		double[][] testArray = { {1,2,3,4,5,6,7},
				{1.5, 3.8, 6.7, 9.0, 11.2, 13.6, 16} };
		
		for (int i = 0; i < getNumberTransactions(); i++) {

			
			// for testing purposes
			//		for (int i = 0; i < 7; i++) {
//			double x = testArray[0][i];
//			double y = testArray[1][i];
//
			double x = getTransaction(i).getTransactionDate().getTime();
			double y = Math.abs(getTransaction(i).getAmount());
			
			array[0][i] = x;
			if (getTransaction(i).getAmount() < 0)
				cumulativeAmount += y;
			array[1][i] = cumulativeAmount;
		}
		

//double m = calculateSlope(sumX, sumY, sumXY, sumXSquared, 7);


		DefaultXYDataset dataset = new DefaultXYDataset();
		dataset.addSeries("Cumulative Amount", array);
		
		JFreeChart chart = ChartFactory.createXYLineChart("Cumulative Cash Flow", 
				"Transaction Date", "Cumulative Amount", dataset);
		
		ValueAxis domainAxis = new DateAxis("Transaction Date");
		ValueAxis rangeAxis = new NumberAxis("Cumulative Amount");
		XYPlot xyPlot = chart.getXYPlot();
		xyPlot.setDomainAxis(domainAxis);
		xyPlot.setRangeAxis(rangeAxis);
		XYDotRenderer dotR = new XYDotRenderer();
		
		// for cumulative amount numbers and trendline
		double m = calculateSlope(numTransactions, array);
		double b = calculateYIntercept(m,numTransactions, array);
		xyPlot.setDataset(1,dataset);
		double[][] trendline1Array = getTrendlineXY(numTransactions, array, m, b);
		DefaultXYDataset trendline1Dataset = new DefaultXYDataset();
		trendline1Dataset.addSeries("Trendline Cum Amnt m=" + m*1000*60*60*24,trendline1Array);
		xyPlot.setDataset(6,trendline1Dataset);
		xyPlot.setRenderer(1,dotR);
		
		// for income dataset and trendline
		xyPlot.setDataset(2,incomeDataset);
		xyPlot.setDataset(3,incomeDataset);
		xyPlot.setRenderer(3,dotR);
		double[][] incomePointSet = incomes.getArrayOfPoints();
		m = calculateSlope(incomes.getNumberTransactions(), incomePointSet);
		b = calculateYIntercept(m, incomes.getNumberTransactions(), incomePointSet);
		double[][] incomeTrendLine = getTrendlineXY(incomes.getNumberTransactions(),
						incomePointSet,
						m, b);
		DefaultXYDataset incomeTrendLineDataset = new DefaultXYDataset();
		incomeTrendLineDataset.addSeries("Trendline Income m=" + m*1000*60*60*24, incomeTrendLine);
		xyPlot.setDataset(7,incomeTrendLineDataset);
		
		// for Mandatory Expenses dataset
		xyPlot.setDataset(4,mtDataset);
		xyPlot.setDataset(5,mtDataset);
		dotR.setDotHeight(5); dotR.setDotWidth(5);

		xyPlot.setRenderer(5,dotR);

		
		JPanel jPanel = new ChartPanel(chart);
		jPanel.setSize(560,367);
//		RefineryUtilities.centerFrameOnScreen(jPanel);
		jPanel.setVisible(true);
		JFrame frame = new JFrame("Spending Category Amounts");
		frame.setLocationRelativeTo(null);
		frame.setSize(new Dimension(400,400));
		frame.add(jPanel);
		frame.setVisible(true);
		
		Logger.out.println("graph complete");
		
	}

	private double[][] getTrendlineXY(int numTransactions, double[][] array, double m, double b) {
		double xMin = array[0][0];
		double xMax = array[0][numTransactions-1];
		
		double x1 = array[0][0];
		double x2 = array[0][numTransactions-1];
		double yMin = m*x1+b;
		double yMax = m*x2+b;
		
		double[][] trendline1Array = { {xMin,xMax}, {yMin,yMax} };
		return trendline1Array;
	}

	private double calculateYIntercept( 
			double m, 
			int n, 
			double[][] array) {
		double sumX = 0.0;
		double sumY = 0.0;

		for (int i = 0; i < n; i++) {
			sumY += array[1][i];
			sumX += array[0][i];
		}
		
		double yBar = sumY/n;
		double xBar = sumX/n;
				
		double b = yBar-m*xBar;
		return(b);
	}
	
	private double calculateYInterceptWithFirstPoint(double x, double y, double m) {
		double b = y - m*x;
		return(b);
	}

	private double calculateSlope(int n, double[][] array) {
		double m, sumX=0.0, sumY=0.0;
		
		for (int i = 0; i < n; i++) {
			sumX += array[0][i];
			sumY += array[1][i];
		}
		
		double xDiff = 0.0, sumXDiff = 0.0, yDiff = 0.0, sumDiff = 0.0;
		double sumXDiffSquared = 0.0;
		for (int i = 0; i < n; i++) {
			xDiff = array[0][i] - sumX/n;
			yDiff = array[1][i] - sumY/n;
			sumDiff += xDiff*yDiff; 
			sumXDiffSquared += xDiff*xDiff;
		}
		
		m = sumDiff/sumXDiffSquared;
		
		return(m);
	}

}
