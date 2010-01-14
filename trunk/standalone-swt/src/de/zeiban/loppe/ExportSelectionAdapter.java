/**
 * 
 */
package de.zeiban.loppe;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

final class ExportSelectionAdapter extends SelectionAdapter {
	private final Shell shell;
	private final Connection connection;

	public ExportSelectionAdapter(Shell shell, Connection connection) {
		this.shell = shell;
		this.connection = connection;
	}

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
}