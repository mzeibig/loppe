/**
 * 
 */
package de.zeiban.loppe;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Shell;

import de.zeiban.loppe.ResultsDialog.Result;
import de.zeiban.loppe.dbcore.DbOperations;
import de.zeiban.loppe.dbcore.DbTemplate;
import de.zeiban.loppe.dbcore.ResultCallback;

final class AuswertungSelectionAdapter extends SelectionAdapter {
	private final Shell shell;
	private DbOperations dbTemplate;
	private BigDecimal loppeShare;
	
	public AuswertungSelectionAdapter(final Shell shell, final Connection connection, final BigDecimal loppeShare) {
		this.shell = shell;
		this.dbTemplate = new DbTemplate(connection);
		this.loppeShare = loppeShare;
	}

	@Override
	public void widgetSelected(final SelectionEvent e) {
		final List<ResultsDialog.Result> resultList = new ArrayList<Result>();
		dbTemplate.execute("select nummer, sum(preis) from kauf group by nummer order by nummer",  
				new ResultCallback() {
					public void doWithResultset(final ResultSet rs) throws SQLException {
						while (rs.next()) {
							final Result res = new Result();
							res.nummer = rs.getInt("nummer");
							final BigDecimal summe = rs.getBigDecimal(2);
							res.summe = summe;
							res.prozKiga = summe.multiply(loppeShare);
							res.prozSeller = summe.subtract(res.prozKiga);
							resultList.add(res);
						}
					}
				});
		new ResultsDialog(shell, SWT.SHELL_TRIM).open(resultList);
	}
}