/**
 * 
 */
package de.zeiban.loppe;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import de.zeiban.loppe.dbcore.DbTemplate;
import de.zeiban.loppe.dbcore.ParamProvider;
import de.zeiban.loppe.dbcore.ResultCallback;

final class BlackListCheckFocusAdapter extends FocusAdapter {
	private final Shell shell;
	private DbTemplate dbTemplate;
	
	public BlackListCheckFocusAdapter(final Shell shell, final Connection connection) {
		this.shell = shell;
		this.dbTemplate = new DbTemplate(connection);
	}

	@Override
	public void focusLost(final FocusEvent e) {
//		final Text source = (Text) e.getSource();
//		try {
//			final Integer nummer = Integer.valueOf(source.getText());
//
//			dbTemplate.select("select 1 from BLACKLIST where NUMMER = ?", 
//					new ParamProvider() {
//				public void fillParams(PreparedStatement stmt) throws SQLException {
//					stmt.setInt(1, nummer.intValue());
//				}
//			}, 
//			new ResultCallback() {
//				public void doWithResultset(ResultSet rs) throws SQLException {
//					if (rs.next()) {
//						source.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_RED));
//					} else {
//						source.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
//					}
//				}
//			});
//		} catch (NumberFormatException nfe) {
//			
//		}
	}
}