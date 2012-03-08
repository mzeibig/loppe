package de.zeiban.loppe;

import java.math.BigDecimal;
import java.math.BigInteger;
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
import org.eclipse.swt.widgets.Text;

public class HeaderSpaceAndTableComposite extends Composite implements KeyListener {
	private final List<Composite> rows = new ArrayList<Composite>();
	protected TopComposite topComposite;
	private Connection connection;
	private int inst = System.getProperty("user.name").hashCode();
	private Shell shell;
	
	public HeaderSpaceAndTableComposite(Composite parent, int style, Connection connection, Shell shell) {
		super(parent, style);
		this.connection = connection;
		this.shell = shell;
		addKeyListener(this);
		final RowLayout layout = new RowLayout(SWT.VERTICAL);
		this.setLayout(layout);
		this.topComposite = new TopComposite(this, connection);
		createPlatz(this);
		createLabel(this);
		rows.add(createRow(this));
		this.setSize(800, 600);
	}
	
	private void createPlatz(final Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);
		final RowLayout compositeLayout = new RowLayout(SWT.HORIZONTAL);
		composite.setLayout(compositeLayout);
		final Label kunde = new Label(composite, SWT.CENTER);
		final RowData rowData = new RowData();
		rowData.width = 100;
		kunde.setLayoutData(rowData);
		kunde.setText("");
	}

	private Composite createLabel(final Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);
		final RowLayout compositeLayout = new RowLayout(SWT.HORIZONTAL);
		compositeLayout.spacing = 15;
		composite.setLayout(compositeLayout);
		final Label nummer = new Label(composite, SWT.CENTER);
		final RowData rowData = new RowData();
		rowData.width = 200;
		nummer.setLayoutData(rowData);
		nummer.setText("Nummer");
		//nummer.addModifyListener(this);
		final Label preis = new Label(composite, SWT.CENTER);
		final RowData rowDataPreis = new RowData();
		rowDataPreis.width = 180;
		preis.setLayoutData(rowDataPreis);
		preis.setText("Preis in €");
		return composite;
	}
	
	private Composite createRow(final Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);
		final RowLayout compositeLayout = new RowLayout(SWT.HORIZONTAL);
		compositeLayout.spacing = 15;
		composite.setLayout(compositeLayout);
		final Text nummer = new Text(composite, SWT.BORDER);
		final RowData rowData = new RowData();
		rowData.width = 200;
		nummer.setLayoutData(rowData);
		nummer.setToolTipText("Nummer");
		nummer.addVerifyListener(new NumberVerifyer());
		nummer.addFocusListener(new BlackListCheckFocusAdapter(this.shell, this.connection));
		final Text preis = new Text(composite, SWT.BORDER);
		final RowData rowDataPreis = new RowData();
		rowDataPreis.width = 180;
		preis.setLayoutData(rowDataPreis);
		preis.addKeyListener(this);
		preis.setToolTipText("Preis in €");
		preis.addVerifyListener(new MoneyVerifyer());
		nummer.setFocus();
		return composite;
	}
	
	//    @Override
	public void keyPressed(final KeyEvent e) {
	}

	//    @Override
	public void keyReleased(final KeyEvent e) {
		if ((int)e.character  == 13 && e.stateMask == 0) {
			topComposite.zwischensumme.setText(NumberFormat.getCurrencyInstance().format(calculate(rows)));
			rows.add(createRow(this));
			this.pack(true);
			final ScrolledComposite sc = ((ScrolledComposite)this.getParent());
			sc.setOrigin(0, Integer.MAX_VALUE);
		} else if ((int)e.character  == 13 && e.stateMask == SWT.CTRL) {
			final BigDecimal summe = calculate(rows);
			final MessageBox messageBox = new MessageBox(shell, SWT.ICON_QUESTION|SWT.YES|SWT.NO|SWT.CANCEL);
			messageBox.setMessage("Summe: "+NumberFormat.getCurrencyInstance().format(summe)+"\nDaten übernehmen?");
			final int messageBoxAnswer = messageBox.open();
			if (messageBoxAnswer == SWT.YES) {
				new DataSaver(connection).saveValues(rows, topComposite.kundeCountInfo.getText(), inst);
				disposeRows();
				this.pack(true);
				topComposite.summeGesamtInfo.setText(new SummeGesamtInfoProvider(connection).getSumme());
				topComposite.kundeCountInfo.setText(new KundeCountProvider(connection).getNextKundeCount());
				rows.add(createRow(this));
				this.pack(true);
				topComposite.letzterKunde.setText(NumberFormat.getCurrencyInstance().format(summe));
				topComposite.zwischensumme.setText(NumberFormat.getCurrencyInstance().format(BigInteger.ZERO));
			}
			if (messageBoxAnswer == SWT.CANCEL) {
				final MessageBox confirmMessageBox = new MessageBox(shell, SWT.ICON_QUESTION|SWT.YES|SWT.NO);
				confirmMessageBox.setMessage("Alle Daten verwerfen ?");
				if (confirmMessageBox.open() == SWT.YES) {
					disposeRows();
					topComposite.kundeCountInfo.setText(new KundeCountProvider(connection).getNextKundeCount());
					rows.add(createRow(this));
					this.pack(true);
				}
			}
		}
	}

	private static BigDecimal calculate(final List<Composite> rows) {
		BigDecimal ergebnis = BigDecimal.ZERO;
		for (final Composite row : rows) {
			final String preisText = ((Text)row.getChildren()[1]).getText();
			if (preisText.length() == 0) continue;
			final BigDecimal wert = new BigDecimal(preisText);
			ergebnis = ergebnis.add(wert);
		}
		return ergebnis;
	}

	private void disposeRows() {
		for (final Composite row : rows) {
			row.dispose();
		}
		rows.clear();
	}

}
