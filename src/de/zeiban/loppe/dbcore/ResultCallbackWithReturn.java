package de.zeiban.loppe.dbcore;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultCallbackWithReturn {

	Object doWithResultset(ResultSet rs) throws SQLException;

}
