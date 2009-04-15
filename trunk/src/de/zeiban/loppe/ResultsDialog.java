package de.zeiban.loppe;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class ResultsDialog extends Dialog {
    
    private List<Result> data;

    public ResultsDialog(Shell parent) {
        this(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
    }
    
    
    
    public ResultsDialog(Shell parent, int style) {
        super(parent, style);
        setText("Auswertung - Flohmarkt");
    }



    public void open(List<Result> data) {
        this.data = data;
        Shell shell = new Shell(getParent(), getStyle());
        shell.setText(getText());
        createContents(shell);
        shell.pack();
        shell.setSize(500, 600);
        shell.open();
        Display display = getParent().getDisplay();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }

    private void createContents(final Shell shell) {
        shell.setLayout(new FillLayout());
        Table table = new Table(shell, SWT.NULL);
        table.setHeaderVisible(true);
        TableColumn c1 = new TableColumn(table, SWT.NULL);
        c1.setText("Index");
        c1.setWidth(60);
        TableColumn c2 = new TableColumn(table, SWT.RIGHT);
        c2.setText("Nummer");
        c2.setWidth(60);
        TableColumn c3 = new TableColumn(table, SWT.RIGHT);
        c3.setText("Summe");
        c3.setWidth(60);
        TableColumn c4 = new TableColumn(table, SWT.RIGHT);
        c4.setText("25-Prozent");
        c4.setWidth(100);
        TableColumn c5 = new TableColumn(table, SWT.RIGHT);
        c5.setText("75-Prozent");
        c5.setWidth(100);
        NumberFormat f = java.text.NumberFormat.getCurrencyInstance();
        int idx = 1;
        for (final Result result : data) {
            TableItem ti = new TableItem(table, SWT.NULL);
            ti.setText(String.valueOf(idx));
            ti.setText(1, String.valueOf(result.nummer));
            ti.setText(2, f.format(result.summe));
            ti.setText(3, f.format(result.proz25));
            ti.setText(4, f.format(result.proz75));
            idx++;
        }
    }
    
    static class Result {
        Integer nummer;
        BigDecimal summe;
        BigDecimal proz25;
        BigDecimal proz75;
    }

}
