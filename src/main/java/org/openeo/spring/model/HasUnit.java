package org.openeo.spring.model;

/**
 * Tagging interface to bundle together dimensions which allow a unit of measure.
 *
 */
public interface HasUnit {

	/** Unit getter. */
	String getUnit();

	/** Unit setter. */
	void setUnit(String unit);

	static <T> boolean is(T obj) {
		return obj instanceof HasUnit;
	}
}
