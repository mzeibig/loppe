package de.zeiban.loppe.verifyer;

import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Text;

public class NumberAndBlacklistVerifyer extends NumberVerifyer {

	private List<String> blacklist = Arrays.asList("13", "24");
	
	@Override
	public void verifyText(VerifyEvent event) {
		super.verifyText(event);
		char typedInChar = event.character;
		Text textfield = (Text) event.widget;
		String oldtext = textfield.getText();
		boolean isBlacklisted = blacklist.contains(oldtext + typedInChar);
		if (isBlacklisted) {
			textfield.setBackground(textfield.getDisplay().getSystemColor(SWT.COLOR_RED));
		} else {
			textfield.setBackground(textfield.getDisplay().getSystemColor(SWT.COLOR_WHITE));			
		}
		event.doit = event.doit && !isBlacklisted;
	}	
}
