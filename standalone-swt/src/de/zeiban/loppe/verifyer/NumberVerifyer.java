/**
 * 
 */
package de.zeiban.loppe.verifyer;

import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Text;

public class NumberVerifyer implements VerifyListener {

	// @Override
	public void verifyText(VerifyEvent event) {
		event.doit = false;
		char myChar = event.character;
		Text textfield = (Text) event.widget;
		String oldtext = textfield.getText();
		//System.out.println(oldtext+myChar);
		event.doit = (Character.isDigit(myChar) || myChar == '\b');
	}
}