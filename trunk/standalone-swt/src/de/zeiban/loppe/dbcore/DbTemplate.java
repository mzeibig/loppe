/**
 * 
 */
package de.zeiban.loppe.dbcore;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DbTemplate {
	final Connection connection;
	
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
			e1.printStackTrace();
		} finally {
			try {rs.close();} catch (final Exception ignore) {}
			try {stmt.close();} catch (final Exception ignore) {}
		}
	}
	
	public Object selectObject(final String sql, final ResultCallbackWithReturn resultCallback) {
		return selectObject(sql, null, resultCallback);
	}
	
	public Object selectObject(final String sql, final ParamProvider paramProvider, final ResultCallbackWithReturn resultCallback) {
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
			e1.printStackTrace();
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
			e1.printStackTrace();
			return false;
		} finally {
			try {rs.close();} catch (final Exception ignore) {}
			try {stmt.close();} catch (final Exception ignore) {}
		}
	}
	
	public void execute(final String sql) {
		Statement stmt = null;
		ResultSet rs = null;
		try {
			boolean b = stmt.execute(sql);
		} catch (final SQLException e1) {
			e1.printStackTrace();
		} finally {
			try {rs.close();} catch (final Exception ignore) {}
			try {stmt.close();} catch (final Exception ignore) {}
		}
	}	
}
