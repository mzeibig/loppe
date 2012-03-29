package de.zeiban.loppe;

import java.math.BigDecimal;
import java.sql.Connection;
import java.text.NumberFormat;
import java.util.Locale;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import de.zeiban.loppe.data.KundeCountProvider;
import de.zeiban.loppe.data.SummeGesamtInfoProvider;

/**
 * Dieses TopComposite ist sowas wie die Statuszeile am oberen Ende.
 * 
 * @author mirkoz
 */
public class TopComposite extends Composite implements TopInfos {

	private final Label summeGesamtInfo;
	private final Label kundeCountInfo;
	private final Label zwischensumme;
	private final Label letzterKunde;

	public TopComposite(final Composite parent, final Connection connection) {
		super(parent, SWT.BORDER);
		this.setLayout(new RowLayout(SWT.HORIZONTAL));
		summeGesamtInfo = createLabelValueComposite(this, "Summe:", "0.00", 50, 60);
		setSummeGesamt(new SummeGesamtInfoProvider(connection).getSumme());
		kundeCountInfo = createLabelValueComposite(this, "Anzahl Kunden:", "0", 100, 30);
		setKundeCount(new KundeCountProvider(connection).getNextKundeCount());
		zwischensumme = createLabelValueComposite(this, "Zwischensumme:", "0.00", 110, 50);
		setZwischensumme(new BigDecimal(0));
		letzterKunde = createLabelValueComposite(this, "letzter Kunde:", "0.00", 90, 50);
		setLetzterKunde(new BigDecimal(0));
	}
	
	public String getKundeCountAsString() {
		return kundeCountInfo.getText();
	}
	
	final public void setKundeCount(final int count) {
		this.kundeCountInfo.setText(String.valueOf(count));
	}

	public String getSummeGesamtAsString() {
		return this.summeGesamtInfo.getText();
	}
	
	public void setSummeGesamt(final String summe) {
		this.summeGesamtInfo.setText(summe);
	}
	
	final public void setSummeGesamt(final BigDecimal summe) {
		this.summeGesamtInfo.setText(NumberFormat.getCurrencyInstance(Locale.GERMANY).format(summe));
	}
	
	final public void setZwischensumme(final BigDecimal summe) {
		this.zwischensumme.setText(NumberFormat.getCurrencyInstance(Locale.GERMANY).format(summe));
	}
	
	final public void setLetzterKunde(final BigDecimal summe) {
		letzterKunde.setText(NumberFormat.getCurrencyInstance(Locale.GERMANY).format(summe));
	}

	private Label createLabelValueComposite(final Composite parent, final String labelText, final String initValue, final int width, final int widthValue) {
		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new RowLayout(SWT.HORIZONTAL));
		final Label label = new Label(composite, SWT.NONE);
		label.setLayoutData(new RowData(width, SWT.DEFAULT));
		label.setText(labelText);
		final Label cnt = new Label(composite, SWT.CENTER);
		cnt.setLayoutData(new RowData(widthValue, SWT.DEFAULT));
		cnt.setText(initValue);
		return cnt;
	}
}
