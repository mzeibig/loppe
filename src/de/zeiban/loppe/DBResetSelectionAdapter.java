/**
 * 
 */
package de.zeiban.loppe;

import java.sql.Connection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import de.zeiban.loppe.dbcore.DbTemplate;

final class DBResetSelectionAdapter extends SelectionAdapter {
	private final Shell shell;
	private final DbTemplate dbTemplate;

	public DBResetSelectionAdapter(final Shell shell, final Connection connection) {
		this.shell = shell;
		dbTemplate = new DbTemplate(connection);
	}

	@Override
	public void widgetSelected(final SelectionEvent e) {
		final MessageBox messageBox = new MessageBox(shell, SWT.ICON_QUESTION|SWT.YES|SWT.NO);
		messageBox.setMessage("Datenbank jetzt zurücksetzen ?\n!!!Achtung: Alle Daten werden gelöscht !!!");
		if (messageBox.open() == SWT.YES) {   
			final MessageBox confirmMessageBox = new MessageBox(shell, SWT.ICON_QUESTION|SWT.YES|SWT.NO);
			confirmMessageBox.setMessage("Wirklich alle Daten aus der Datenbank löschen ?");
			if (confirmMessageBox.open() == SWT.YES) {
				dbTemplate.execute("delete from kauf");
			}

		}
	}
}
