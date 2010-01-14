/**
 * 
 */
package de.zeiban.loppe;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

class KundenNummerProvider {
	Connection connection;

	public KundenNummerProvider(Connection connection) {
		this.connection = connection;
	}

	String getNextKundeCount() {
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery("select max(kunde) from kauf");
			int count;
			if (!rs.next())
				count = 1;
			else
				count = rs.getInt(1);
			return String.valueOf(count + 1);

		} catch (final SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		} finally {
			try {
				stmt.close();
			} catch (final Exception ignore) {
			}
		}
	}
}