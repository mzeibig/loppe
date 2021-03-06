/**
 * 
 */
package de.zeiban.loppe;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import de.zeiban.loppe.data.SummeGesamtInfoProvider;
import de.zeiban.loppe.data.Summenprovider;
import de.zeiban.loppe.dbcore.DbTemplate;
import de.zeiban.loppe.dbcore.ParamProvider;

final class ImportSelectionAdapter extends SelectionAdapter {
	
	private final Shell shell;
	private final Connection connection;
	private final Content content;
	private final Summenprovider summenprovider;
	
	public ImportSelectionAdapter(final Shell shell, final Connection connection, final Content content) {
		this.shell = shell;
		this.connection = connection;
		this.content = content;
		this.summenprovider = new SummeGesamtInfoProvider(connection);
	}
	
	@Override
	public void widgetSelected(final SelectionEvent e) {
		final String fileName = selectFile();
		if (fileName != null) {
			final MessageBox messageBox = new MessageBox(this.shell, SWT.ICON_QUESTION|SWT.YES|SWT.NO);
			messageBox.setMessage("Daten jetzt importieren ?");
			if (messageBox.open() == SWT.YES) {   
				BufferedReader reader = null;
				try {
					reader = new BufferedReader(new FileReader(new File(fileName)));
					String zeile = reader.readLine(); 
					while (zeile != null) {
						final String[] splitted = zeile.split(";");
						new DbTemplate(connection).execute("insert into kauf (inst, kunde, nummer, preis) values (?,?,?,?)", new ParamProvider() {
							public void fillParams(final PreparedStatement stmt) throws SQLException {
								stmt.setInt(1, Integer.valueOf(splitted[0]));
								stmt.setInt(2, Integer.valueOf(splitted[1]));
								stmt.setInt(3, Integer.valueOf(splitted[2]));
								DecimalFormat df = new DecimalFormat("###.##");
								df.setParseBigDecimal(true);
								try {
									stmt.setBigDecimal(4, (BigDecimal) df.parse(splitted[3]));
								} catch (ParseException e) {
									throw new RuntimeException(e);
								}
							}
						});
						zeile = reader.readLine();
					}
				} catch (final IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} finally {
					try {reader.close();} catch (final Exception ignore) {}
				}  				
				this.content.setSummeGesamt(summenprovider.getSumme());
			}
		}
	}

	private String selectFile() {
		final FileDialog dlg = new FileDialog(this.shell, SWT.OPEN);
		dlg.setFilterExtensions(new String[]{"*.csv", "*"});
		dlg.setFilterNames(new String[]{"Comma Separated Values", "Alle Dateien"});
		return dlg.open();
	}
}