/**
 * 
 */
package de.zeiban.loppe;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

final class BlackListCheckFocusAdapter extends FocusAdapter {
	private final Shell shell;
	
	public BlackListCheckFocusAdapter(final Shell shell) {
		this.shell = shell;
	}

	@Override
	public void focusLost(final FocusEvent e) {
		final Text source = (Text) e.getSource();
		try {
			final Integer verkaeuferNummer = Integer.valueOf(source.getText());
			if (verkaeuferNummer==3) {
				source.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_RED));
				source.setFocus();
			} else {
				source.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_WHITE));
			}
		} catch (NumberFormatException nfe) {
			
		}
	}
}
