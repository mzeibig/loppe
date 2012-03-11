package de.zeiban.loppe;

import java.math.BigDecimal;

public interface TopInfos {

	String getKundeCountAsString();

	void setKundeCount(String count);

	String getSummeGesamtAsString();

	void setSummeGesamt(String summe);

	void setZwischensumme(BigDecimal summe);

	void setLetzterKunde(BigDecimal summe);

}