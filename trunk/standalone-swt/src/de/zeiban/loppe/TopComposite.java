package de.zeiban.loppe;

import java.math.BigDecimal;
import java.sql.Connection;
import java.text.NumberFormat;

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
public class TopComposite extends Composite {

	private final Label summeGesamtInfo;
	private final Label kundeCountInfo;
	private final Label zwischensumme;
	private final Label letzterKunde;

	public TopComposite(final Composite parent, final Connection connection) {
		super(parent, SWT.BORDER);
		this.setLayout(new RowLayout(SWT.HORIZONTAL));
		summeGesamtInfo = createLabelValueComposite(this, "Summe:", "0.00");
		summeGesamtInfo.setText(new SummeGesamtInfoProvider(connection).getSumme());
		kundeCountInfo = createLabelValueComposite(this, "Anzahl Kunden:", "0");
		kundeCountInfo.setText(new KundeCountProvider(connection).getNextKundeCount());
		zwischensumme = createLabelValueComposite(this, "Zwischensumme:", "0.00");
		letzterKunde = createLabelValueComposite(this, "letzter Kunde:", "0.00");
	}
	
	public String getKundeCountAsString() {
		return kundeCountInfo.getText();
	}
	
	public void setKundeCount(String count) {
		this.kundeCountInfo.setText(count);
	}

	public String getSummeGesamtAsString() {
		return this.summeGesamtInfo.getText();
	}
	
	@Deprecated
	public Label getSummeLabel() {
		return this.summeGesamtInfo;
	}

	public void setSummeGesamt(String summe) {
		this.summeGesamtInfo.setText(summe);
	}
	
	public void setZwischensumme(BigDecimal summe) {
		this.zwischensumme.setText(NumberFormat.getCurrencyInstance().format(summe));
	}
	
	public void setLetzterKunde(BigDecimal summe) {
		letzterKunde.setText(NumberFormat.getCurrencyInstance().format(summe));
	}

	private Label createLabelValueComposite(final Composite parent, String labelText, String initValue) {
		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new RowLayout(SWT.HORIZONTAL));
		final Label label = new Label(composite, SWT.NONE);
		final RowData rowData = new RowData();
		rowData.width = 110;
		label.setLayoutData(rowData);
		label.setText(labelText);
		final Label cnt = new Label(composite, SWT.CENTER);
		cnt.setLayoutData(new RowData(60, SWT.DEFAULT));
		cnt.setText(initValue);
		return cnt;
	}
}
