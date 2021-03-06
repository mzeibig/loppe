package de.zeiban.loppe;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import de.zeiban.loppe.dbcore.DbOperations;
import de.zeiban.loppe.dbcore.DbTemplate;
import de.zeiban.loppe.properties.PropertyReader;


public class Flohmarkt {

	private static final String DBNAME = "loppedb";
	private static final String URL = "jdbc:hsqldb:file:" + DBNAME + ";shutdown=true";

	private final class CocoaQuitListener implements Listener {
		public void handleEvent(final Event event) {
			shell.close();
			try {connection.close();} catch (final Exception ignore){}
		}
	}

	private final class CocoaSettingsListener implements Listener {
		public void handleEvent(final Event event) {
		}
	}

	private final class CocoaAboutListener implements Listener {
		Shell shell;
		public CocoaAboutListener(Shell shell) {
			this.shell = shell;
		}
		public void handleEvent(final Event event) {
			new AboutDialog(this.shell).open(); 
		}
	}

	private Shell shell;
	private HeaderSpaceAndTableComposite content;
	private Connection connection; 
	private BigDecimal loppeShare;

	public static void main(final String[] args) throws Exception {
		Class.forName("org.hsqldb.jdbcDriver");
 		final Flohmarkt loppe = new Flohmarkt();
		loppe.readProzent();
		loppe.doit();
	}

	private static boolean isMac() {
        return SWT.getPlatform().equals("cocoa");
    }

	private void readProzent() {
        final BigDecimal n = PropertyReader.getNumProperty("loppe.prozent", new BigDecimal(25));	
        loppeShare = n.divide(new BigDecimal(100));
	}

	private void doit() throws Exception {
		connection = DriverManager.getConnection(URL, "sa", "");
		initDb(connection);
		Display.setAppName("Flohmarkt");
		final Display display = new Display();
		final Image icon = new Image(display, Flohmarkt.class.getResourceAsStream("cart-icon.png"));
		shell = new Shell(display);
		shell.setText("Flohmarkt");
		shell.setImage(icon);
		//shell.setAlpha(200);
		shell.setLayout(new FillLayout());
		final ScrolledComposite sc = new ScrolledComposite(shell, SWT.BORDER|SWT.V_SCROLL);
		sc.setAlwaysShowScrollBars(true);
		sc.setBackgroundMode(SWT.INHERIT_DEFAULT);
//		final ResizeListener resizeListener = new ResizeListener(display, sc);
//		sc.addListener(SWT.Resize, resizeListener);
		content = new HeaderSpaceAndTableComposite(sc, SWT.NONE, connection);
		//createButtons(content);
		
		if ( isMac() ) {
	        final CocoaUIEnhancer enhancer = new CocoaUIEnhancer("Flohmarkt");
	        enhancer.hookApplicationMenu(display, 
	        		new CocoaQuitListener(), 
	        		new CocoaAboutListener(shell), 
	        		new CocoaSettingsListener());
	    }
		sc.setContent(content);

		shell.setMenuBar(new MenuBar(shell, content, connection, loppeShare).menuBarInstance());
		shell.pack();
		shell.setSize(800, 600);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		try {connection.close();} catch (final Exception ignore){}
		icon.dispose();
//		resizeListener.dispose();
		display.dispose();
	}

	private void initDb(Connection connection) {
		DbOperations db = new DbTemplate(connection);
		Integer tabzahl = 
				db.selectInteger(
						"SELECT count(*) " +
						"FROM information_schema.system_tables " +
						"WHERE table_schem = 'PUBLIC' AND table_name = 'KAUF'");
		if (tabzahl==0) {
			db.execute("CREATE TABLE KAUF(INST INTEGER,KUNDE INTEGER,NUMMER INTEGER,PREIS DECIMAL)");
		}
	}
	
}
