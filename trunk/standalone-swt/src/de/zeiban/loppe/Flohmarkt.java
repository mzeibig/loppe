package de.zeiban.loppe;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import de.zeiban.loppe.ResultsDialog.Result;

public class Flohmarkt implements ModifyListener, KeyListener {

    private Shell shell;
    private Composite content;
    private final List<Composite> rows = new ArrayList<Composite>();
    private Connection connection; 
    private Image oldImage;
    private Color meinBlau;
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
        meinBlau = new Color(display, 51,102,255);
        shell = new Shell(display);
        shell.setText("Flohmarkt");
        shell.setLayout(new FillLayout());
        final ScrolledComposite sc = new ScrolledComposite(shell, SWT.BORDER|SWT.V_SCROLL);
        sc.setAlwaysShowScrollBars(true);
        sc.setBackgroundMode(SWT.INHERIT_DEFAULT);
        sc.addListener(SWT.Resize, new Listener() {
            public void handleEvent (final Event event) {
                final Rectangle rect = sc.getClientArea ();
                final Image newImage = new Image (display, Math.max (1, rect.width), 1);  
                final GC gc = new GC (newImage);
                gc.setForeground (display.getSystemColor (SWT.COLOR_WHITE));
                gc.setBackground (meinBlau);
                gc.fillGradientRectangle (rect.x, rect.y, rect.width, 1, false);
                gc.dispose ();
                sc.setBackgroundImage (newImage);
                if (oldImage != null) oldImage.dispose ();
                oldImage = newImage;
            }
        });
        content = new Composite(sc, SWT.NONE);
        content.addKeyListener(this);
        final RowLayout layout = new RowLayout(SWT.VERTICAL);
        content.setLayout(layout);
        //createButtonAuswertung(content);
        createButtons(content);
        summeGesamt = createInfo(content);
        updateSumme();
        kundeCount = createKundeInfo(content);
        updateKundeCount();
        createPlatz(content);
        createLabel(content);
        createRow(content);
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
        if (oldImage != null) oldImage.dispose ();
        meinBlau.dispose();
        display.dispose();
        try {connection.close();} catch (final Exception ignore){}
    }

    private void createButtonAuswertung(final Composite parent) {
        final Button auswertung = new Button(parent, SWT.PUSH);
        auswertung.setText("Auswertung");
        auswertung.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
//                System.out.println("BUTTON");
                final List<ResultsDialog.Result> testlist = new ArrayList<Result>();
                Statement stmt = null;
                ResultSet rs = null;
                try {
                    stmt = connection.createStatement();
                    rs = stmt.executeQuery("select nummer, sum(preis) from kauf group by nummer order by nummer");
                    while (rs.next()) {
                        final Result res = new Result();
                        res.nummer = rs.getInt("nummer");
                        final BigDecimal summe = rs.getBigDecimal(2);
                        res.summe = summe;
                        res.proz25 = summe.multiply(new BigDecimal(0.25));
                        res.proz75 = summe.multiply(new BigDecimal(0.75));
                        testlist.add(res);
                    }
                } catch (final SQLException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } finally {
                    try {rs.close();} catch (final Exception ignore) {}
                    try {stmt.close();} catch (final Exception ignore) {}
                }
                final List<ResultsDialog.Result> emptyList = Collections.emptyList();
                new ResultsDialog(shell, SWT.SHELL_TRIM).open(testlist);
            }
        });
    }
    
    private void createButtonExport(final Composite parent) {
        final Button export = new Button(parent, SWT.PUSH);
        export.setText("Export");
        export.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
//                System.out.println("BUTTON-Export");
                final FileDialog dlg = new FileDialog(shell, SWT.OPEN);
                dlg.setFilterExtensions(new String[]{"*.csv"});
                final String fileName = dlg.open();
                if (fileName != null) {
                    final MessageBox messageBox = new MessageBox(shell, SWT.ICON_QUESTION|SWT.YES|SWT.NO);
                    messageBox.setMessage("Daten jetzt exportieren ?");
                    if (messageBox.open() == SWT.YES) {   

                        final File file = new File(fileName);
                        Statement stmt = null;
                        ResultSet rs = null;
                        PrintWriter writer = null;
                        try {
                            writer = new PrintWriter(new BufferedWriter(new FileWriter(file)));
                            stmt = connection.createStatement();
                            rs = stmt.executeQuery("select inst, kunde, nummer, preis from kauf");
                            while (rs.next()) {
                                final int inst = rs.getInt("inst");
                                final int kunde = rs.getInt("kunde");
                                final int nummer = rs.getInt("nummer");
                                final BigDecimal preis = rs.getBigDecimal("preis");
                                final StringBuffer sb = new StringBuffer();
                                sb.append(inst).append(";").append(kunde).append(";").append(nummer)
                                .append(";").append(preis);
//                                System.out.println(sb.toString());
                                writer.println(sb.toString());
                            }
                        } catch (final IOException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        } catch (final SQLException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        } finally {
                            try {rs.close();} catch (final Exception ignore) {}
                            try {stmt.close();} catch (final Exception ignore) {}
                            try {writer.close();} catch (final Exception ignore) {}
                        }  
                    }
                }
            }
        });
    }
    
    private void createButtonImport(final Composite parent) {
        final Button importb = new Button(parent, SWT.PUSH);
        importb.setText("Import");
        importb.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
//                System.out.println("BUTTON-Import");
                final FileDialog dlg = new FileDialog(shell, SWT.OPEN);
                dlg.setFilterExtensions(new String[]{"*.csv"});
                final String fileName = dlg.open();
                if (fileName != null) {
                    final MessageBox messageBox = new MessageBox(shell, SWT.ICON_QUESTION|SWT.YES|SWT.NO);
                    messageBox.setMessage("Daten jetzt importieren ?");
                    if (messageBox.open() == SWT.YES) {   
                        final File file = new File(fileName);
                        PreparedStatement stmt = null;
                        BufferedReader reader = null;
                        try {
                            reader = new BufferedReader(new FileReader(file));
                            final String s = "insert into kauf (inst, kunde, nummer, preis) values (?,?,?,?)";
                            stmt = connection.prepareStatement(s);
                            String zeile = reader.readLine(); 
                            while (zeile != null) {
                                final String[] splitted = zeile.split(";");
                                System.out.println(splitted);
                                stmt.setInt(1,Integer.valueOf(splitted[0]));
                                stmt.setInt(2, Integer.valueOf(splitted[1]));
                                stmt.setInt(3, Integer.valueOf(splitted[2]));
                                stmt.setBigDecimal(4, new BigDecimal(splitted[3]));
                                stmt.execute();
                                zeile = reader.readLine();
                            }
                        } catch (final IOException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        } catch (final SQLException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        } finally {
                            try {stmt.close();} catch (final Exception ignore) {}
                            try {reader.close();} catch (final Exception ignore) {}
                        }  
                        updateSumme();
                    }
                }
            }
        });
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
        //nummer.addModifyListener(this);
        final Text preis = new Text(composite, SWT.BORDER);
        final RowData rowDataPreis = new RowData();
        rowDataPreis.width = 80;
        preis.setLayoutData(rowDataPreis);
        //preis.addModifyListener(this);
        preis.addKeyListener(this);
        preis.setToolTipText("Preis in â‚¬");
        preis.addVerifyListener(new MoneyVerifyer());
        rows.add(composite);
        nummer.setFocus();
        return composite;
    }

//    @Override
    public void modifyText(final ModifyEvent e) {
//        System.out.println(e);
//        System.out.println(e.widget);
//        System.out.println(((Text)e.widget).getText());
    }

//    @Override
    public void keyPressed(final KeyEvent e) {
//        System.out.println(e.keyCode);
    }

//    @Override
    public void keyReleased(final KeyEvent e) {
        if ((int)e.character  == 13 && e.stateMask == 0) {
            //System.out.println("neue Zeile!");
            createRow(content);
            content.pack(true);
            final ScrolledComposite sc = ((ScrolledComposite)content.getParent());
            sc.setOrigin(0, Integer.MAX_VALUE);
        } else if ((int)e.character  == 13 && e.stateMask == SWT.CTRL) {
            final BigDecimal summe = calculate();
            final MessageBox messageBox = new MessageBox(shell, SWT.ICON_QUESTION|SWT.YES|SWT.NO);
            messageBox.setMessage("Daten übernehmen?\n" + summe.toString());
            if (messageBox.open() == SWT.YES) {
                saveValues();
                disposeTexts();
                updateSumme();
                updateKundeCount();
//                System.out.println("muss neue Zeile machen");
                createRow(content);
                content.pack(true);
            }
        }
    }
    
    private BigDecimal calculate() {
        BigDecimal ergebnis = BigDecimal.ZERO;
        for (final Composite row : rows) {
            final String preisText = ((Text)row.getChildren()[1]).getText();
            if (preisText.length() == 0) continue;
            final BigDecimal wert = new BigDecimal(preisText);
            ergebnis = ergebnis.add(wert);
        }
        return ergebnis;
    }
    
    private void disposeTexts() {
        for (final Composite row : rows) {
            row.dispose();
        }
        rows.clear();
    }
    
    private void updateSumme() {
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = connection.createStatement();
            rs = stmt.executeQuery("select sum(preis) from kauf");
            rs.next();
            final BigDecimal gessum = rs.getBigDecimal(1);
            summeGesamt.setText(gessum == null ? "0.00" : gessum.toString());

        } catch (final SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {stmt.close();} catch (final Exception ignore) {}
        } 
    }
    
    private void updateKundeCount() {
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = connection.createStatement();
            rs = stmt.executeQuery("select max(kunde) from kauf");
            int count;
            if (!rs.next()) count = 1;
            else count=rs.getInt(1);
            kundeCount.setText(String.valueOf(count+1));

        } catch (final SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {stmt.close();} catch (final Exception ignore) {}
        } 
    }    
    
    private void saveValues() {
        PreparedStatement stmt = null;
        try {
            stmt = connection.prepareStatement("insert into kauf values(?, ?, ?, ?)");
            for (final Composite row : rows) {
                final String textPreis = ((Text)row.getChildren()[1]).getText();
                final String textNummer = ((Text)row.getChildren()[0]).getText();
                final String textKunde = kundeCount.getText();
                if (textPreis.length() == 0 || textNummer.length() == 0) continue;
                System.out.println(textKunde + ":" + textNummer + ":" + textPreis);
                stmt.setInt(1, inst);
                stmt.setInt(2, Integer.parseInt(textKunde));
                stmt.setInt(3, Integer.parseInt(textNummer));
                stmt.setBigDecimal(4, new BigDecimal(textPreis));
                stmt.execute();
            }
        } catch (final SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {stmt.close();} catch (final Exception ignore) {}
        }
    }
}
