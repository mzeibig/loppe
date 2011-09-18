package de.zeiban.loppe;

import java.math.BigDecimal;
import java.sql.Connection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

final class ExitSelectionAdapter extends SelectionAdapter {

	private Shell shell;
	private Connection connection;

	public ExitSelectionAdapter(Shell parent, Connection connection) {
		this.shell = parent;
		this.connection = connection;
	}

	@Override
	public void widgetSelected(SelectionEvent arg0) {
		try {this.connection.close();} catch (Exception ignore) {} 
		shell.close();
//		final MessageBox messageBox = new MessageBox(this.shell, SWT.ICON_QUESTION|SWT.OK);
//		messageBox.setMessage("Kindergarten-Prozent: "+loppeShare);
//		messageBox.open();
	}
}
