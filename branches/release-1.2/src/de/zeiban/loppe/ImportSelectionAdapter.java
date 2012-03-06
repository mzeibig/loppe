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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

final class ImportSelectionAdapter extends SelectionAdapter {
	
	private final Shell shell;
	private final Connection connection;
	private final Label summeGesamt;
	private final Summenprovider summenprovider;
	
	public ImportSelectionAdapter(Shell shell, Connection connection, Label summeGesamt, Summenprovider summenprovider) {
		this.shell = shell;
		this.connection = connection;
		this.summeGesamt = summeGesamt;
		this.summenprovider = summenprovider;
	}
	
	@Override
	public void widgetSelected(final SelectionEvent e) {
		//                System.out.println("BUTTON-Import");
		final FileDialog dlg = new FileDialog(this.shell, SWT.OPEN);
		dlg.setFilterExtensions(new String[]{"*.csv"});
		dlg.setFilterNames(new String[]{"Comma Separated Values"});
		final String fileName = dlg.open();
		if (fileName != null) {
			final MessageBox messageBox = new MessageBox(this.shell, SWT.ICON_QUESTION|SWT.YES|SWT.NO);
			messageBox.setMessage("Daten jetzt importieren ?");
			if (messageBox.open() == SWT.YES) {   
				final File file = new File(fileName);
				//TODO: DbTemplate beutzen
				PreparedStatement stmt = null;
				BufferedReader reader = null;
				try {
					reader = new BufferedReader(new FileReader(file));
					final String s = "insert into kauf (inst, kunde, nummer, preis) values (?,?,?,?)";
					stmt = this.connection.prepareStatement(s);
					String zeile = reader.readLine(); 
					while (zeile != null) {
						final String[] splitted = zeile.split(";");
						//System.out.println(splitted);
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
				this.summeGesamt.setText(summenprovider.getSumme());
			}
		}
	}
}