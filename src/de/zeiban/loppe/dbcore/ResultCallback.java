package de.zeiban.loppe.dbcore;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultCallback {

	void doWithResultset(ResultSet rs) throws SQLException;

}
