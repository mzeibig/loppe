package de.zeiban.loppe;

import java.math.BigDecimal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

final class InfoSelectionAdapter extends SelectionAdapter {

	private Shell shell;
	private BigDecimal loppeShare;

	public InfoSelectionAdapter(Shell parent, BigDecimal loppeShare) {
		this.shell = parent;
		this.loppeShare = loppeShare;
	}

	@Override
	public void widgetSelected(SelectionEvent arg0) {
		final MessageBox messageBox = new MessageBox(this.shell, SWT.ICON_QUESTION|SWT.OK);
		messageBox.setMessage("Kindergarten-Prozent: "+loppeShare);
		messageBox.open();


	}

}
