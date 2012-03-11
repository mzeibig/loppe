package de.zeiban.loppe.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import de.zeiban.loppe.dbcore.DbTemplate;
import de.zeiban.loppe.dbcore.ParamProvider;

public class DataSaver {
	final Connection connection;

	public DataSaver(Connection connection) {
		this.connection = connection;
	}

	public void saveValues(List<Row> rows, final String kundeCount, final int inst) {
		for (final Row row : rows) {
			if (row.isEmpty())
				continue;
			new DbTemplate(connection).execute("insert into kauf (inst, kunde, nummer, preis) values(?, ?, ?, ?)", new ParamProvider() {
				public void fillParams(PreparedStatement stmt) throws SQLException {
					stmt.setInt(1, inst);
					stmt.setInt(2, Integer.parseInt(kundeCount));
					stmt.setInt(3, row.getVerkaeuferNummer());
					stmt.setBigDecimal(4, row.getPreis());
				}
			});
		}		
	}
}
