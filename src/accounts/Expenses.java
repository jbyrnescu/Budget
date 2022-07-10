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
import org.jfree.chart.renderer.xy.DefaultXYItemRenderer;
import org.jfree.data.xy.DefaultXYDataset;

public class Expenses extends BigViewAccount {

	@Override
	public void loadTransactionsFromDatabase(Connection c) throws SQLException {
		
		String query = "select * from BigTXView where XclFrmCshFlw is null "
				+ "and amount < 0;"
				+ "and BudgetCat not like %ncome%";
		ResultSet rs = c.createStatement().executeQuery(query);
		while(rs.next()) {
			BigViewTransaction bvt = new BigViewTransaction();
			bvt.loadTransactionFromDatabase(rs);
			addTransaction(bvt);
		}
	}
	
	public void drawCashFlowGraph() {
		// put numbers in double[][] array
		int numTransactions = getNumberTransactions();
		double[][] array = new double[2][numTransactions];
		
		float cumulativeAmount = 0.0f;
		
		for (int i = 0; i < getNumberTransactions(); i++) {
			array[0][i] = getTransaction(i).getTransactionDate().getTime();
			cumulativeAmount += Math.abs(getTransaction(i).getAmount());
			array[1][i] = cumulativeAmount;

		}
		
		DefaultXYDataset dataset = new DefaultXYDataset();
		dataset.addSeries("Cumulative Amount", array);
		
		JFreeChart chart = ChartFactory.createXYLineChart("Cumulative Cash Flow", 
				"Transaction Date", "Cumulative Amount", dataset);
		
		ValueAxis domainAxis = new DateAxis("Transaction Date");
		ValueAxis rangeAxis = new NumberAxis("Cumulative Amount");
		XYPlot xyPlot = chart.getXYPlot();
		xyPlot.setDomainAxis(domainAxis);
		xyPlot.setRangeAxis(rangeAxis);
		
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
