/**
 * 
 */
package de.zeiban.loppe.verifyer;

import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Display;

public class MoneyVerifyer implements VerifyListener {
//        @Override
            public void verifyText(VerifyEvent event) {
                event.doit = false;
                char myChar = event.character;
                boolean b = Character.isDigit(myChar) || myChar == '\b' || myChar == '.' || myChar == '-';
                if (!b) {
                	Display.getCurrent().beep();
                }
				event.doit = b;
            }
    }