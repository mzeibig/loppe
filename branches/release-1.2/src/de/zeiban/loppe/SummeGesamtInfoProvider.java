/**
 * 
 */
package de.zeiban.loppe;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;

class SummeGesamtInfoProvider implements Summenprovider {

	private final Connection connection;

	public SummeGesamtInfoProvider(Connection connection) {
		this.connection = connection;
	}

	public String getSumme() {
		Statement stmt = null; 
		ResultSet rs = null;
		NumberFormat f = java.text.NumberFormat.getCurrencyInstance();
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery("select sum(preis) from kauf");
			rs.next();
			final BigDecimal gessum = rs.getBigDecimal(1);
			return f.format(gessum == null ? 0.0 : gessum);
		} catch (final SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "0.0";
		} finally {
			try {stmt.close();} catch (final Exception ignore) {}
		} 
	}
}
