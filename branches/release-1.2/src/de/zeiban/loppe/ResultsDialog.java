package de.zeiban.loppe;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class ResultsDialog extends Dialog {
    
    private List<Result> data;

    public ResultsDialog(final Shell parent) {
        this(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
    }
    
    public ResultsDialog(final Shell parent, final int style) {
        super(parent, style);
        setText("Auswertung - Flohmarkt");
    }

    public void open(final List<Result> data) {
        this.data = data;
        final Shell shell = new Shell(getParent(), getStyle());
        final GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        shell.setLayout(layout);
        
        shell.setText(getText());
        final Table table = createContents(shell);
        createButtonAbspeichern(shell, shell);
        shell.pack();
        shell.setSize(500, 600);
        shell.open();
        final Display display = getParent().getDisplay();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }

    private Table createContents(final Shell shell) {
    	final ScrolledComposite sc = new ScrolledComposite(shell, SWT.BORDER|SWT.V_SCROLL);
        sc.setAlwaysShowScrollBars(false);
        //sc.setLayoutData(new GridData(GridData.FILL_BOTH));
        //sc.setBackgroundMode(SWT.INHERIT_DEFAULT);
        final Composite content = new Composite(sc, SWT.NONE);
        content.setLayout(new FillLayout());
        final Table table = new Table(content, SWT.NULL);
        table.setHeaderVisible(true);
//        TableColumn c1 = new TableColumn(table, SWT.NULL);
//        c1.setText("Index");
//        c1.setWidth(60);
        final TableColumn c2 = new TableColumn(table, SWT.RIGHT);
        c2.setText("Nummer");
        c2.setWidth(60);
        final TableColumn c3 = new TableColumn(table, SWT.RIGHT);
        c3.setText("Summe");
        c3.setWidth(60);
        final TableColumn c4 = new TableColumn(table, SWT.RIGHT);
        c4.setText("Kiga-Prozent");
        c4.setWidth(100);
        final TableColumn c5 = new TableColumn(table, SWT.RIGHT);
        c5.setText("Verkäufer-Prozent");
        c5.setWidth(120);
        final NumberFormat f = java.text.NumberFormat.getCurrencyInstance();
        int idx = 1;
        for (final Result result : data) {
            final TableItem ti = new TableItem(table, SWT.NULL);
            //ti.setText(String.valueOf(idx));
            ti.setText(0, String.valueOf(result.nummer));
            ti.setText(1, f.format(result.summe));
            ti.setText(2, f.format(result.prozKiga));
            ti.setText(3, f.format(result.prozSeller));
            idx++;
        }
        content.setSize(450, 500);
        sc.setContent(content);
        return table;
    }

    private void createButtonAbspeichern(final Composite parent, final Shell shell) {
        final Button export = new Button(parent, SWT.PUSH);
        export.setText("Als CSV Abspeichern");
        //export.setSize(30, 10);
        export.addSelectionListener(new SelectionAdapterExtension(shell));
    }
    
    
    private final class SelectionAdapterExtension extends SelectionAdapter {
		private final Shell shell;

		private SelectionAdapterExtension(Shell shell) {
			this.shell = shell;
		}

		@Override
		public void widgetSelected(final SelectionEvent e) {
		    System.out.println("BUTTON-Abspeichern");
		    final FileDialog dlg = new FileDialog(shell, SWT.SAVE);
		    dlg.setFilterExtensions(new String[]{"*.csv"});
		    final String fileName = dlg.open();
		    if (fileName != null) {
		    	final File file = new File(fileName);
		    	PrintWriter writer = null;
		    	try {
		    		writer = new PrintWriter(new BufferedWriter(new FileWriter(file)));
		    		for (final Result result: data) {
		    			final StringBuffer sb = new StringBuffer();
		    			sb.append(result.nummer).append(";");
		    			sb.append(result.prozKiga).append(";");
		    			sb.append(result.prozSeller).append(";");
		    			sb.append(result.summe);
		    			writer.println(sb.toString());
		    		}
		    	} catch (final IOException e1) {
		    		// TODO Auto-generated catch block
		    		e1.printStackTrace();
		    	} finally {
		    		try {writer.close();} catch (final Exception ignore) {}
		    	}  
		        
		    }
		}
	}

	static class Result {
        Integer nummer;
        BigDecimal summe;
        BigDecimal prozKiga;
        BigDecimal prozSeller;
    }

}
