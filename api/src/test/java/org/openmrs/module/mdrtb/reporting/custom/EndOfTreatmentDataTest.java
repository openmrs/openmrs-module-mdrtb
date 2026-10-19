package org.openmrs.module.mdrtb.reporting.custom;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class EndOfTreatmentDataTest {
	
	private static EndOfTreatmentData row(String identifier) {
		EndOfTreatmentData data = new EndOfTreatmentData();
		data.setIdentifier(identifier);
		return data;
	}
	
	@Test
	public void compareTo_shouldSortRowsByRegistrationNumber() {
		List<EndOfTreatmentData> rows = new ArrayList<>();
		rows.add(row("03-002"));
		rows.add(row("01-001"));
		rows.add(row("02-005"));
		Collections.sort(rows);
		assertEquals("01-001", rows.get(0).getIdentifier());
		assertEquals("02-005", rows.get(1).getIdentifier());
		assertEquals("03-002", rows.get(2).getIdentifier());
	}
	
	@Test
	public void compareTo_shouldTreatMissingRegistrationNumberAsEqual() {
		assertEquals(0, row(null).compareTo(row("01-001")));
		assertEquals(0, row("01-001").compareTo(row(null)));
	}
	
	@Test
	public void compareTo_shouldOrderDifferentRegistrationNumbers() {
		assertTrue(row("01-001").compareTo(row("01-002")) < 0);
		assertTrue(row("01-002").compareTo(row("01-001")) > 0);
	}
}
