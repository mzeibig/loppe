package de.zeiban.loppe;

import java.math.BigDecimal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

final class ExitSelectionAdapter extends SelectionAdapter {

	private Shell shell;

	public ExitSelectionAdapter(Shell parent) {
		this.shell = parent;
	}

	@Override
	public void widgetSelected(SelectionEvent arg0) {
		shell.close();
//		final MessageBox messageBox = new MessageBox(this.shell, SWT.ICON_QUESTION|SWT.OK);
//		messageBox.setMessage("Kindergarten-Prozent: "+loppeShare);
//		messageBox.open();


	}

}
