package de.zeiban.loppe.dbcore;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultCallbackWithReturn<T> {

	T doWithResultset(ResultSet rs) throws SQLException;

}
