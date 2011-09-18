package de.zeiban.loppe;

import java.sql.Connection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class TopComposite extends Composite {

	protected Label summeGesamtInfo;
	protected Label kundeCountInfo;
	protected Label zwischensumme;

	public TopComposite(final Composite parent, final Connection connection) {
		super(parent, SWT.BORDER);
		final RowLayout topCompositeLayout = new RowLayout(SWT.HORIZONTAL);
		this.setLayout(topCompositeLayout);
		summeGesamtInfo = createLabelValueComposite(this, "Summe:", "0.00");
		summeGesamtInfo.setText(new SummeGesamtInfoProvider(connection).getSumme());
		kundeCountInfo = createLabelValueComposite(this, "Anzahl Kunden:", "0");
		kundeCountInfo.setText(new KundeCountProvider(connection).getNextKundeCount());
		zwischensumme = createLabelValueComposite(this, "Zwischensumme:", "0.00");
	}

	private Label createLabelValueComposite(final Composite parent, String labelText, String initValue) {
		final Composite composite = new Composite(parent, SWT.NONE);
		final RowLayout compositeLayout = new RowLayout(SWT.HORIZONTAL);
		composite.setLayout(compositeLayout);
		final Label label = new Label(composite, SWT.NONE);
		final RowData rowData = new RowData();
		rowData.width = 100;
		label.setLayoutData(rowData);
		label.setText(labelText);
		final Label cnt = new Label(composite, SWT.CENTER);
		final RowData rowDataCnt = new RowData();
		rowDataCnt.width = 80;
		cnt.setLayoutData(rowDataCnt);
		cnt.setText(initValue);
		return cnt;
	}
}
