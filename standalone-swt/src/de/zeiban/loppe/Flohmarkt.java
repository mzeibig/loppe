package de.zeiban.loppe;

import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.Connection;
import java.sql.DriverManager;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import de.zeiban.loppe.properties.PropertyReader;


public class Flohmarkt implements KeyListener {

	private final class CocoaQuitListener implements Listener {
		public void handleEvent(Event event) {
			System.out.println("Quit");
			shell.close();
			try {connection.close();} catch (final Exception ignore){}
		}
	}

	private final class CocoaSettingsListener implements Listener {
		public void handleEvent(Event event) {
			System.out.println("Settings");
		}
	}

	private final class CocoaAboutListener implements Listener {
		public void handleEvent(Event event) {
			System.out.println("About");
		}
	}

	private Shell shell;
	private Composite content;
	private final List<Composite> rows = new ArrayList<Composite>();
	private Connection connection; 

	private TopComposite topComposite;
	private int inst;
	private BigDecimal loppeShare;

	public static void main(final String[] args) throws Exception {
		Class.forName("org.hsqldb.jdbcDriver");
 		Flohmarkt loppe = new Flohmarkt();
		loppe.readProzent();
		loppe.doit();
	}

	private static boolean isMac() {
        return SWT.getPlatform().equals("cocoa");
    }

	private void readProzent() {
        BigDecimal n = PropertyReader.getNumProperty("loppe.prozent", new BigDecimal(25));	
        loppeShare = n.divide(new BigDecimal(100));
	}

	private void doit() throws Exception {
		connection = DriverManager.getConnection("jdbc:hsqldb:file:testdb2;shutdown=true", "sa", "");
		inst = System.getProperty("user.name").hashCode();
		//        System.out.println(inst);
		Display.setAppName("Flohmarkt");
		final Display display = new Display();
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
		//createButtons(content);
		
		if ( isMac() ) {
	        CocoaUIEnhancer enhancer = new CocoaUIEnhancer("Flohmarkt");
	        enhancer.hookApplicationMenu(display, 
	        		new CocoaQuitListener(), 
	        		new CocoaAboutListener(), 
	        		new CocoaSettingsListener());
	    }
		
		//content.
		this.topComposite = new TopComposite(content, connection);
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
		try {connection.close();} catch (final Exception ignore){}
		resizeListener.dispose();
		display.dispose();
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
		auswertungItem.addSelectionListener(new AuswertungSelectionAdapter(parent, connection, loppeShare));
//		MenuItem blackListItem = new MenuItem(verwaltungMenu,SWT.NONE);
//		blackListItem.setText("Black-List pflegen");
//		MenuItem whiteListItem = new MenuItem(verwaltungMenu,SWT.NONE);
//		whiteListItem.setText("White-List pflegen");
		MenuItem infoItem = new MenuItem(verwaltungMenu,SWT.NONE);
		infoItem.setText("Info");	
		infoItem.addSelectionListener(new InfoSelectionAdapter(parent, loppeShare));
		if (!isMac()) {
			MenuItem exitItem = new MenuItem(verwaltungMenu,SWT.NONE);
			exitItem.setText("Exit");
			exitItem.addSelectionListener(new ExitSelectionAdapter(parent, connection));
		}
		Menu adminMenu = new Menu(menuBar);
		adminItem.setMenu(adminMenu);
		MenuItem exportItem = new MenuItem(adminMenu,SWT.NONE);
		exportItem.setText("Daten exportieren");
		exportItem.addSelectionListener(new ExportSelectionAdapter(shell, connection));
		MenuItem importItem = new MenuItem(adminMenu,SWT.NONE);
		importItem.setText("Daten importieren");
		importItem.addSelectionListener(new ExportSelectionAdapter(shell, connection));
		MenuItem dbresetItem = new MenuItem(adminMenu,SWT.NONE);
		dbresetItem.setText("Datenbank zurücksetzen");
		dbresetItem.addSelectionListener(new DBResetSelectionAdapter(shell, connection));		
		return menuBar;
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
		nummer.addFocusListener(new BlackListCheckFocusAdapter(this.shell, this.connection));
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
			BigDecimal zwSumme = new BigDecimal(0.0);
			for (Composite row: rows) {
				zwSumme = zwSumme.add(new BigDecimal(((Text) row.getChildren()[1]).getText()));
			}
			topComposite.zwischensumme.setText(NumberFormat.getCurrencyInstance().format(zwSumme));
			rows.add(createRow(content));
			content.pack(true);
			final ScrolledComposite sc = ((ScrolledComposite)content.getParent());
			sc.setOrigin(0, Integer.MAX_VALUE);
		} else if ((int)e.character  == 13 && e.stateMask == SWT.CTRL) {
			final BigDecimal summe = calculate(rows);
			final MessageBox messageBox = new MessageBox(shell, SWT.ICON_QUESTION|SWT.YES|SWT.NO|SWT.CANCEL);
			messageBox.setMessage("Summe: "+summe.toString()+"\nDaten übernehmen?");
			int messageBoxAnswer = messageBox.open();
			if (messageBoxAnswer == SWT.YES) {
				new DataSaver(connection).saveValues(rows, topComposite.kundeCountInfo.getText(), inst);
				disposeRows();
				content.pack(true);
				topComposite.summeGesamtInfo.setText(new SummeGesamtInfoProvider(connection).getSumme());
				topComposite.kundeCountInfo.setText(new KundeCountProvider(connection).getNextKundeCount());
				//                System.out.println("muss neue Zeile machen");
				rows.add(createRow(content));
				content.pack(true);
			}
			if (messageBoxAnswer == SWT.CANCEL) {
				final MessageBox confirmMessageBox = new MessageBox(shell, SWT.ICON_QUESTION|SWT.YES|SWT.NO);
				confirmMessageBox.setMessage("Alle Daten verwerfen ?");
				if (confirmMessageBox.open() == SWT.YES) {
					disposeRows();
					topComposite.kundeCountInfo.setText(new KundeCountProvider(connection).getNextKundeCount());
					rows.add(createRow(content));
					content.pack(true);
				}
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
