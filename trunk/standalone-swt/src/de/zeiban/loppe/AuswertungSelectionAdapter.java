/**
 * 
 */
package de.zeiban.loppe;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Shell;

import de.zeiban.loppe.ResultsDialog.Result;

final class AuswertungSelectionAdapter extends SelectionAdapter {
	private final Shell shell;
	private final Connection connection;
	
	public AuswertungSelectionAdapter(Shell shell,
			Connection connection) {
		this.shell = shell;
		this.connection = connection;
	}

	@Override
	public void widgetSelected(final SelectionEvent e) {
		//                System.out.println("BUTTON");
		final List<ResultsDialog.Result> resultList = new ArrayList<Result>();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.createStatement();
			rs = stmt.executeQuery("select nummer, sum(preis) from kauf group by nummer order by nummer");
			while (rs.next()) {
				final Result res = new Result();
				res.nummer = rs.getInt("nummer");
				final BigDecimal summe = rs.getBigDecimal(2);
				res.summe = summe;
				res.proz25 = summe.multiply(new BigDecimal(0.25));
				res.proz75 = summe.multiply(new BigDecimal(0.75));
				resultList.add(res);
			}
		} catch (final SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			try {rs.close();} catch (final Exception ignore) {}
			try {stmt.close();} catch (final Exception ignore) {}
		}
		new ResultsDialog(shell, SWT.SHELL_TRIM).open(resultList);
	}
}