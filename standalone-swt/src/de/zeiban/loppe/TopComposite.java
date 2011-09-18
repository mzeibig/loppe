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

	public TopComposite(final Composite parent, final Connection connection) {
		super(parent, SWT.BORDER);
		final RowLayout topCompositeLayout = new RowLayout(SWT.HORIZONTAL);
		this.setLayout(topCompositeLayout);
		summeGesamtInfo = createSummeGesamtInfoLabel(this);
		summeGesamtInfo.setText(new SummeGesamtInfoProvider(connection).getSumme());
		kundeCountInfo = createKundeCountInfoLabel(this);
		kundeCountInfo.setText(new KundeCountProvider(connection).getNextKundeCount());
	}

	private Label createSummeGesamtInfoLabel(final Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);
		final RowLayout compositeLayout = new RowLayout(SWT.HORIZONTAL);
		composite.setLayout(compositeLayout);
		final Label summe = new Label(composite, SWT.NONE);
		final RowData rowDataPreis = new RowData();
		rowDataPreis.width = 80;
		summe.setLayoutData(rowDataPreis);
		summe.setText("Summe:");
		final Label summecnt = new Label(composite, SWT.CENTER);
		final RowData rowDataSummecnt = new RowData();
		rowDataSummecnt.width = 80;
		summecnt.setLayoutData(rowDataSummecnt);
		summecnt.setText("0.00");
		return summecnt;
	}

	private Label createKundeCountInfoLabel(final Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);
		final RowLayout compositeLayout = new RowLayout(SWT.HORIZONTAL);
		composite.setLayout(compositeLayout);
		final Label kunde = new Label(composite, SWT.CENTER);
		final RowData rowDataPreis = new RowData();
		rowDataPreis.width = 100;
		kunde.setLayoutData(rowDataPreis);
		kunde.setText("Anzahl Kunden:");
		final Label kundecnt = new Label(composite, SWT.CENTER);
		final RowData rowDataKundecnt = new RowData();
		rowDataKundecnt.width = 80;
		kundecnt.setLayoutData(rowDataKundecnt);
		kundecnt.setText("0");
		return kundecnt;
	}    

}
