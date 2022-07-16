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
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.renderer.xy.DefaultXYItemRenderer;
import org.jfree.chart.renderer.xy.SamplingXYLineRenderer;
import org.jfree.chart.renderer.xy.VectorRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;

public class Expenses extends BigViewAccount {
	
	Connection c = null;

	@Override
	public void loadTransactionsFromDatabase(Connection c) throws SQLException {
		this.c = c;
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
	
	public void drawCashFlowGraph() throws SQLException {

		Incomes incomes = new Incomes();
		incomes.loadTransactionsFromDatabase(c);
		XYDataset incomeDataset = incomes.getXYDataset();
		// put numbers in double[][] array
		int numTransactions = getNumberTransactions();
		double[][] array = new double[2][numTransactions];
		
		float cumulativeAmount = 0.0f;
		
		for (int i = 0; i < getNumberTransactions(); i++) {
			array[0][i] = getTransaction(i).getTransactionDate().getTime();
			if (getTransaction(i).getAmount() < 0)
				cumulativeAmount += Math.abs(getTransaction(i).getAmount());
			array[1][i] = cumulativeAmount;

		}
		
		DefaultXYDataset dataset = new DefaultXYDataset();
		dataset.addSeries("Cumulative Amount", array);
//		dataset.addSeries("Cumulative Amount 2", array);
		
		JFreeChart chart = ChartFactory.createXYLineChart("Cumulative Cash Flow", 
				"Transaction Date", "Cumulative Amount", dataset);
		
		ValueAxis domainAxis = new DateAxis("Transaction Date");
		ValueAxis rangeAxis = new NumberAxis("Cumulative Amount");
		XYPlot xyPlot = chart.getXYPlot();
		xyPlot.setDomainAxis(domainAxis);
		xyPlot.setRangeAxis(rangeAxis);
		
		xyPlot.setDataset(1,dataset);
		xyPlot.setDataset(2,incomeDataset);
		xyPlot.setDataset(3,incomeDataset);
//		DefaultXYDataset xySet = (DefaultXYDataset) xyPlot.getDataset(1);

		
//		AbstractXYItemRenderer dotR = new XYBarRenderer();
//		
		XYDotRenderer dotR = new XYDotRenderer();
		dotR.setDotHeight(5); dotR.setDotWidth(5);
		xyPlot.setRenderer(1,dotR);
		xyPlot.setRenderer(3,dotR);
		
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
