package de.zeiban.loppe;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


public class Flohmarkt implements KeyListener {

	private Shell shell;
	private Composite content;
	private final List<Composite> rows = new ArrayList<Composite>();
	private Connection connection; 

	private Label summeGesamt;
	private Label kundeCount;
	private int inst;

	public static void main(final String[] args) throws Exception {
		Class.forName("org.hsqldb.jdbcDriver");
		new Flohmarkt().doit();
	}

	private void doit() throws Exception {
		connection = DriverManager.getConnection("jdbc:hsqldb:file:testdb2", "sa", "");
		inst = System.getProperty("user.name").hashCode();
		//        System.out.println(inst);
		final Display display = new Display();
		Display.setAppName("Flohmarkt");
		shell = new Shell(display);
		shell.setText("Flohmarkt");
		//shell.setAlpha(200);
		shell.setLayout(new FillLayout());
		Menu menu = createMenu(shell);
		shell.setMenuBar(menu);
		final ScrolledComposite sc = new ScrolledComposite(shell, SWT.BORDER|SWT.V_SCROLL);
		sc.setAlwaysShowScrollBars(true);
		sc.setBackgroundMode(SWT.INHERIT_DEFAULT);
		ResizeListener resizeListener = new ResizeListener(display, sc);
		sc.addListener(SWT.Resize, resizeListener);
		content = new Composite(sc, SWT.NONE);
		content.addKeyListener(this);
		final RowLayout layout = new RowLayout(SWT.VERTICAL);
		content.setLayout(layout);
		createButtons(content);
		summeGesamt = createInfo(content);

		summeGesamt.setText(new Summer(connection).getSumme());
		kundeCount = createKundeInfo(content);
		kundeCount.setText(new KundenNummerProvider(connection).getNextKundeCount());
		createPlatz(content);
		createLabel(content);
		rows.add(createRow(content));
		content.setSize(800, 600);
		sc.setContent(content);

		shell.pack();
		shell.setSize(800, 600);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		resizeListener.dispose();
		display.dispose();
		try {connection.close();} catch (final Exception ignore){}
	}

	private void createButtonAuswertung(final Composite parent) {
		final Button auswertung = new Button(parent, SWT.PUSH);
		auswertung.setText("Auswertung");
		auswertung.addSelectionListener(new AuswertungSelectionAdapter(shell, connection));
	}

	private void createButtonExport(final Composite parent) {
		final Button export = new Button(parent, SWT.PUSH);
		export.setText("Export");
		export.addSelectionListener(new ExportSelectionAdapter(shell, connection));
	}

	private void createButtonImport(final Composite parent) {
		final Button importb = new Button(parent, SWT.PUSH);
		importb.setText("Import");
		importb.addSelectionListener(new ImportSelectionAdapter(shell, connection, summeGesamt, new Summer(connection)));
	}

	private Menu createMenu(final Shell parent) {
		Menu menuBar = new Menu (parent, SWT.BAR);
		MenuItem verwaltungItem = new MenuItem(menuBar,SWT.CASCADE);
		verwaltungItem.setText("Verwaltung");
		MenuItem adminItem = new MenuItem(menuBar,SWT.CASCADE);
		adminItem.setText("Admin");
		MenuItem hilfeItem = new MenuItem(menuBar,SWT.CASCADE);
		hilfeItem.setText("Hilfe");

		Menu verwaltungMenu = new Menu(menuBar);
		verwaltungItem.setMenu(verwaltungMenu);
		MenuItem auswertungItem = new MenuItem(verwaltungMenu,SWT.NONE);
		auswertungItem.setText("Auswertung");
		auswertungItem.addSelectionListener(new AuswertungSelectionAdapter(parent, connection));
		MenuItem blackListItem = new MenuItem(verwaltungMenu,SWT.NONE);
		blackListItem.setText("Black-List pflegen");
		MenuItem whiteListItem = new MenuItem(verwaltungMenu,SWT.NONE);
		whiteListItem.setText("White-List pflegen");
		MenuItem exitItem = new MenuItem(verwaltungMenu,SWT.NONE);
		exitItem.setText("Exit");

		Menu adminMenu = new Menu(menuBar);
		adminItem.setMenu(adminMenu);
		MenuItem exportItem = new MenuItem(adminMenu,SWT.NONE);
		exportItem.setText("Daten exportieren");
		exportItem.addSelectionListener(new ExportSelectionAdapter(shell, connection));
		MenuItem importItem = new MenuItem(adminMenu,SWT.NONE);
		importItem.setText("Daten importieren");
		importItem.addSelectionListener(new ExportSelectionAdapter(shell, connection));
		//shell.setMenuBar(menuBar);
		return menuBar;

	}

	private Label createInfo(final Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);
		final RowLayout compositeLayout = new RowLayout(SWT.HORIZONTAL);
		composite.setLayout(compositeLayout);
		final Label summe = new Label(composite, SWT.CENTER);
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

	private Label createKundeInfo(final Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);
		final RowLayout compositeLayout = new RowLayout(SWT.HORIZONTAL);
		composite.setLayout(compositeLayout);
		final Label kunde = new Label(composite, SWT.CENTER);
		final RowData rowDataPreis = new RowData();
		rowDataPreis.width = 80;
		kunde.setLayoutData(rowDataPreis);
		kunde.setText("Kunde:");
		final Label kundecnt = new Label(composite, SWT.CENTER);
		final RowData rowDataKundecnt = new RowData();
		rowDataKundecnt.width = 80;
		kundecnt.setLayoutData(rowDataKundecnt);
		kundecnt.setText("0");
		return kundecnt;
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

	private void createButtons(final Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);
		final RowLayout compositeLayout = new RowLayout(SWT.HORIZONTAL);
		composite.setLayout(compositeLayout);
		createButtonAuswertung(composite);
		createButtonExport(composite);
		createButtonImport(composite);
	}

	private Composite createLabel(final Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);
		final RowLayout compositeLayout = new RowLayout(SWT.HORIZONTAL);
		composite.setLayout(compositeLayout);
		final Label nummer = new Label(composite, SWT.CENTER);
		final RowData rowData = new RowData();
		rowData.width = 100;
		nummer.setLayoutData(rowData);
		nummer.setText("Nummer");
		//nummer.addModifyListener(this);
		final Label preis = new Label(composite, SWT.CENTER);
		final RowData rowDataPreis = new RowData();
		rowDataPreis.width = 80;
		preis.setLayoutData(rowDataPreis);
		preis.setText("Preis");
		return composite;
	}

	private Composite createRow(final Composite parent) {
		//        System.out.println("createRow");
		final Composite composite = new Composite(parent, SWT.NONE);
		final RowLayout compositeLayout = new RowLayout(SWT.HORIZONTAL);
		composite.setLayout(compositeLayout);
		final Text nummer = new Text(composite, SWT.BORDER);
		final RowData rowData = new RowData();
		rowData.width = 100;
		nummer.setLayoutData(rowData);
		nummer.setToolTipText("Nummer");
		nummer.addVerifyListener(new NumberVerifyer());
		final Text preis = new Text(composite, SWT.BORDER);
		final RowData rowDataPreis = new RowData();
		rowDataPreis.width = 80;
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
			//System.out.println("neue Zeile!");
			rows.add(createRow(content));
			content.pack(true);
			final ScrolledComposite sc = ((ScrolledComposite)content.getParent());
			sc.setOrigin(0, Integer.MAX_VALUE);
		} else if ((int)e.character  == 13 && e.stateMask == SWT.CTRL) {
			final BigDecimal summe = calculate(rows);
			final MessageBox messageBox = new MessageBox(shell, SWT.ICON_QUESTION|SWT.YES|SWT.NO);
			messageBox.setMessage("Daten übernehmen?\n" + summe.toString());
			if (messageBox.open() == SWT.YES) {
				new DataSaver(connection).saveValues(rows, kundeCount.getText(), inst);
				disposeRows();
				content.pack(true);
				summeGesamt.setText(new Summer(connection).getSumme());
				kundeCount.setText(new KundenNummerProvider(connection).getNextKundeCount());
				//                System.out.println("muss neue Zeile machen");
				rows.add(createRow(content));
				content.pack(true);
			}
		}
	}

	private static BigDecimal calculate(List<Composite> rows) {
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
