/**
 * 
 */
package de.zeiban.loppe.dbcore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

public class DbTemplate {
	
	private final static Logger LOGGER = Logger.getLogger(DbTemplate.class);
	private final Connection connection;
	
	public DbTemplate(final Connection connection) {
		this.connection = connection;
	}
	
	public void select(final String sql, final ResultCallback resultCallback) {
		select(sql, null, resultCallback);
	}
	
	public void select(final String sql, final ParamProvider paramProvider, final ResultCallback resultCallback) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.prepareStatement(sql);
			if (paramProvider != null) {
				paramProvider.fillParams(stmt);
			}
			rs = stmt.executeQuery();
			resultCallback.doWithResultset(rs);
		} catch (final SQLException e1) {
			LOGGER.error("Fehler beim Datenbankzugriff:" + e1.getMessage(), e1);;
		} finally {
			try {rs.close();} catch (final Exception ignore) {}
			try {stmt.close();} catch (final Exception ignore) {}
		}
	}
	
	public <T> Object selectObject(final String sql, final ResultCallbackWithReturn<T> resultCallback) {
		return selectObject(sql, null, resultCallback);
	}
	
	public <T> T selectObject(final String sql, final ParamProvider paramProvider, final ResultCallbackWithReturn<T> resultCallback) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.prepareStatement(sql);
			if (paramProvider != null) {
				paramProvider.fillParams(stmt);
			}
			rs = stmt.executeQuery();
			return resultCallback.doWithResultset(rs);
		} catch (final SQLException e1) {
			LOGGER.error("Fehler beim Datenbankzugriff:" + e1.getMessage(), e1);;
			return null;
		} finally {
			try {rs.close();} catch (final Exception ignore) {}
			try {stmt.close();} catch (final Exception ignore) {}
		}
	}

	public boolean selectBoolean(final String sql, final ParamProvider paramProvider) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = connection.prepareStatement(sql);
			if (paramProvider != null) {
				paramProvider.fillParams(stmt);
			}
			rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getBoolean(1);
			}
			return false;
		} catch (final SQLException e1) {
			LOGGER.error("Fehler beim Datenbankzugriff:" + e1.getMessage(), e1);;
			return false;
		} finally {
			try {rs.close();} catch (final Exception ignore) {}
			try {stmt.close();} catch (final Exception ignore) {}
		}
	}
	
	public Integer selectInteger(final String sql) {
		return selectInteger(sql, null);
	}
	
	public Integer selectInteger(final String sql, final ParamProvider paramProvider) {
		return selectObject(sql, paramProvider, new ResultCallbackWithReturn<Integer>() {
			public Integer doWithResultset(ResultSet rs) throws SQLException {
				if (rs.next()) {
					return Integer.valueOf(rs.getInt(1));
				} else {
					return null;
				}
			}
		});
	}

	public boolean execute(final String sql) {
		PreparedStatement stmt = null;
		try {
			stmt = connection.prepareStatement(sql);
			return stmt.execute();
		} catch (final SQLException e1) {
			e1.printStackTrace();
			return false;
		} finally {
			try {stmt.close();} catch (final Exception ignore) {}
		}
	}	
}
