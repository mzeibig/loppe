/**
 * 
 */
package de.zeiban.loppe;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.zeiban.loppe.dbcore.DbTemplate;
import de.zeiban.loppe.dbcore.ResultCallbackWithReturn;

class KundeCountProvider {
	private final DbTemplate dbTemplate;

	public KundeCountProvider(final Connection connection) {
		dbTemplate = new DbTemplate(connection);
	}

	String getNextKundeCount() {
		return (String) dbTemplate.selectObject("select max(kunde) from kauf", new ResultCallbackWithReturn() {
			public Object doWithResultset(final ResultSet rs) throws SQLException {
				int count;
				if (!rs.next())
					count = 1;
				else
					count = rs.getInt(1);
				return String.valueOf(count + 1);
				
			}
			
		});
	}
}
