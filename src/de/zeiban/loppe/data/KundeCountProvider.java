/**
 * 
 */
package de.zeiban.loppe.data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.zeiban.loppe.dbcore.DbTemplate;
import de.zeiban.loppe.dbcore.ResultCallbackWithReturn;

public class KundeCountProvider {
	private final DbTemplate dbTemplate;

	public KundeCountProvider(final Connection connection) {
		dbTemplate = new DbTemplate(connection);
	}

	public int getNextKundeCount() {
		return dbTemplate.selectObject("select max(kunde) from kauf", new ResultCallbackWithReturn<Integer>() {
			public Integer doWithResultset(final ResultSet rs) throws SQLException {
				int count;
				if (!rs.next())
					count = 1;
				else
					count = rs.getInt(1);
				return count + 1;
			}
		});
	}
}
