package de.zeiban.loppe;

import java.math.BigDecimal;
import java.sql.Connection;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import de.zeiban.loppe.data.DataSaver;
import de.zeiban.loppe.data.KundeCountProvider;
import de.zeiban.loppe.data.Row;
import de.zeiban.loppe.data.SummeGesamtInfoProvider;

public class HeaderSpaceAndTableComposite extends Composite implements KeyListener, Content {
	private final List<Row> rows = new ArrayList<Row>();
	private final TopInfos topComposite;
	private final Connection connection;
	private final int inst = System.getProperty("user.name").hashCode();
	private final Shell shell;
	
	public HeaderSpaceAndTableComposite(final Composite parent, final int style, final Connection connection) {
		super(parent, style);
		this.connection = connection;
		this.shell = parent.getShell();
		addKeyListener(this);
		this.setLayout(new RowLayout(SWT.VERTICAL));
		this.topComposite = new TopComposite(this, connection);
		new SpaceComposite(this);
		new TableHeaderComposite(this);		
		final KeyListener keyListener = this;
		rows.add(new RowComposite(this.shell, this, keyListener));		
		this.setSize(800, 600);
	}
	
	private static class SpaceComposite extends Composite {
		SpaceComposite(final Composite parent) {
			super(parent, SWT.NONE);
			this.setLayout(new RowLayout(SWT.HORIZONTAL));
			final Label label = new Label(this, SWT.CENTER);
			label.setLayoutData(new RowData(100, SWT.DEFAULT));
			label.setText("");
		}
	}
	
	private static class TableHeaderComposite extends Composite {
		public TableHeaderComposite(final Composite parent) {
			super(parent, SWT.NONE);
			final RowLayout compositeLayout = new RowLayout(SWT.HORIZONTAL);
			compositeLayout.spacing = 15;
			this.setLayout(compositeLayout);
			final Label nummer = new Label(this, SWT.CENTER);
			nummer.setLayoutData(new RowData(200, SWT.DEFAULT));
			nummer.setText("Nummer");
			//nummer.addModifyListener(this);
			final Label preis = new Label(this, SWT.CENTER);
			preis.setLayoutData(new RowData(180, SWT.DEFAULT));
			preis.setText("Preis in €");
		}
	}
		
	//    @Override
	public void keyPressed(final KeyEvent e) {
	}

	//    @Override
	public void keyReleased(final KeyEvent e) {
		if (returnPressed(e)) {
			topComposite.setZwischensumme(calculate(rows));
			rows.add(new RowComposite(this.shell, this, this));
			this.pack(true);
			final ScrolledComposite sc = ((ScrolledComposite)this.getParent());
			sc.setOrigin(0, Integer.MAX_VALUE);
		} else if (ctrlReturnPressed(e)) {
			final BigDecimal summe = calculate(rows);
			final MessageBox messageBox = new MessageBox(shell, SWT.ICON_QUESTION|SWT.YES|SWT.NO|SWT.CANCEL);
			messageBox.setMessage("Summe: "+NumberFormat.getCurrencyInstance().format(summe)+"\nDaten übernehmen?");
			final int messageBoxAnswer = messageBox.open();
			if (messageBoxAnswer == SWT.YES) {
				new DataSaver(connection).saveValues(rows, topComposite.getKundeCountAsString(), inst);
				disposeRows();
				this.pack(true);
				topComposite.setSummeGesamt(new SummeGesamtInfoProvider(connection).getSumme());
				topComposite.setKundeCount(new KundeCountProvider(connection).getNextKundeCount());
				topComposite.setLetzterKunde(summe);
				topComposite.setZwischensumme(BigDecimal.ZERO);
				rows.add(new RowComposite(this.shell, this, this));
				this.pack(true);
			} else if (messageBoxAnswer == SWT.CANCEL) {
				final MessageBox confirmMessageBox = new MessageBox(shell, SWT.ICON_QUESTION|SWT.YES|SWT.NO);
				confirmMessageBox.setMessage("Alle Daten verwerfen ?");
				if (confirmMessageBox.open() == SWT.YES) {
					disposeRows();
					topComposite.setKundeCount(new KundeCountProvider(connection).getNextKundeCount());
					rows.add(new RowComposite(this.shell, this, this));
					this.pack(true);
				}
			}
		}
	}

	private static boolean ctrlReturnPressed(final KeyEvent e) {
		return (int)e.character  == 13 && e.stateMask == SWT.CTRL;
	}

	private static boolean returnPressed(final KeyEvent e) {
		return (int)e.character  == 13 && e.stateMask == 0;
	}

	private static BigDecimal calculate(final List<Row> rows) {
		BigDecimal ergebnis = BigDecimal.ZERO;
		for (final Row row : rows) {
			ergebnis = ergebnis.add(row.getPreis());
		}
		return ergebnis;
	}

	private void disposeRows() {
		for (final Row row : rows) {
			if (row instanceof RowComposite) {
				((RowComposite)row).dispose();
			}
		}
		rows.clear();
	}

	public void setSummeGesamt(final BigDecimal summe) {
		topComposite.setSummeGesamt(summe);
	}
	
	public void setKundeCount(int count) {
		topComposite.setKundeCount(count);
	}

}
