package de.zeiban.loppe;

import java.sql.Connection;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
	}
}
