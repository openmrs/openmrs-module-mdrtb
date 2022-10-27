package org.openmrs.module.mdrtb;

import java.util.Locale;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.api.ConceptNameType;


public class MdrtbUtilTest {

	@Test
	public void getConceptName_shouldReturnCorrectConceptNameForLanguage() {
		Concept c = configureConceptWithNames();
		Assert.assertEquals("Spanish Name", MdrtbUtil.getConceptName(c, "es", ConceptNameType.FULLY_SPECIFIED).getName());
	}
	
	@Test
	public void getConceptName_shouldReturnCorrectShortNameForLanguage() {
		Concept c = configureConceptWithNames();		
		Assert.assertEquals("Span", MdrtbUtil.getConceptName(c, "es", ConceptNameType.SHORT).getName());
	}
	
	@Test
	@Ignore
	public void getConceptName_shouldGetConceptNameWithNullType() {
		Concept c = configureConceptWithNames();
		//TODO: Enable this later
		//Assert.assertEquals("No Type Spanish", MdrtbUtil.getConceptName(c, "es", null).getName());
	}
	
	@Test
	public void getConceptName_shouldReturnSomeNameOfMatchingTypeEvenIfNoNameForLocaleExists() {
		Concept c = configureConceptWithNames();
		ConceptName name = MdrtbUtil.getConceptName(c, "fr", ConceptNameType.SHORT);
		Assert.assertNotNull(name);
		Assert.assertEquals(ConceptNameType.SHORT, name.getConceptNameType());
	}
	
	@Test
	public void getConceptName_shouldReturnFullySpecifiedNameForLocaleIfNoNameMatchingTypeExists() {
		Concept c = configureConceptWithNames();
		ConceptName name = MdrtbUtil.getConceptName(c, "fr", ConceptNameType.INDEX_TERM);
		Assert.assertNotNull(name);
		Assert.assertEquals("French Name", name.getName());
	}
	
	@Test
	public void getConceptName_shouldReturnSomeFullySpecifedNameEvenIfNoMatchForLocaleOrTypeExists() {
		Concept c = configureConceptWithNames();
		ConceptName name = MdrtbUtil.getConceptName(c, "hit", ConceptNameType.INDEX_TERM);
		Assert.assertNotNull(name);
		Assert.assertEquals(ConceptNameType.FULLY_SPECIFIED, name.getConceptNameType());
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
		
		ConceptName spanishName = new ConceptName("Spanish Name", new Locale("es"));
		spanishName.setConceptNameType(ConceptNameType.FULLY_SPECIFIED);
		c.addName(spanishName);
		
		ConceptName shortSpanishName = new ConceptName("Span", new Locale("es"));
		shortSpanishName.setConceptNameType(ConceptNameType.SHORT);
		c.addName(shortSpanishName);
		
		c.addName(new ConceptName("No Type Spanish", new Locale("es")));
		
		ConceptName frenchName = new ConceptName("French Name", new Locale("fr"));
		frenchName.setConceptNameType(ConceptNameType.FULLY_SPECIFIED);
		c.addName(frenchName);
		
		return c;
	}
}
