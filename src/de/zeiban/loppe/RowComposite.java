package de.zeiban.loppe;

import java.math.BigDecimal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import de.zeiban.loppe.data.Row;
import de.zeiban.loppe.verifyer.MoneyVerifyer;
import de.zeiban.loppe.verifyer.NumberVerifyer;

public class RowComposite extends Composite implements Row {
	private final Text verkaeuferNummer;
	private final Text preis;

	public RowComposite(final Shell shell, final Composite parent, final KeyListener keyListener) {
		super(parent, SWT.NONE);
		final RowLayout compositeLayout = new RowLayout(SWT.HORIZONTAL);
		compositeLayout.spacing = 15;
		this.setLayout(compositeLayout);
		verkaeuferNummer = new Text(this, SWT.BORDER);
		verkaeuferNummer.setLayoutData(new RowData(200, SWT.DEFAULT));
		verkaeuferNummer.setToolTipText("Nummer");
		verkaeuferNummer.addVerifyListener(new NumberVerifyer());
		//verkaeuferNummer.addFocusListener(new BlackListCheckFocusAdapter(shell));
		preis = new Text(this, SWT.BORDER);
		preis.setLayoutData(new RowData(180, SWT.DEFAULT));
		preis.addKeyListener(keyListener);
		preis.setToolTipText("Preis in €");
		preis.addVerifyListener(new MoneyVerifyer());
		verkaeuferNummer.setFocus();
	}
	
	/**
	 * @see de.zeiban.loppe.Row#isEmpty()
	 */
	public boolean isEmpty() {
		return preis.getText().length() == 0 || verkaeuferNummer.getText().length() == 0;
	}
	
	/**
	 * @see de.zeiban.loppe.Row#getNummerAsString()
	 */
	public String getNummerAsString() {
		return this.verkaeuferNummer.getText();
	}
	
	/**
	 * @see de.zeiban.loppe.Row#getVerkaeuferNummer()
	 */
	public int getVerkaeuferNummer() {
		return Integer.parseInt(verkaeuferNummer.getText());
	}
	
	/**
	 * @see de.zeiban.loppe.Row#getPreisAsString()
	 */
	public String getPreisAsString() {
		return this.preis.getText();
	}
	
	/**
	 * @see de.zeiban.loppe.Row#getPreis()
	 */
	public BigDecimal getPreis() {
		final String preisText = this.preis.getText();
		if (preisText.length() == 0) {
			return BigDecimal.ZERO;
		} else {
			return new BigDecimal(preisText);
		}
	}
}
