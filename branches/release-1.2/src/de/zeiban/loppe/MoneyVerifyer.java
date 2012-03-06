/**
 * 
 */
package de.zeiban.loppe;

import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;

final class MoneyVerifyer implements VerifyListener {
//        @Override
            public void verifyText(VerifyEvent event) {
                event.doit = false;
                char myChar = event.character;
                event.doit = Character.isDigit(myChar) || myChar == '\b' || myChar == '.' || myChar == '-';
            }
    }