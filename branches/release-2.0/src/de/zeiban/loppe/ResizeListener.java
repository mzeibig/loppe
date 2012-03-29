/**
 * 
 */
package de.zeiban.loppe;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

final class ResizeListener implements Listener {
	private final Display display;
	private final ScrolledComposite sc;
	private Image oldImage;
    private Color meinBlau;

	ResizeListener(Display display, ScrolledComposite sc) {
		this.display = display;
		this.sc = sc;
		meinBlau = new Color(display, 51,102,255);
	}

	public void handleEvent (final Event event) {
	    final Rectangle rect = sc.getClientArea ();
	    final Image newImage = new Image (display, Math.max (1, rect.width), 1);  
	    final GC gc = new GC (newImage);
	    gc.setForeground (display.getSystemColor (SWT.COLOR_WHITE));
	    gc.setBackground (meinBlau);
	    gc.fillGradientRectangle (rect.x, rect.y, rect.width, 1, false);
	    gc.dispose ();
	    sc.setBackgroundImage (newImage);
	    if (oldImage != null) oldImage.dispose ();
	    oldImage = newImage;
	}
	
	public void dispose() {
        if (oldImage != null) oldImage.dispose ();
        meinBlau.dispose();
	}
}