/**
 * 
 */
package de.zeiban.loppe.verifyer;

import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;

public class MoneyVerifyer implements VerifyListener {
//        @Override
            public void verifyText(VerifyEvent event) {
                event.doit = false;
                char myChar = event.character;
                event.doit = Character.isDigit(myChar) || myChar == '\b' || myChar == '.' || myChar == '-';
            }
    }