package de.zeiban.loppe;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

class DataSaver {
	final Connection connection;

	public DataSaver(Connection connection) {
		this.connection = connection;
	}

	void saveValues(List<Composite> rows, String kundeCount, int inst) {
		PreparedStatement stmt = null;
		try {
			stmt = connection.prepareStatement("insert into kauf values(?, ?, ?, ?)");
			for (final Composite row : rows) {
				final String textPreis = ((Text) row.getChildren()[1]).getText();
				final String textNummer = ((Text) row.getChildren()[0]).getText();
				final String textKunde = kundeCount;
				if (textPreis.length() == 0 || textNummer.length() == 0)
					continue;
				System.out.println(textKunde + ":" + textNummer + ":" + textPreis);
				stmt.setInt(1, inst);
				stmt.setInt(2, Integer.parseInt(textKunde));
				stmt.setInt(3, Integer.parseInt(textNummer));
				stmt.setBigDecimal(4, new BigDecimal(textPreis));
				stmt.execute();
			}
		} catch (final SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				stmt.close();
			} catch (final Exception ignore) {
			}
		}
	}
}