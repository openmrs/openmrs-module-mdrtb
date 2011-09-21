package org.openmrs.module.mdrtb.web.taglib;

import java.util.Locale;

import junit.framework.Assert;


import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.api.ConceptNameType;



public class FormatTagTest {
	
	@Test
	public void formatTagFormatObjectMethod_shouldFormatConceptWithShortNamebyDefault() {
		FormatTag tag = new FormatTag();
		Concept c = configureConceptWithNames();
		Assert.assertEquals("Eng", tag.formatObject(c));
	}
	
	@Test
	public void formatTagFormatObjectMethod_shouldFormatConceptWithFullSpecifiedName() {
		FormatTag tag = new FormatTag();
		Concept c = configureConceptWithNames();
		
		tag.setNameType("fully_specified");
		Assert.assertEquals("English Name", tag.formatObject(c));
	}
	
	
	private Concept configureConceptWithNames() {
		Concept c = new Concept();
		
		ConceptName englishName = new ConceptName("English Name", new Locale("en"));
		englishName.setConceptNameType(ConceptNameType.FULLY_SPECIFIED);
		c.addName(englishName);
		
		ConceptName shortEnglishName = new ConceptName("Eng", new Locale("en"));
		shortEnglishName.setConceptNameType(ConceptNameType.SHORT);
		c.addName(shortEnglishName);
		
		c.addName(new ConceptName("No Type English", new Locale("en")));
		
		return c;
	}
}
