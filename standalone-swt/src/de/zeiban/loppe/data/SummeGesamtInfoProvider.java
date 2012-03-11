/**
 * 
 */
package de.zeiban.loppe.data;

import java.math.BigDecimal;
import java.sql.Connection;
import java.text.NumberFormat;

import de.zeiban.loppe.dbcore.DbTemplate;

public class SummeGesamtInfoProvider implements Summenprovider {

	private final Connection connection;

	public SummeGesamtInfoProvider(Connection connection) {
		this.connection = connection;
	}

	public String getSumme() {
		BigDecimal gessum = new DbTemplate(connection).selectObject("select sum(preis) from kauf");
		NumberFormat f = java.text.NumberFormat.getCurrencyInstance();
		return f.format(gessum == null ? 0.0 : gessum);
	}
}
