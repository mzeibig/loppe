package de.zeiban.loppe;

import java.math.BigDecimal;

public interface TopInfos {

	String getKundeCountAsString();

	void setKundeCount(int count);

	String getSummeGesamtAsString();

	void setSummeGesamt(BigDecimal summe);

	void setZwischensumme(BigDecimal summe);

	void setLetzterKunde(BigDecimal summe);

}