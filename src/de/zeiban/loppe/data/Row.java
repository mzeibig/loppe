package de.zeiban.loppe.data;

import java.math.BigDecimal;

public interface Row {

	boolean isEmpty();

	String getNummerAsString();

	int getVerkaeuferNummer();

	String getPreisAsString();

	BigDecimal getPreis();

}