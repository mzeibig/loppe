package de.zeiban.loppe;


import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class AboutDialog extends Dialog {
	Object result;

	public AboutDialog(Shell parent) {
		super(parent, SWT.SHEET|SWT.DIALOG_TRIM);
		setText("About");
	}

	public Object open() {
		Shell shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shell.setText(getText());
		createContents(shell);
		shell.setSize(400, 300);
		//shell.pack();
		shell.open();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		return result;
	}

	private void createContents(Shell shell) {
		shell.setLayout(new GridLayout(1, true));
		createLine(shell, "Flohmarkt 2.0");
		createLine(shell, "");
		createLine(shell, "GNU General Public License (GPL)");
		createLine(shell, "https://sourceforge.net/projects/loppe/");
		createLine(shell, "");
		createLine(shell, "Entwickler:");
		createLine(shell, "Mirko Zeibig");
		createLine(shell, "Heike Andrae");
	}

	private void createLine(Shell shell, String text) {
		Label name = new Label(shell, SWT.CENTER);
		name.setText(text);
		name.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}
}
