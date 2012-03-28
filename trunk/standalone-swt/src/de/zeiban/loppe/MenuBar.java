package de.zeiban.loppe;

import java.math.BigDecimal;
import java.sql.Connection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;


public class MenuBar {
	final private Menu menuBar;

	public MenuBar(final Shell shell, final Content content, final Connection connection, final BigDecimal loppeShare) {
		menuBar = new Menu(shell, SWT.BAR);
		new VerwaltungMenu(menuBar, connection, loppeShare);
		new AdminMenu(menuBar, connection, content);
		if (!SWT.getPlatform().equals("cocoa")) {
			new HilfeMenu(menuBar);
		}
	}

	public Menu menuBarInstance() {
		return menuBar;
	}

	static class HilfeMenu {
		final MenuItem hilfeItem;
		public HilfeMenu(final Menu menuBar) {
			final Shell shell = menuBar.getShell();
			hilfeItem = new MenuItem(menuBar, SWT.CASCADE);
			hilfeItem.setText("Hilfe");
			Menu hilfeMenu = new Menu(menuBar);
			hilfeItem.setMenu(hilfeMenu);
			MenuItem aboutItem = new MenuItem(hilfeMenu, SWT.NONE);
			aboutItem.setText("About...");
			aboutItem.addSelectionListener(new SelectionAdapter() {
				
				public void widgetSelected(SelectionEvent e) {
					new AboutDialog(shell).open(); 
				}
			});
		}
	}
	
	static class VerwaltungMenu {
		final MenuItem verwaltungItem;
		public VerwaltungMenu(final Menu menuBar, final Connection connection, final BigDecimal loppeShare) {
			final Shell shell = menuBar.getShell();
			verwaltungItem = new MenuItem(menuBar, SWT.CASCADE);
			verwaltungItem.setText("Verwaltung");
			final Menu verwaltungMenu = new Menu(menuBar);
			verwaltungItem.setMenu(verwaltungMenu);
			final MenuItem auswertungItem = new MenuItem(verwaltungMenu, SWT.NONE);
			auswertungItem.setText("Auswertung");
			auswertungItem.addSelectionListener(new AuswertungSelectionAdapter(shell, connection, loppeShare));
			// MenuItem blackListItem = new MenuItem(verwaltungMenu,SWT.NONE);
			// blackListItem.setText("Black-List pflegen");
			// MenuItem whiteListItem = new MenuItem(verwaltungMenu,SWT.NONE);
			// whiteListItem.setText("White-List pflegen");
			final MenuItem infoItem = new MenuItem(verwaltungMenu, SWT.NONE);
			infoItem.setText("Info");
			infoItem.addSelectionListener(new InfoSelectionAdapter(shell, loppeShare));
			if (!isMac()) {
				final MenuItem exitItem = new MenuItem(verwaltungMenu, SWT.NONE);
				exitItem.setText("Exit");
				exitItem.addSelectionListener(new ExitSelectionAdapter(shell, connection));
			}
		}
		
		private static boolean isMac() {
			return SWT.getPlatform().equals("cocoa");
		}
	}
	
	static class AdminMenu {
		private final Menu adminMenu;
		private final MenuItem adminItem;
		public AdminMenu(final Menu menuBar, final Connection connection, final Content content) {
			final Shell shell = menuBar.getShell();
			adminItem = new MenuItem(menuBar, SWT.CASCADE);
			adminItem.setText("Admin");
			adminMenu = new Menu(menuBar);
			adminItem.setMenu(adminMenu);
			final MenuItem exportItem = new MenuItem(adminMenu, SWT.NONE);
			exportItem.setText("Daten exportieren");
			exportItem.addSelectionListener(new ExportSelectionAdapter(shell, connection));
			final MenuItem importItem = new MenuItem(adminMenu, SWT.NONE);
			importItem.setText("Daten importieren");
			importItem.addSelectionListener(new ImportSelectionAdapter(shell, connection, content));
			final MenuItem dbresetItem = new MenuItem(adminMenu, SWT.NONE);
			dbresetItem.setText("Datenbank zurücksetzen");
			dbresetItem.addSelectionListener(new DBResetSelectionAdapter(shell, connection, content));
		}
	}
}
