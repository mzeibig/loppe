package de.zeiban.loppe.dbcore;

public interface DbOperations {

	<T> T selectObject(final String sql);

	<T> T selectObject(final String sql, final ParamProvider paramProvider);

	<T> T selectObject(final String sql, final ResultCallbackWithReturn<T> resultCallback);

	<T> T selectObject(final String sql, final ParamProvider paramProvider,	final ResultCallbackWithReturn<T> resultCallback);

	boolean selectBoolean(final String sql, final ParamProvider paramProvider);

	Integer selectInteger(final String sql);

	Integer selectInteger(final String sql, final ParamProvider paramProvider);

	void execute(final String sql, final ResultCallback resultCallback);
	
	void execute(final String sql, final ParamProvider paramProvider, final ResultCallback resultCallback);
	
	boolean execute(final String sql);

	boolean execute(final String sql, final ParamProvider paramProvider);

}