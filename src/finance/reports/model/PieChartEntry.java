package finance.reports.model;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PieChartEntry {
	private String category;
	private float amount;
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public float getAmount() {
		return amount;
	}
	public void setAmount(float amount) {
		this.amount = amount;
	}
	public void loadFromResultSet(ResultSet rs) throws SQLException {
		category = rs.getString("budgetCat");
		amount = rs.getFloat("amount");
	}
	
}
